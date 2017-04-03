package services;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class SpecialCommandsServiceHandle extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // should only include a pipeline that executes commands of the request
//        pipeLine.addLast(RequestParser.class.getName(), new RequestParser());
        pipeline.addLast(ExecuteControllerRequest.class.getName(),
                new ExecuteControllerRequest()); // should add a class that extends SimpleChannelInboundHandler<Object>
    }
}
