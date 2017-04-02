package services;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

public class ServiceInitializer extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // setup the pipeline
        ChannelPipeline pipeLine = socketChannel.pipeline();
        pipeLine.addLast(LoadBalancerHandlerPipeline.class.getName(), new LoadBalancerHandlerPipeline()); // should add a class that extends SimpleChannelInboundHandler<Object>
    }
}
