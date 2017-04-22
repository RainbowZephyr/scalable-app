package connections;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        /**
         * IN TESTING ONLY USE THE FOLLOWING
         *
         * IN PRODUCTION COMMENT OUT decoder, encoder, aggregator & HttpRequestHandle
         */
//        pipeline.addLast("decoder", new HttpRequestDecoder());
//        pipeline.addLast("encoder", new HttpResponseEncoder());
//        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
//        pipeline.addLast(HttpRequestHandle.class.getSimpleName(),
//                new HttpRequestHandle());
        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(RequestParser.class.getSimpleName(),
                new RequestParser()); // your handler (controller request handler)
        pipeline.addLast(ControllerRequestHandler.class.getSimpleName(),
                new ControllerRequestHandler()); // your handler (controller request handler)
    }
}
