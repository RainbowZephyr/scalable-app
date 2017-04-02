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
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AdminHandlerPipeline extends SimpleChannelInboundHandler<FullHttpRequest> {
	@Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest o) throws Exception {
		final String data = o.content().toString(StandardCharsets.UTF_8);
        System.out.println("DATA RECEIVED @ Special Port: " + data);

        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(data, Map.class);
        String app_id = map.get("receiving_app_id");

        if(app_id.matches(".*\\d+.*")){
        	//send to specific app
        	int index = -1;
        	for(int i = 0;i<Apps.apps.size();i++){
        		if(Apps.apps.get(i).getName().equals(app_id)){
        			index = i;
        			break;
        		}
        	}
        	//if no match
        	if(index==-1){
        		channelHandlerContext.close();
        		return;
        	}
        	final String ip = Apps.apps.get(index).getIp();
        	final int port = Apps.apps.get(index).getPort();
        	System.out.println("Sending to app with id = "+app_id+", ip = "+ip+", port = "+port);

            Thread thread = new Thread(){
                public void run(){
                	Apps.tcpSend(ip, port, 10, data);
                }
            };
            thread.start();

        	
        }else{
        	//send to all instances of app
        }
//        ServiceRequest serviceRequest = new ServiceRequest("echo", "sessionId", map);
//        RequestHandle serviceHandle = new RequestHandle();
//        Dispatcher.sharedInstance().dispatchRequest(serviceHandle, serviceRequest);
        //channelHandlerContext.writeAndFlush("OK");

            ChannelFuture cf = channelHandlerContext.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR))
                    .addListener(ChannelFutureListener.CLOSE);
            channelHandlerContext.flush();
    }
}
