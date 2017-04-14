import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
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
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
//        pipeline.addLast(HttpRequestHandle.class.getSimpleName(),new HttpRequestHandle());
        pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));

        //by7wlhom mn bytes le format el string
//         pipeline.addLast(new StringDecoder());
         // beydecode el3ak le string
//        pipeline.addLast(new StringEncoder());

        // by7wl el string ely gaii le json object
        pipeline.addLast(SocketConnectionParser.class.getSimpleName(),new SocketConnectionParser());


//beya5od el bytes ye7wlha le string
    }
}
