package threads;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommandsThreadPool {
    private final int DEFAULT_POOL_SIZE = 10;
    private int poolSize = DEFAULT_POOL_SIZE;
    private static CommandsThreadPool instance =
            new CommandsThreadPool();
    private ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(DEFAULT_POOL_SIZE, DEFAULT_POOL_SIZE, 0,
                    TimeUnit.NANOSECONDS,
                    new LinkedBlockingDeque<Runnable>());
    public static CommandsThreadPool sharedInstance(){
        return instance;
    }
    private CommandsThreadPool(){};

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    /* Instantiate the Thread Pool */
    public void reloadThreadPool() {
        threadPool = new ThreadPoolExecutor(poolSize,
                poolSize,
                threadPool.getKeepAliveTime(TimeUnit.NANOSECONDS),
                TimeUnit.NANOSECONDS,
                threadPool.getQueue());
    }

    public void setMaxThreadPoolSize(int size){
        poolSize = size;
        getThreadPool().setCorePoolSize(poolSize);
    }
}
