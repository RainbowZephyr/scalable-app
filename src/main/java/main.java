public class main {
    //private final static int REQUEST_PORT = 6001, SPECIAL_PORT = 6002;
    private final static String HOST = " 127.0.0.1";

    public static void main(String[] args) throws Exception {
        SocketConnectionToController.sharedInstance().init();

    }

}
