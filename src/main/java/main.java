import connections.SocketConnectionToController;
import connections.Producer;
import connections.QueueConsumer;
import services.Dispatcher;

public class main {

    public static void main(String [] args) throws Exception {
        Dispatcher.sharedInstance().init();
        QueueConsumer.sharedInstance().init();
        Thread consumerListeningThread = new Thread(QueueConsumer.sharedInstance());
        consumerListeningThread.start();
        Producer.sharedInstance().init();
        SocketConnectionToController.sharedInstance().init();
    }

}
