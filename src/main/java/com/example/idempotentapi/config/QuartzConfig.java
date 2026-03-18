package com.example.idempotentapi.config;

import com.example.idempotentapi.quartz.AutowiringSpringBeanJobFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(AutowireCapableBeanFactory beanFactory) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(new AutowiringSpringBeanJobFactory(beanFactory));
        return factory;
    }
}
