package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling // 如果没有加上这个注解，那么定时任务不会启用
@EnableAsync
public class ThreadPoolConfig {
}
