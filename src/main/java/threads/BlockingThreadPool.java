package threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingThreadPool extends ThreadPoolExecutor{
    private long repeatedTryEvery;
    private TimeUnit timeUnit;
    private long limitedTrials = 100; // default value
    public BlockingThreadPool(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              long repeatedTryEvery,
                              TimeUnit timeUnit,
                              BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.repeatedTryEvery = repeatedTryEvery;
        this.timeUnit = timeUnit;
        initRejectedExecutionHandler();
    }

    private RejectedExecutionHandler rejectedExecutionHandler;


    /**
     * initializes a policy to handle the rejected tasks.
     * Here mainly it keeps indefinitely trying to assign a task
     * till a number of retries (total rejection) or acceptance
     */
    private void initRejectedExecutionHandler(){
        rejectedExecutionHandler = new RejectedExecutionHandler() {
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

                int retryCount = 0;

                // Try indefinitely to add the task to the queue
                while (true){
                    retryCount++;
                    // if thread pool is down (reject)
//                    if(executor.isShutdown()){
//                        System.out.println("THREAD EXECUTING BLOCK HANDLER: "+ Thread.currentThread().getId());
//                        ((BlockingThreadPool) executor).taskRejectedGaveUp(r, retryCount);
//                        throw new RejectedExecutionException("ThreadPool has been shutdown");
//                    }

                    try{
                        if (executor.getQueue().offer(r, repeatedTryEvery, timeUnit))
                        {
                            // Task got accepted!
                            ((BlockingThreadPool) executor).taskAccepted(r, retryCount);
                            break;
                        }
                        else{
                            boolean tryAgain =
                                    ((BlockingThreadPool) executor).taskRejectedRetrying(r, retryCount);
                            if(!tryAgain){
                                break;
                            }
                        }
                    }catch (InterruptedException e){
                        throw new AssertionError(e);
                    }

                }
            }
        };
        setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    /**
     * Called when giving up on the task and rejecting it for good.
     * @param r - Task
     * @param retryCount - number of total retries
     */
    protected void taskRejectedGaveUp(Runnable r, int retryCount)
    {
        System.out.println("TASK Gave UP !!!");
    }

    /**
     * Called when the task that was rejected initially is rejected again.
     * @param r - task to be executed
     * @param retryCount - trials till rejection
     * @return whether or not to keep trying submitting the task
     */
    protected boolean taskRejectedRetrying(Runnable r, int retryCount)
    {
        return true;
    }

    /**
     * Called when the rejected task is finally accepted.
     * @param r - Task
     * @param retryCount - number of retries before acceptance
     */
    protected void taskAccepted(Runnable r, int retryCount)
    {

    }

    public void setLimitedTrials(long limitedTrials) {
        this.limitedTrials = limitedTrials;
    }
}
