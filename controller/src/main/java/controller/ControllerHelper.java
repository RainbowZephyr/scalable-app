package controller;

import channels.LoadBalancerChannelHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class ControllerHelper {

	private static ControllerHelper instance = new ControllerHelper();
	private static ArrayList<App> apps = new ArrayList<App>();
	private static ChannelGroup channelGroupWall = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static ChannelGroup channelGroupUser = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static ChannelGroup channelGroupMedia = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static ChannelGroup channelGroupMessage = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static ChannelGroup channelGroupSearch = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static HashMap<String, Channel> channels = new HashMap<String, Channel>();

	public Channel getChannelToLoadBalancer() {
		return channelToLoadBalancer;
	}

	private Channel channelToLoadBalancer;

	private ControllerHelper() {
	}

	public static ControllerHelper sharedInstance() {
		return instance;
	}

	public ArrayList<App> getApps() {
		return apps;
	}

	public void setApps(ArrayList<App> apps) {
		ControllerHelper.apps = apps;
	}

	public ChannelGroup getChannelGroupWall() {
		return channelGroupWall;
	}

	public void setChannelGroupWall(ChannelGroup channelGroupWall) {
		ControllerHelper.channelGroupWall = channelGroupWall;
	}

	public ChannelGroup getChannelGroupUser() {
		return channelGroupUser;
	}

	public void setChannelGroupUser(ChannelGroup channelGroupUser) {
		ControllerHelper.channelGroupUser = channelGroupUser;
	}

	public ChannelGroup getChannelGroupMedia() {
		return channelGroupMedia;
	}

	public void setChannelGroupMedia(ChannelGroup channelGroupMedia) {
		ControllerHelper.channelGroupMedia = channelGroupMedia;
	}

	public ChannelGroup getChannelGroupMessage() {
		return channelGroupMessage;
	}

	public void setChannelGroupMessage(ChannelGroup channelGroupMessage) {
		ControllerHelper.channelGroupMessage = channelGroupMessage;
	}

	public ChannelGroup getChannelGroupSearch() {
		return channelGroupSearch;
	}

	public void setChannelGroupSearch(ChannelGroup channelGroupSearch) {
		ControllerHelper.channelGroupSearch = channelGroupSearch;
	}

	public HashMap<String, Channel> getChannels() {
		return channels;
	}

	public void setChannels(HashMap<String, Channel> channels) {
		ControllerHelper.channels = channels;
	}

	public App getAppByName(String name) {
		int temp = -1;
		for (int i = 0; i < apps.size(); i++) {
			if ((apps.get(i).getName()).equals(name)) {
				temp = i;
				break;
			}
		}
		if (temp != -1)
			return apps.get(temp);
		else
			return null;
	}

	public String getFrozenInstanceOfApp(String name) {
		int temp = -1;
		for (int i = 0; i < apps.size(); i++) {
			if ((apps.get(i).getName()).contains(name) && apps.get(i).getStatus() == 0) {
				temp = i;
				break;
			}
		}

		if (temp != -1)
			return apps.get(temp).getName();
		else
			return "";
	}

	public void updateConfFromApps() {
		String newConf = "";
		ArrayList<App> tempApps = ControllerHelper.sharedInstance().getApps();
		for (int i = 0; i < tempApps.size(); i++) {
			newConf += tempApps.get(i).getName() + " " + tempApps.get(i).getStatus() + " " + tempApps.get(i).getIp()
					+ " " + tempApps.get(i).getPort() + " " + tempApps.get(i).getMax_thread_count() + " "
					+ tempApps.get(i).getMax_db_count() + "\n";
		}
		try {
			PrintWriter pw = new PrintWriter("controller/config/APP_CONFIG_FILE.config");
			pw.print(newConf);
			pw.close();
			pw = null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void bootNettyClient(String host, int port, String app_id) throws InterruptedException, IOException {
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		Bootstrap b = new Bootstrap(); // (1)
		b.group(workerGroup); // (2)
		b.channel(NioSocketChannel.class); // (3)
		b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
				ch.pipeline().addLast(new StringDecoder());
				ch.pipeline().addLast(new StringEncoder());
				ch.pipeline().addLast(new ApplicationResponseHandler());
			}
		});

		// Start the client.
		Channel channel = b.connect(host, port).channel(); // (5)
		// Add to appropriate channel group
		AppType appType = ControllerHelper.sharedInstance().getAppByName(app_id).getAppType();
		switch (appType) {
		case search:
			channelGroupSearch.add(channel);
			break;
		case user:
			channelGroupUser.add(channel);
			break;
		case wall:
			channelGroupWall.add(channel);
			break;
		case media:
			channelGroupMedia.add(channel);
			break;
		case message:
			channelGroupMessage.add(channel);
			break;
		}
		// Add to channels hashmap
		channels.put(app_id, channel);

	}

	// NETTY CONNECTION
	static class ApplicationResponseHandler extends SimpleChannelInboundHandler<String> {
		@Override
		public void channelRead0(ChannelHandlerContext ctx, String msg) {
			System.out.println("APPLICATION RESPONDED WITH: \n" + msg);
			JsonParser jsonParser = new JsonParser();
			if(msg != null) {
				JsonObject json = (JsonObject) jsonParser.parse(msg);
				String app_id = json.get("app_id").getAsString();
				String service_type = json.get("service_type").getAsString();
				if (service_type.equals("freeze")) {
					ControllerHelper.sharedInstance().getAppByName(app_id).setStatus(0);
				} else if (service_type.equals("continue")){
					ControllerHelper.sharedInstance().getAppByName(app_id).setStatus(1);
				} else if (service_type.equals("set_max_thread_count")) {
					int thread_count = json.get("count").getAsInt();
					ControllerHelper.sharedInstance().getAppByName(app_id).setMax_thread_count(thread_count);
				} else if (service_type.equals("set_max_db_connections_count")) {
					int db_count = json.get("count").getAsInt();
					ControllerHelper.sharedInstance().getAppByName(app_id).setMax_db_count(db_count);
				}
			}
		}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}
	}

	public void loadBalancerBootNettyClient(String host, int port) throws
			InterruptedException, IOException {
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(NioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new DelimiterBasedFrameDecoder
							(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new StringEncoder());
					ch.pipeline().addLast(
							LoadBalancerChannelHandler.class.getName(),
							new LoadBalancerChannelHandler());

				}
			});

			// Start the client.
			channelToLoadBalancer = b.connect(host, port).sync().channel(); // (5)
			channelToLoadBalancer.closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
		}

	}

	public boolean existsMoreThanOneApp(String app_id){
		int appCount = 0;
		for(int i = 0;i<ControllerHelper.sharedInstance().getApps().size();i++){
			if(ControllerHelper.sharedInstance().getApps().get(i).getName().contains(app_id)
					&& ControllerHelper.sharedInstance().getApps().get(i).getStatus() == 1){
				appCount++;
			}
		}
		return appCount > 1;
	}

	public void sendMessageToLoadBalancer(String message){
		message += "\n";
		channelToLoadBalancer.writeAndFlush(message);
	}

}
