package services;

import connections.SocketConnectionToController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import utility.Constants;

import java.util.Map;


public class AdminRequestServer extends SimpleChannelInboundHandler<Map<String, Object>> {
    protected void channelRead0(ChannelHandlerContext ctx, Map<String, Object> request) throws Exception {
        ServiceRequest serviceRequest = constructReq(request);
        Dispatcher.sharedInstance().executeControllerCommand(
                new RequestHandle(SocketConnectionToController.class.getSimpleName())
                , serviceRequest);
    }


    private ServiceRequest constructReq(Map<String, Object> request) {
        String sessionId = (String) request.get(Constants.SESSION_ID_KEY);
        String appId = (String) request.get(Constants.APP_ID_KEY);
        String receivingAppId = (String) request.get(Constants.RECEIVING_APP_ID_KEY);
        String strAction = (String) request.get(Constants.ACTION_NAME_KEY);
        Map<String, Object> requestParams = (Map<String, Object>)
                request.get(Constants.REQUEST_PARAMETERS_KEY);
        return new ServiceRequest(strAction, sessionId, requestParams);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        SocketConnectionToController.sharedInstance().setCtx(ctx);
    }
}
