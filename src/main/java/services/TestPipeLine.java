package services;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

public class TestPipeLine extends SimpleChannelInboundHandler<Object> {
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("DATA RECEIVED @ Special Port: " + o);

        String json = "{'echo':'sha8al'}";
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(json, Map.class);
        ServiceRequest serviceRequest = new ServiceRequest("echo", "sessionId", map);
        RequestHandle serviceHandle = new RequestHandle();
        Dispatcher.sharedInstance().dispatchRequest(serviceHandle, serviceRequest);
    }
}
