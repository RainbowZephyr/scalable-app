package services;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;

import controller.Apps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class LoadBalancerChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	@Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest o) throws Exception {
		//get JSON from http request
		final String data = o.content().toString(StandardCharsets.UTF_8);
        System.out.println("DATA RECEIVED @ Special Port: " + data);

        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(data, Map.class);
        
        //HERE WE SHOULD HANDLE THE AUTOMATED REQUESTS OF THE CONTROLLER
        //FREEZE, CONTINUE, SET_MAX_THREAD_COUNT, SET_MAX_DB_CONNECTIONS

        //TODO get info from JSON (similar to the one located in /JSON/loadBalancerToController/message.json)
        //TODO get app thread count from app_id in JSON using Apps.apps
        //TODO get app request_per_second from JSON
        //TODO check if requests_per_second is less than (thread_count * 2)
        //TODO if so send request to decrease max thread count and decrease db connections to be half thread_count
        //TODO if greater than, send request to increase thread count and increase db connections to be half thread_count
        //TODO if thread_count would become greater than 50 by doing so, send continue to different frozen instance of same app
        
        //TODO if requests_per_second is less than 10, send freeze to app
        
        
        
            ChannelFuture cf = channelHandlerContext.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK))
                    .addListener(ChannelFutureListener.CLOSE);
            channelHandlerContext.flush();
    }
}
