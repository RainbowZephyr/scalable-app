package services;

import com.google.gson.Gson;
import connections.SocketConnectionFactory;
import exceptions.MultipleResponseException;

import java.io.IOException;

public class RequestHandle {
    private StringBuffer stringBuffer;
    private boolean didSendMessage = false;
    private String connectionId;

    public RequestHandle(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * IMPORTANT: THIS METHOD EXECUTES ONLY ONCE (RESTRICTION ON PUTTING ON QUEUE)
     * @param stringBuffer
     */
    public synchronized void send(StringBuffer stringBuffer)
            throws MultipleResponseException, IOException {
        if (didSendMessage) {
            throw new MultipleResponseException();
        }
        this.stringBuffer = stringBuffer;
        // get some factory and send message with result
        SocketConnectionFactory.sharedInstance().getConnection(connectionId)
                .sendMessage(stringBuffer.toString());
        // some how send this to its destination (should be put in the corresponding queue)
        didSendMessage = true;
    }

    public String constructJSON(StringBuffer stringBuffer){
        return new Gson().toJson(stringBuffer.toString());
    }

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }
}