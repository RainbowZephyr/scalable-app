package connections;

import java.io.IOException;
import java.util.Map;

public interface SocketConnection {
    void sendMessage(String response, Map<String, Object> additionalParams) throws IOException;
}
