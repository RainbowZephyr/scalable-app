package connections;


public class QueueConsumerListenerThread {
    private static Thread instance = new Thread(QueueConsumer.sharedInstance());

    public static Thread sharedInstance() {
        return instance;
    }

    private QueueConsumerListenerThread() {
    }


}
