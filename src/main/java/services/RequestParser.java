package services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.Map;

public class RequestParser extends MessageToMessageDecoder<Object> {

    private Object parseToRequest(Object o){
        Gson gson = new Gson();
//        Map<String, Object> map = gson.fromJson((String) o, Map.class);
        System.out.println(o);
        return o;
    }

    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {
        list.add(parseToRequest(o));
    }
}
