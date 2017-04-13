package services;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class SpecialCommandsServiceHandle extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeLine = socketChannel.pipeline();
        // should only include a pipeline that executes commands of the request
//        pipeLine.addLast(RequestParser.class.getName(), new RequestParser());
        pipeLine.addLast(ExecuteControllerRequest.class.getName(),
                new ExecuteControllerRequest()); // should add a class that extends SimpleChannelInboundHandler<Object>
        pipeLine.addLast(TestPipeLine.class.getName(), new TestPipeLine());
    }
}
