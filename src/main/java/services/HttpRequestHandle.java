package services;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;


public class HttpRequestHandle extends SimpleChannelInboundHandler<FullHttpRequest> {
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        ByteBuf jsonBuf = fullHttpRequest.content();
        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
        super.channelRead(ctx, jsonStr);
    }
}
