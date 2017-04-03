package services;

import exceptions.MultipleResponseException;

public class RequestHandle {
    private StringBuffer stringBuffer;
    private boolean didSendMessage = false;

    /**
     * IMPORTANT: THIS METHOD EXECUTES ONLY ONCE (RESTRICTION ON PUTTING ON QUEUE)
     * @param stringBuffer
     */
    public synchronized void send(StringBuffer stringBuffer) throws MultipleResponseException {
        if (didSendMessage) {
            throw new MultipleResponseException();
        }
        this.stringBuffer = stringBuffer;
        System.out.println(stringBuffer);
        // some how send this to its destination (should be put in the corresponding queue)
        didSendMessage = true;
    }

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }
}