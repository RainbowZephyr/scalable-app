package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import controller.ControllerHelper;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;

public class LoadBalancerChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	@Override
	public void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest o) throws Exception {
		// get JSON from http request
		final String data = o.content().toString(StandardCharsets.UTF_8);
		System.out.println("DATA RECEIVED @ LoadBalancer Port: " + data);

		JsonParser jsonParser = new JsonParser();
		JsonObject json = (JsonObject) jsonParser.parse(data);
		JsonArray jsonArray = (JsonArray) json.get("content");

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jo = (JsonObject) jsonArray.get(i);
			String app_id = jo.get("app_id").getAsString();
			System.out.println(app_id);
			int requests_per_second = Integer.parseInt(jo.get("requests_per_second").getAsString());
			int max_thread_count = ControllerHelper.sharedInstance().getAppByName(app_id).getMax_thread_count();
			
			//TODO Construct the actual JSON and send it using ControllerHelper.sharedInsance.getChannels().get(app_id).writeAndFlush(json)
			if (requests_per_second < 10) {
				// send freeze
				System.out.println("FREEZE APP: " + app_id);
			} else {
				if (requests_per_second > 100) {
					// send request to continue another app
					String continuedAppName = ControllerHelper.sharedInstance()
							.getFrozenInstanceOfApp(app_id.replaceAll("\\d+.*", ""));
					// send continue request to continuedApp
					System.out.println("CONTINUE APP: " + continuedAppName);
				} else {
					if (max_thread_count == requests_per_second / 2) {
						// DO NOTHING
					} else {
						if (max_thread_count > requests_per_second / 2) {
							System.out.println("DECREASE THREAD COUNT(" + max_thread_count + ") TO BE "
									+ ((requests_per_second / 2) + (max_thread_count - (requests_per_second / 2)) / 2)
									+ " APP: " + app_id);
						} else {
							System.out.println("INCREASE THREAD COUNT(" + max_thread_count + ") TO BE "
									+ ((requests_per_second / 2) - ((requests_per_second / 2) - max_thread_count) / 2)
									+ " APP: " + app_id);
						}
					}
				}
			}
		}
		channelHandlerContext.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
				.addListener(ChannelFutureListener.CLOSE);
		channelHandlerContext.flush();
	}
}
