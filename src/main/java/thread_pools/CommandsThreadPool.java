package thread_pools;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommandsThreadPool{
    private static CommandsThreadPool instance =
            new CommandsThreadPool();
    private final int DEFAULT_POOL_SIZE = 2;
    private long retryEvery = 1;
    private TimeUnit retryEveryTimeUnit = TimeUnit.SECONDS;
    private int poolSize = DEFAULT_POOL_SIZE;

    private BlockingThreadPool threadPool =
            new BlockingThreadPool(
                    DEFAULT_POOL_SIZE,
                    DEFAULT_POOL_SIZE,
                    0,
                    TimeUnit.NANOSECONDS,
                    new SynchronousQueue<Runnable>()); // Queue that holds nothing

    private CommandsThreadPool() {
    }

    public static CommandsThreadPool sharedInstance() {
        return instance;
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    /* Instantiate the Thread Pool */
    public void reloadThreadPool() {
        threadPool = new BlockingThreadPool(poolSize,
                poolSize,
                threadPool.getKeepAliveTime(TimeUnit.NANOSECONDS),
                TimeUnit.NANOSECONDS,
                threadPool.getQueue());
    }

    public void setMaxThreadPoolSize(int size) {
        poolSize = size;
        getThreadPool().setCorePoolSize(poolSize);
    }
}
