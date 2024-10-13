package com.LR.openRana.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {

    private static ConcurrentHashMap<String, ExecutorService> threadPools = new ConcurrentHashMap<>();

    /**
     * 创建一个固定大小的线程池
     *
     * @param poolName          线程池的名称
     * @param numberOfThreads   线程池的线程数量
     */
    public static ExecutorService createThreadPool(String poolName, int numberOfThreads) {
        if (threadPools.containsKey(poolName)) {
            return threadPools.get(poolName);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads*2);
        threadPools.put(poolName, executorService);
        return executorService;
    }

    /**
     * 提交一个任务到指定的线程池
     *
     * @param poolName  线程池的名称
     * @param task      要提交的任务
     */
    public static void submitTask(String poolName, Runnable task) {
        ExecutorService executorService = threadPools.get(poolName);
        if (executorService == null || executorService.isShutdown()) {
            throw new IllegalStateException("线程池 \"" + poolName + "\" 不存在或已关闭");
        }
        executorService.submit(task);
    }

    /**
     * 关闭指定的线程池
     *
     * @param poolName  线程池的名称
     */
    public static void shutdown(String poolName) {
        ExecutorService executorService = threadPools.remove(poolName);
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown(); // 请求关闭线程池
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow(); // 超时后强制关闭
                }
            } catch (InterruptedException ex) {
                executorService.shutdownNow(); // 线程被中断时强制关闭
                Thread.currentThread().interrupt(); // 保留中断状态
            }
        }
    }

    /**
     * 关闭所有线程池
     */
    public static void shutdownAll() {
        for (String poolName : threadPools.keySet()) {
            shutdown(poolName);
        }
    }

    /**
     * 检查指定线程池是否存在
     *
     * @param poolName  线程池的名称
     * @return          如果线程池存在且未关闭，返回 true；否则返回 false
     */
    public static boolean isThreadPoolCreated(String poolName) {
        ExecutorService executorService = threadPools.get(poolName);
        return executorService != null && !executorService.isShutdown();
    }
}

