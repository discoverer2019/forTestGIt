package com.nowcoder.community.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AlphaService {

    public static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    // 允许该方法在多线程环境下被异步调用
    @Async
    public void execute1() {
        logger.debug("execute1");
    }

    //@Scheduled(initialDelay = 1000, fixedRate = 1000)
    public void execute2() {
        logger.debug("executor2");
    }
}
