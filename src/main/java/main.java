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

import static utility.Constants.REQUEST_PORT;
import static utility.Constants.SPECIAL_PORT;

public class main {

    private final static String HOST = "127.0.0.1";
    public static void main(String [] args) throws Exception {
        Dispatcher.sharedInstance().init();
        // should only call onMessage & process data through
        EventLoopGroup reqBossGroup = new NioEventLoopGroup(5);
        EventLoopGroup reqWorkerGroup = new NioEventLoopGroup();
        try {
            BasicConfigurator.configure();
            // server instance to listen on normal requests Port
            ServerBootstrap reqServerBootstrap = new ServerBootstrap();
            reqServerBootstrap.group(reqBossGroup, reqWorkerGroup);
            reqServerBootstrap.channel(NioServerSocketChannel.class);
            reqServerBootstrap.handler(new LoggingHandler(LogLevel.TRACE));
            // add the appropriate child handler
            reqServerBootstrap.childHandler(new ServerInitializer());
            // bind to normal request port
            Channel requestsChannel = reqServerBootstrap.bind(HOST, REQUEST_PORT).sync().channel();
            // bind to special request port
            Channel specialRequestChannel = reqServerBootstrap.bind(HOST, SPECIAL_PORT).sync().channel();

            System.err.println("Listening For Requests on http" + "://127.0.0.1:" + REQUEST_PORT + '/');
            System.err.println("Listening For Controller Requests on http" + "://127.0.0.1:" + SPECIAL_PORT + '/');
            specialRequestChannel.closeFuture().sync();
        }finally {
            reqBossGroup.shutdownGracefully();
            reqWorkerGroup.shutdownGracefully();
        }
    }


}
