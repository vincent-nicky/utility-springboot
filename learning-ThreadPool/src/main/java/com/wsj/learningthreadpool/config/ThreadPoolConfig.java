package com.wsj.learningthreadpool.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolConfig {

    // 核心线程
    private static final int SIZE_CORE_POOL = 6;

    // 最大线程数
    private static final int SIZE_MAX_POOL = 12;

    // 线程池维护线程所允许的空闲时间
    private static final long TIME_KEEP_ALIVE = 1L;

    // 缓冲队列的大小
    private static final int SIZE_WORK_QUEUE = 20;

    // 自定义一个线程池
    public static ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                SIZE_CORE_POOL,
                SIZE_MAX_POOL,
                TIME_KEEP_ALIVE,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(SIZE_WORK_QUEUE),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
