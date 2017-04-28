import connections.Producer;
import connections.QueueConsumer;
import connections.QueueConsumerListenerThread;
import connections.SocketConnectionToController;
import services.Dispatcher;

public class main {

    public static void main(String[] args) throws Exception {
        Dispatcher.sharedInstance().init();
        Producer.sharedInstance().init(); // this initializes both listening QUEUE & Producer QUEUE
        QueueConsumer.sharedInstance().init();
        QueueConsumerListenerThread.sharedInstance().start();
        SocketConnectionToController.sharedInstance().init();
    }
}
