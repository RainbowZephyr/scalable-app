package threads;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DatabaseThreadPool {
    private static DatabaseThreadPool instance =
            new DatabaseThreadPool();
    private ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(10, 10, 0,
                    TimeUnit.NANOSECONDS,
                    new LinkedBlockingDeque<Runnable>());

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
