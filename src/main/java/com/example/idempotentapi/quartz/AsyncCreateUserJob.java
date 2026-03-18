package com.example.idempotentapi.quartz;

import com.example.idempotentapi.dto.OperationResponse;
import com.example.idempotentapi.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.example.idempotentapi.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class AsyncCreateUserJob implements Job {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String idemKey = jobDataMap.getString("idemKey");
        String userJson = jobDataMap.getString("userJson");

        try {
            User user = objectMapper.readValue(userJson, User.class);
            OperationResponse payload = userService.create(user); //calling legacy method
            messagingTemplate.convertAndSend("/topic/async-results", payload);
        } catch (Exception ex) {
            log.error("Failed to execute async create job for key {}", idemKey, ex);
            messagingTemplate.convertAndSend(
                    "/topic/async-results",
                    OperationResponse.error("Async create failed for key: " + idemKey)
            );
            throw new JobExecutionException(ex);
        }
    }
}
