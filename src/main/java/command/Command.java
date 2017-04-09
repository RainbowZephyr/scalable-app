package command;

import services.RequestHandle;
import services.ServiceRequest;

import java.util.Map;

public abstract class Command implements Runnable {

    protected Map<String, Object> parameters;

    protected boolean shouldReturnResponse() {
        return true;
    }

    final public void run() {
        ServiceRequest serviceRequest = (ServiceRequest)
                parameters.get(ServiceRequest.class.getSimpleName());
        RequestHandle requestHandle = (RequestHandle)
                parameters.get(RequestHandle.class.getSimpleName());
        try {
            StringBuffer strbufResponse = execute(serviceRequest.getData());
            if (shouldReturnResponse()) {
                // send response to the queue
                requestHandle.send(strbufResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final public void init(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    protected abstract StringBuffer execute(Map<String, Object> requestMapData) throws Exception;
}