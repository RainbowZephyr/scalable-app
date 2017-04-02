package services;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ControllerRequestHandler extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeLine = socketChannel.pipeline();
        pipeLine.addLast("RequestParser", new AdminHandlerPipeline()); // should add a class that extends SimpleChannelInboundHandler<Object>
    }
}
