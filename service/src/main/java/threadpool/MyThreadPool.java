package threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;


public class MyThreadPool {

    /**
     * 任务异步执行 线程池
     */
    private static ThreadPoolExecutor TASK_POOL;

    private static final long KEEP_ALIVE_TIME = 60;



    public static void init() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
            .namingPattern("abnormal-task-pool-%d")
            .build();

        TASK_POOL = new ThreadPoolExecutor(
            10,
            15,
            KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(500),
            threadFactory, new ThreadPoolExecutor.AbortPolicy());

        //, new ThreadPoolExecutor.CallerRunsPolicy()

        System.out.println("ABNORMAL_TASK_POOL initialized");
    }

    public static ThreadPoolExecutor getPool() {
        return TASK_POOL;
    }

}
