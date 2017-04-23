import connections.SocketConnectionToController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class main {
    private static final String controllerConfPath = "loadbalancer/config/controller.properties";

    public static void main(String[] args) throws Exception {

        //init Listening To Controller
        startListeningToController();

        // get statistics and send each second

//        SocketConnectionToController.sharedInstance().init(HOST, port); // blocking code
    }

    public static void startListeningToController() {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(controllerConfPath);
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String controllerServerAddress = prop.getProperty("ControllerServerAddress");
        int controllerServerPort = Integer.parseInt(prop.getProperty("ControllerServerPort"));
        SocketConnectionToController.sharedInstance().setControllerRemoteAddress
                (controllerServerAddress, controllerServerPort);
        SocketConnectionToController.sharedInstance().init();
    }

}
