import connections.Producer;
import connections.QueueConsumer;
import connections.QueueConsumerListenerThread;
import connections.SocketConnectionToController;
import services.Dispatcher;
import utility.Constants;

public class main {

    public static void main(String[] args) throws Exception {
        Constants.setAppId("search1"); // for each instance running increment this
        // open titan graph database connection & set it

        Dispatcher.sharedInstance().init();
        Producer.sharedInstance().init(); // this initializes both listening QUEUE & Producer QUEUE
        QueueConsumer.sharedInstance().init();
        QueueConsumerListenerThread.sharedInstance().start();
        SocketConnectionToController.sharedInstance().init();
    }

}
