package services;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestPipeline2 extends SimpleChannelInboundHandler<Object> {
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        Dispatcher.sharedInstance().updateClass("TestCommand", null);
        System.out.println("Loading...");
    }
}
