package connections;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;


public class ControllerRequestHandler extends SimpleChannelInboundHandler<Map<String, Object>> {
    protected void channelRead0(ChannelHandlerContext ctx, Map<String, Object> request) throws Exception {
        // 4oof hat3ml eh hna, bs to shutdown an instance use this
        String serviceType = (String) request.get("service_type");
        String startSending = "start_sending" , stopSending = "stop_sending";
        if(serviceType.matches(startSending) &&
                !SocketConnectionToController.sharedInstance().isSending()) {
            SocketConnectionToController.sharedInstance().startSendingDataToController();
        }else if(serviceType.matches(stopSending)){
            SocketConnectionToController.sharedInstance().stopSendingDataToController();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        SocketConnectionToController.sharedInstance().setCtx(ctx);
    }
}
