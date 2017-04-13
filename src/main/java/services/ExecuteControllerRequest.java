package services;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ExecuteControllerRequest extends SimpleChannelInboundHandler<ServiceRequest> {

    protected void channelRead0(ChannelHandlerContext ctx, ServiceRequest serviceRequest) throws Exception {
        Dispatcher.sharedInstance().dispatchRequest(null, serviceRequest);
    }
}
