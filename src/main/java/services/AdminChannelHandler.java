package services;

import com.google.gson.Gson;
import controller.AppType;
import controller.ControllerHelper;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AdminChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	@Override
	public void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest o) throws Exception {
		// get JSON from http request
		final String data = o.content().toString(StandardCharsets.UTF_8);
		System.out.println("DATA RECEIVED @ Admin Port: " + data);

		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(data, Map.class);
		String app_id = map.get("receiving_app_id");
		// check if app_id contains a number, if so then the command is intended
		// for a specific app
		if (app_id.matches(".*\\d+.*")) {
			// send to specific app
			ControllerHelper.sharedInstance().getChannels().get(app_id).writeAndFlush(data);

		} else {
			// send to all instances of app
			
			//check if app_id can be converted to AppType
			boolean isAppType = false;
			for (AppType atype : AppType.values()) {
				if (atype.toString().equals(app_id)) {
					isAppType = true;
					break;
				}
			}
			//send to appropriate channelGroup
			if (isAppType) {
				switch (AppType.valueOf(app_id)) {
				case search:
					ControllerHelper.sharedInstance().getChannelGroupSearch().writeAndFlush(data);
					break;
				case user:
					ControllerHelper.sharedInstance().getChannelGroupUser().writeAndFlush(data);
					break;
				case wall:
					ControllerHelper.sharedInstance().getChannelGroupWall().writeAndFlush(data);
					break;
				case media:
					ControllerHelper.sharedInstance().getChannelGroupMedia().writeAndFlush(data);
					break;
				case message:
					ControllerHelper.sharedInstance().getChannelGroupMessage().writeAndFlush(data);
					break;
				}
			}
		}

		channelHandlerContext
				.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
				.addListener(ChannelFutureListener.CLOSE);
		channelHandlerContext.flush();
	}
}
