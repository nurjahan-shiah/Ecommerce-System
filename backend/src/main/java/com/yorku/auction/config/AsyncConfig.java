package com.yorku.auction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * AsyncConfig
 *
 * Enables Spring's @Async support so that NotificationService.onAuctionEvent()
 * runs on a dedicated thread pool and never blocks the publishing transaction.
 *
 * Pool sizing:
 *   core = 2  — always-alive threads for burst pushes
 *   max  = 10 — can scale up briefly under load
 *   queue= 50 — backlog before rejection
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(2);
        exec.setMaxPoolSize(10);
        exec.setQueueCapacity(50);
        exec.setThreadNamePrefix("notif-");
        exec.initialize();
        return exec;
    }
}
