package thread_pools;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DatabaseThreadPool {
    private static DatabaseThreadPool instance =
            new DatabaseThreadPool();
    private final int DEFAULT_POOL_SIZE = 10;
    private long retryEvery = 1;
    private TimeUnit retryEveryTimeUnit = TimeUnit.SECONDS;
    private int poolSize = DEFAULT_POOL_SIZE;

    private BlockingThreadPool threadPool =
            new BlockingThreadPool(
                    DEFAULT_POOL_SIZE,
                    DEFAULT_POOL_SIZE,
                    0,
                    TimeUnit.NANOSECONDS,
                    new SynchronousQueue<Runnable>());

    private DatabaseThreadPool() {
    }

    public static DatabaseThreadPool sharedInstance() {
        return instance;
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public void setMaxThreadPoolSize(int size) {
        getThreadPool().setCorePoolSize(size);
    }
}
