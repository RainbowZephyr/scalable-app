package services;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import utility.Constants;

import java.util.Map;

public class ExecuteControllerRequest extends SimpleChannelInboundHandler<Map<String,Object>> {

    protected void channelRead0(ChannelHandlerContext ctx, Map<String,Object> request) throws Exception {
        String sessionId = (String) request.get(Constants.SESSION_ID_KEY);
        String appId = (String) request.get(Constants.APP_ID_KEY);
        String receivingAppId = (String) request.get(Constants.RECEIVING_APP_ID_KEY);
        String strAction = (String) request.get(Constants.ACTION_NAME_KEY);
        Map<String, Object> requestParams = (Map<String, Object>)
                request.get(Constants.REQUEST_PARAMETERS_KEY);
        ServiceRequest serviceRequest = new ServiceRequest(strAction, sessionId, requestParams);
        Dispatcher.sharedInstance().dispatchRequest(new RequestHandle(), serviceRequest);
    }
}
