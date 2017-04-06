package services;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        /**
         * IN TESTING ONLY USE THE FOLLOWING
         *
         * IN PRODUCTION COMMENT OUT decoder, encoder, aggregator & HttpRequestHandle
         */
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast(HttpRequestHandle.class.getSimpleName(),
                new HttpRequestHandle()); // should add a class that extends SimpleChannelInboundHandler<Object>
        pipeline.addLast(RequestParser.class.getSimpleName(),
                new RequestParser());
        pipeline.addLast(AdminRequestServer.class.getSimpleName(),
                new AdminRequestServer());

    }
}
