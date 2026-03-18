package com.example.idempotentapi.service;

import com.example.idempotentapi.dto.OperationResponse;
import com.example.idempotentapi.model.User;
import com.example.idempotentapi.quartz.AsyncCreateUserJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

@Service
public class UserService {

    private static final String IDEM_CACHE = "idempotencyKeys";

    private final CacheManager cacheManager;
    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;

    public UserService(CacheManager cacheManager, Scheduler scheduler, ObjectMapper objectMapper) {
        this.cacheManager = cacheManager;
        this.scheduler = scheduler;
        this.objectMapper = objectMapper;
    }

    // Legacy method without idempotency
    public OperationResponse create(User user) {
        // Dummy response. Replace with actual Legacy API call and response handling.
        return OperationResponse.success("User created", user); 
    }

    public OperationResponse createIdem(User user) {
        String key = "idem:hash:" + hashUser(user);
        if (idempotencyCache().putIfAbsent(key, Boolean.TRUE) != null) {
            return OperationResponse.error("User exists");
        }
        return create(user);//calling legacy method
    }

    public OperationResponse createWithKey(User user, String idemKey) {
        String key = "idem:key:" + idemKey;
        if (idempotencyCache().putIfAbsent(key, Boolean.TRUE) != null) {
            return OperationResponse.error("User exists");
        }
        return create(user);//calling legacy method
    }

    public OperationResponse createAsync(User user, String idemKey) {
        String jobName = "async-user-create-" + idemKey;
        String triggerName = "async-user-create-trigger-" + idemKey;

        try {
            JobDetail existing = scheduler.getJobDetail(org.quartz.JobKey.jobKey(jobName, "user-create"));
            if (existing != null) {
                return OperationResponse.error("User creation in progress. Please wait.");
            }

            List<?> runningJobs = scheduler.getCurrentlyExecutingJobs().stream()
                    .filter(job -> job.getJobDetail().getKey().getName().equals(jobName))
                    .toList();
            if (!runningJobs.isEmpty()) {
                return OperationResponse.error("User creation in progress. Please wait until websocket message.");
            }

            JobDataMap dataMap = new JobDataMap();
            dataMap.put("idemKey", idemKey);
            dataMap.put("userJson", objectMapper.writeValueAsString(user));

            JobDetail jobDetail = JobBuilder.newJob(AsyncCreateUserJob.class)
                    .withIdentity(jobName, "user-create")
                    .usingJobData(dataMap)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(TriggerKey.triggerKey(triggerName, "user-create"))
                    .forJob(jobDetail)
                    .startAt(java.util.Date.from(Instant.now().plusSeconds(5)))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            return OperationResponse.success("User creation scheduled ( in 10 seconds )", user);
        } catch (SchedulerException ex) {
            return OperationResponse.error("Failed to schedule async create: " + ex.getMessage());
        } catch (Exception ex) {
            return OperationResponse.error("Failed to serialize request: " + ex.getMessage());
        }
    }

    private String hashUser(User user) {
        try {
            String normalized = objectMapper.writeValueAsString(user);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash user", ex);
        }
    }

    private Cache idempotencyCache() {
        Cache cache = cacheManager.getCache(IDEM_CACHE);
        if (cache == null) {
            throw new IllegalStateException("Missing cache: " + IDEM_CACHE);
        }
        return cache;
    }
}
