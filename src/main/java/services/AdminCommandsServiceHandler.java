package services;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

public class AdminCommandsServiceHandler extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeLine = socketChannel.pipeline();
        // should only include a pipeline that executes commands of the request
//        pipeLine.addLast(RequestParser.class.getName(), new RequestParser());
        System.out.println("Received something");
//        pipeLine.addLast(LineBasedFrameDecoder.class.getName(),
//                new LineBasedFrameDecoder(256));
//
//        pipeLine.addLast(StringDecoder.class.getName(),
//                new StringDecoder(CharsetUtil.UTF_8));
//
//        pipeLine.addLast(ExecuteControllerRequest.class.getName(),
//                new ExecuteControllerRequest()); // should add a class that extends SimpleChannelInboundHandler<Object>
     // Provides support for http objects:
        pipeLine.addLast("codec", new HttpServerCodec());
        // Deals with fragmentation in http traffic: 
        pipeLine.addLast("aggregator", new HttpObjectAggregator(Short.MAX_VALUE));

        pipeLine.addLast(AdminHandlerPipeline.class.getName(), new AdminHandlerPipeline());
    }
}
