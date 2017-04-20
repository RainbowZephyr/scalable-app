package thread_pools;

import connections.QueueConsumerListenerThread;
import exceptions.CannotAcceptRequestException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingThreadPool extends ThreadPoolExecutor {
    private RejectedExecutionHandler rejectedExecutionHandler;

    BlockingThreadPool(int corePoolSize,
                       int maximumPoolSize,
                       long keepAliveTime,
                       TimeUnit unit,
                       BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        initRejectedExecutionHandler();
    }


    /**
     * initializes a policy to handle the rejected tasks.
     * Here mainly it keeps indefinitely trying to assign a task
     * till a number of retries (total rejection) or acceptance
     */
    private void initRejectedExecutionHandler() {
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                // Try indefinitely to add the task to the queue
                while (true) {
                    // if thread pool is down (reject)
                    if (executor.isShutdown()) {
                        try {
                            synchronized (QueueConsumerListenerThread.sharedInstance()) {
                                QueueConsumerListenerThread.sharedInstance().wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        throw new CannotAcceptRequestException();
                    }
                    if (executor.getActiveCount() < executor.getCorePoolSize()) {
                        executor.execute(r);
                        // Task got accepted!
                        break;
                    }
                }
            }



            setRejectedExecutionHandler(rejectedExecutionHandler);
        }
    }
}
