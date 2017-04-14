import nginx.clojure.NginxClojureRT;
import nginx.clojure.NginxHttpServerChannel;
import nginx.clojure.java.ArrayMap;
import nginx.clojure.java.NginxJavaRequest;
import nginx.clojure.java.NginxJavaRingHandler;

import static nginx.clojure.MiniConstants.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Nginx implements NginxJavaRingHandler {


    public Object[] invoke(Map<String, Object> request) {
        NginxJavaRequest r = ((NginxJavaRequest) request);
        NginxHttpServerChannel channel = r.handler().hijack(r, true);

//        NginxHttpServerChannel channel = r.channel();
        ByteBuffer buffer = ByteBuffer.allocate(r.size());
        try {

            channel.read(buffer);
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while (buffer.hasRemaining()) {
                strBuilder.append(buffer.get());
            }
            String data = buffer.toString();

            NginxClojureRT.log.info("DATA "+data);


        } catch (IOException e)

        {
            e.printStackTrace();
        }
        //save channel ch to use it later
        //nginx-clojure will ignore this return because we have hijacked the request.
        return null;
    }
}
