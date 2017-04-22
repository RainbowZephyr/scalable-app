package connections;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;


public class ControllerRequestHandler extends SimpleChannelInboundHandler<Map<String, Object>> {
    protected void channelRead0(ChannelHandlerContext ctx, Map<String, Object> request) throws Exception {
        // 4oof hat3ml eh hna, bs to shutdown an instance use this
//        Nginx.turnOffInstance("NameOfInstanceOfApp ex wall1");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        SocketConnectionToController.sharedInstance().setCtx(ctx);
    }
}
