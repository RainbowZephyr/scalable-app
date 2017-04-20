package services;


import java.util.HashMap;
import java.util.Map;

public class RequestServerFactory {
    private static RequestServerFactory instance = new RequestServerFactory();
    private static Map<String, Class<?>> requestServerHtbl =
            new HashMap<String, Class<?>>();

    private RequestServerFactory() {
    }

    public static RequestServerFactory sharedInstance() {
        return instance;
    }

    public Class<?> getRequestServer(String id) {
        return requestServerHtbl.get(id);
    }

    public void registerRequestServer(String id,
                                      Class<?> requestServer) {
        requestServerHtbl.put(id, requestServer);
    }
}
