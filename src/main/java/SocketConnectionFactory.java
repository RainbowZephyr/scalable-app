import java.util.HashMap;


public class SocketConnectionFactory {
    private static SocketConnectionFactory instance = new SocketConnectionFactory();
    private static HashMap<String, SocketConnection> hashMap =
            new HashMap<String, SocketConnection>();

    private SocketConnectionFactory() {
    }

    public static SocketConnectionFactory sharedInstance() {
        return instance;
    }

    public SocketConnection getConnection(String connectionId) {
        return hashMap.get(connectionId);
    }

    public void registerConnection(String connectionName, SocketConnection connection) {
        hashMap.put(connectionName, connection);
    }
}