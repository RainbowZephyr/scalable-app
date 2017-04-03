package controller;
import com.google.gson.Gson;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.BasicConfigurator;
import services.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Controller {
    private final static int LOAD_BALANCER_PORT = 4000, ADMIN_PORT = 4001;
    private final static String HOST = "127.0.0.1";
    private static ArrayList<App> apps = new ArrayList<App>();
    public static void main(String [] args) throws Exception {

    	//turn config file into App instances
    	getAppsFromConf();

        /* #1 run a netty server
           #2 keep polling for a message using the worker threads
           #3 on message Received assign to a thread
           #4
         */

        // should only call onMessage & process data through
        EventLoopGroup loadBossGroup = new NioEventLoopGroup(5);
        EventLoopGroup loadWorkerGroup = new NioEventLoopGroup();
        EventLoopGroup adminBossGroup = new NioEventLoopGroup(5);
        EventLoopGroup adminWorkerGroup = new NioEventLoopGroup();
        try {
            BasicConfigurator.configure();
            // server instance to listen on normal requests Port
            ServerBootstrap loadBalancerBootstrap = new ServerBootstrap();
            loadBalancerBootstrap.group(loadBossGroup, loadWorkerGroup);
            loadBalancerBootstrap.channel(NioServerSocketChannel.class);
            loadBalancerBootstrap.handler(new LoggingHandler(LogLevel.TRACE));
            // add the appropriate child handler
            loadBalancerBootstrap.childHandler(new LoadBalancerChannelInitializer());
            Channel requestsChannel = loadBalancerBootstrap.bind(HOST, LOAD_BALANCER_PORT).sync().channel();

            // server instance to listen on special requests Port
            ServerBootstrap specialReqServerBootstrap = new ServerBootstrap();
            specialReqServerBootstrap.group(adminBossGroup, adminWorkerGroup);
            specialReqServerBootstrap.channel(NioServerSocketChannel.class);
            specialReqServerBootstrap.handler(new LoggingHandler(LogLevel.TRACE));
            // add the appropriate child handler
            specialReqServerBootstrap.childHandler(new AdminChannelInitializer());
            Channel specialRequestChannel = specialReqServerBootstrap.bind(HOST, ADMIN_PORT).sync().channel();

            System.err.println("Listening For LoadBalancer Updates on http" + "://127.0.0.1:" + LOAD_BALANCER_PORT + '/');
            System.err.println("Listening For Admin Requests on http" + "://127.0.0.1:" + ADMIN_PORT + '/');
            specialRequestChannel.closeFuture().sync();
        }finally {
            loadBossGroup.shutdownGracefully();
            loadWorkerGroup.shutdownGracefully();
            adminBossGroup.shutdownGracefully();
            adminWorkerGroup.shutdownGracefully();
        }
    }
    public static void getAppsFromConf(){
    	//clear apps array
    	apps.clear();
    	//read from config file to String
    	String res="";
    	try {
    	    BufferedReader in = new BufferedReader(new FileReader("./src/APP_CONFIG_FILE.config"));
    	    String str;
    	    while ((str = in.readLine()) != null){
    	        res+=str+"\n";
    	    }
    	    in.close();
    	} catch (IOException e) {
    	}
    	System.out.println(res);
    	String [] resArray = res.split("\n");
    	System.out.println(resArray.length);
    	//turn each line in config file to instance of App class
    	for(int i = 0;i<resArray.length;i++){
    		String [] tempArray = resArray[i].split(" ");
    		App app = new App(tempArray[0], //name
    				Integer.parseInt(tempArray[1]), //status
    				tempArray[2], //ip
    				Integer.parseInt(tempArray[3]),	//port
    				Integer.parseInt(tempArray[4]),	//max_thread_count
    				Integer.parseInt(tempArray[5]), //max_db_count
    				AppType.valueOf(tempArray[0].replaceAll("\\d+.*", ""))); //type
    		apps.add(app);
    		
    	}
    	//assign global variable (Apps.apps) to apps
    	//we can now access apps anywhere in the code by calling Apps.apps
    	Apps.apps = apps;
    }


}
