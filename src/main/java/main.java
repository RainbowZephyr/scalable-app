import load_balancer.NginxInitialization;

public class main {
    //private final static int REQUEST_PORT = 6001, SPECIAL_PORT = 6002;
    private final static String HOST = "127.0.0.1";
    private final static int port = 6001;
    public static void main(String[] args) throws Exception {

        //init Nginx
        NginxInitialization.getInstance();


        // get statistics and send each second

//        SocketConnectionToController.sharedInstance().init(HOST, port); // blocking code
    }

}
