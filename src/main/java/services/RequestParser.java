package services;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class RequestParser extends MessageToMessageDecoder<Object> {

    private ServiceRequest parseToRequest(Object o){
        return null;
    }

    protected void decode(ChannelHandlerContext ctx, Object o, List<Object> list) throws Exception {

        // to be implemented

        list.add(parseToRequest(o));
    }
}
