import connections.Producer;
import connections.QueueConsumer;
import connections.SocketConnectionToController;
import services.Dispatcher;

public class main {

    public static void main(String [] args) throws Exception {

        Dispatcher.sharedInstance().init();
        Producer.sharedInstance().init(); // this initializes both listening QUEUE & Producer QUEUE
        QueueConsumer.sharedInstance().init();
        Thread consumerListeningThread = new Thread(QueueConsumer.sharedInstance());
        consumerListeningThread.start();
        SocketConnectionToController.sharedInstance().init();
    }

}
