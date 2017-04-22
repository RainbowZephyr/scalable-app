package load_balancer;

import com.google.gson.Gson;
import connections.OutboundMessageQueue;
import javafx.util.Pair;
import net.openhft.chronicle.map.ChronicleMap;
import nginx.clojure.NativeInputStream;
import nginx.clojure.NginxClojureRT;
import nginx.clojure.NginxHttpServerChannel;
import nginx.clojure.RequestBodyFetcher;
import nginx.clojure.java.NginxJavaRequest;
import nginx.clojure.java.NginxJavaRingHandler;
import nginx.clojure.util.NginxSharedHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static nginx.clojure.MiniConstants.DEFAULT_ENCODING;


public class Nginx implements NginxJavaRingHandler {

    private static Gson gson = new Gson();
    private static volatile ChronicleMap<String, List<Pair<Boolean, OutboundMessageQueue>>>
            messageQueuesHashMap =
            ChronicleMap.of(String.class, (Class<List<Pair<Boolean, OutboundMessageQueue>>>) (Class)List.class)
                    .name("MessageQueuesHashMap")
                    .averageKeySize(8096)
                    .entries(8096)
                    .averageValueSize(8096)
                    .create();
    // just for now using a normal concurrent hashmap (not shared)
    private static volatile Map<Long, NginxHttpServerChannel>
            channelNginxSharedHashMap =
            new ConcurrentHashMap<>();

    private static volatile NginxSharedHashMap<String, Integer>
            countersHashMap =
            NginxSharedHashMap.build("Counters");

    //List<Pair<Boolean,OutboundMessageQueue>>
    public static ChronicleMap<String, List<Pair<Boolean,OutboundMessageQueue>>> getMessageQueuesHashMap() {
        return messageQueuesHashMap;
    }
    //NginxHttpServerChannel
    public static Map<Long, NginxHttpServerChannel> getChannelNginxSharedHashMap() {
        return channelNginxSharedHashMap;
    }
//    public static NginxSharedHashMap<Long, NginxHttpServerChannel> getChannelNginxSharedHashMap(){
//        return channelNginxSharedHashMap;
//    }

    public static NginxSharedHashMap<String, Integer> getCountersHashMap() {
        return countersHashMap;
    }

    public Object[] invoke(Map<String, Object> request) throws IOException {
        // read in request
        NginxJavaRequest req = (NginxJavaRequest) request;
        // hijack to be able to respond at any time (async response)
        NginxHttpServerChannel channel = req.hijack(true);

        channelNginxSharedHashMap.put(req.nativeRequest(), channel);

        // get body of the request & turn to json
        RequestBodyFetcher b = new RequestBodyFetcher(); // get body of the request
        NativeInputStream body = (NativeInputStream) b.fetch(req.nativeRequest(), DEFAULT_ENCODING);
        // LOG
        NginxClojureRT.log.info("REQUEST: " + req.nativeRequest() + " ,BODY: " + channelNginxSharedHashMap.get(req.nativeRequest()));
        // call putInCorrespondingQueue
        putInCorrespondingQueue(streamToString(body), req.nativeRequest()+"");
        return null; // this is ignored as the request is hijacked
    }

    private synchronized String streamToString(InputStream is) throws IOException {
        final int PKG_SIZE = 1024;
        byte[] data = new byte[PKG_SIZE];
        StringBuilder buffer = new StringBuilder(PKG_SIZE * 10);
        int size;

        size = is.read(data, 0, data.length);
        while (size > 0) {
            String str = new String(data, 0, size);
            buffer.append(str);
            size = is.read(data, 0, data.length);
        }
        return buffer.toString();
    }

    public static synchronized void putInCorrespondingQueue(String json, String reqUUID) {
        Map<String, Object> map = gson.fromJson(json, Map.class);
        String appName = (String) map.get("app_id");
        // get corresponding Queue
        int counter = countersHashMap.get(appName);
        // get List
        List<Pair<Boolean,OutboundMessageQueue>> mapObject =
                getMessageQueuesHashMap().get(appName);
        // send on that queue
        if (mapObject.get(counter).getKey()){
            mapObject.get(counter).getValue().sendMessage(json, reqUUID);
        }
        // increment counter
        counter = counter < mapObject.size() - 1 ? counter++ : 0; // increment counter
        countersHashMap.put(appName, counter);
    }

    private void turnOffInstance(String instanceName){
        getMessageQueuesHashMap().remove(instanceName);
        countersHashMap.remove(instanceName);
        NginxInitialization.getInstance().changeInstanceState(instanceName, 0); // turn off in config
    }
}
