import org.apache.log4j.BasicConfigurator;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import connections.Producer;
import connections.QueueConsumer;
import connections.QueueConsumerListenerThread;
import connections.SocketConnectionToController;
import services.Dispatcher;

public class main {

    private final static int REQUEST_PORT = 6001, SPECIAL_PORT = 6002;
    private final static String HOST = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        Dispatcher.sharedInstance().init();
        Producer.sharedInstance().init(); // this initializes both listening QUEUE & Producer QUEUE
        QueueConsumer.sharedInstance().init();
        QueueConsumerListenerThread.sharedInstance().start();
        SocketConnectionToController.sharedInstance().init();
//        ServiceRequest serviceRequest = new ServiceRequest("echo", "sessionId", map);
//        RequestHandle serviceHandle = new RequestHandle();
//        Dispatcher.sharedInstance().dispatchRequest(serviceHandle, serviceRequest);
        /* #1 run a netty server           #2 keep polling for a message using the worker threads
           #3 on message Received assign to a thread
           #4
         */

//        Dispatcher.sharedInstance().dispatchRequest(serviceHandle, serviceRequest);
        // should only call onMessage & process data through
//        EventLoopGroup reqBossGroup = new NioEventLoopGroup(5);
//        EventLoopGroup reqWorkerGroup = new NioEventLoopGroup();
        EventLoopGroup specialBossGroup = new NioEventLoopGroup(5);
        EventLoopGroup specialWorkerGroup = new NioEventLoopGroup();
        try {
            BasicConfigurator.configure();
//            // server instance to listen on normal requests Port
//            ServerBootstrap reqServerBootstrap = new ServerBootstrap();
//            reqServerBootstrap.group(reqBossGroup, reqWorkerGroup);
//            reqServerBootstrap.channel(NioServerSocketChannel.class);
//            reqServerBootstrap.handler(new LoggingHandler(LogLevel.TRACE));
////            // add the appropriate child handler
//            reqServerBootstrap.childHandler(new ServiceInitializer());
//            Channel requestsChannel = reqServerBootstrap.bind(HOST, REQUEST_PORT).sync().channel();

            // server instance to listen on special requests Port
            ServerBootstrap specialReqServerBootstrap = new ServerBootstrap();
            specialReqServerBootstrap.group(specialBossGroup, specialWorkerGroup);
            specialReqServerBootstrap.channel(NioServerSocketChannel.class);
            specialReqServerBootstrap.handler(new LoggingHandler(LogLevel.TRACE));
            // add the appropriate child handler
            specialReqServerBootstrap.childHandler(new SpecialCommandsServiceHandle());
            Channel specialRequestChannel = specialReqServerBootstrap.bind(HOST, SPECIAL_PORT).sync().channel();

//            System.err.println("Listening For Requests on http" + "://127.0.0.1:" + REQUEST_PORT + '/');
            System.err.println("Listening For Controller Requests on http" + "://127.0.0.1:" + SPECIAL_PORT + '/');
            specialRequestChannel.closeFuture().sync();
        } finally {
//            reqBossGroup.shutdownGracefully();
//            reqWorkerGroup.shutdownGracefully();
            specialBossGroup.shutdownGracefully();
            specialWorkerGroup.shutdownGracefully();
        }
    }
}
