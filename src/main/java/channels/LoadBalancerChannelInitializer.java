package channels;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class LoadBalancerChannelInitializer extends ChannelInitializer<SocketChannel> {
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeLine = socketChannel.pipeline();
		System.out.println("Received something at LB port");
		// pipeLine.addLast(LineBasedFrameDecoder.class.getName(),
		// new LineBasedFrameDecoder(256));
		//
		// pipeLine.addLast(StringDecoder.class.getName(),
		// new StringDecoder(CharsetUtil.UTF_8));

		// Provides support for http objects:
		pipeLine.addLast("codec", new HttpServerCodec());
		// Deals with fragmentation in http traffic:
		pipeLine.addLast("aggregator", new HttpObjectAggregator(Short.MAX_VALUE));

		pipeLine.addLast(LoadBalancerChannelHandler.class.getName(), new LoadBalancerChannelHandler());
	}
}
