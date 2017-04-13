package channels;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class AdminChannelInitializer extends ChannelInitializer<SocketChannel> {
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		System.out.println("Received something at Admin port");
		// pipeLine.addLast(LineBasedFrameDecoder.class.getName(),
		// new LineBasedFrameDecoder(256));
		//
		// pipeLine.addLast(StringDecoder.class.getName(),
		// new StringDecoder(CharsetUtil.UTF_8));

		// pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE,
		// Delimiters.lineDelimiter()));
		// pipeline.addLast(new StringDecoder());
		// pipeline.addLast(new StringEncoder());
		// Provides support for http objects:
		pipeline.addLast("codec", new HttpServerCodec());
		// Deals with fragmentation in http traffic:
		pipeline.addLast("aggregator", new HttpObjectAggregator(Short.MAX_VALUE));

		pipeline.addLast(AdminChannelHandler.class.getName(), new AdminChannelHandler());
	}
}
