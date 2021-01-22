package com.hyd.redisfx.fx;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 用于后台执行 Redis 操作的线程池。因为属于 IO 密集型任务，因此线程池大小非常大
 */
public class BackgroundExecutor {

    private static final ThreadPoolExecutor executor =
        new ThreadPoolExecutor(5, 100, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));
    }

    public static void shutdown() {
        executor.shutdownNow();
    }

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}
