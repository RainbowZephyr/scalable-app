package connections;

import java.io.IOException;
import java.io.Serializable;

public interface SocketConnection {
    void sendMessage(Serializable object) throws IOException;
}
