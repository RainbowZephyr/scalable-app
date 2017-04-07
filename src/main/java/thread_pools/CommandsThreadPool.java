package thread_pools;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommandsThreadPool{
    private static CommandsThreadPool instance =
            new CommandsThreadPool();
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
                    retryEvery,
                    retryEveryTimeUnit,
                    new LinkedBlockingDeque<Runnable>());

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
                retryEvery,
                retryEveryTimeUnit,
                threadPool.getQueue());
    }

    public void setMaxThreadPoolSize(int size) {
        poolSize = size;
        getThreadPool().setCorePoolSize(poolSize);
    }

    /**
     * set
     * @param retryEvery - units of timeUnit to wait till resubmitting a task
     * @param timeUnit - TimeUnit (nano, milliseconds, seconds)
     */
    public void setRetryEvery(long retryEvery, TimeUnit timeUnit){
        this.retryEvery = retryEvery;
        this.retryEveryTimeUnit = timeUnit;
    }
}
