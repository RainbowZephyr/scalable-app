package connections;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.BasicConfigurator;
import services.ServerInitializer;

import java.io.Serializable;

import static utility.Constants.SPECIAL_PORT;


public class SocketConnectionToController implements SocketConnection {
    private static SocketConnectionToController instance = new SocketConnectionToController();

    public static SocketConnectionToController sharedInstance() {
        return instance;
    }

    private SocketConnectionToController() {}

    private final static String HOST = "127.0.0.1";

    private Channel specialRequestChannel;

    public void init() throws InterruptedException {
        EventLoopGroup reqBossGroup = new NioEventLoopGroup(5);
        EventLoopGroup reqWorkerGroup = new NioEventLoopGroup();
        try {
            BasicConfigurator.configure();
            // server instance to listen on Special requests Port
            ServerBootstrap reqServerBootstrap = new ServerBootstrap();
            reqServerBootstrap.group(reqBossGroup, reqWorkerGroup);
            reqServerBootstrap.channel(NioServerSocketChannel.class);
            reqServerBootstrap.handler(new LoggingHandler(LogLevel.TRACE));
            // add the appropriate child handler
            reqServerBootstrap.childHandler(new ServerInitializer());
            // bind to special request port
            specialRequestChannel = reqServerBootstrap.bind(HOST, SPECIAL_PORT).sync().channel();
            System.err.println("Listening For JSONRequests on queue: [" +
                    QueueConsumer.sharedInstance().getQUEUE_NAME()+"] -> " +
                    QueueConsumer.sharedInstance().getMQ_SERVER_ADDRESS() +
                    ":"+ QueueConsumer.sharedInstance().getMQ_SERVER_PORT()+ '/');
            System.err.println("Listening For Controller Requests on http" + "://127.0.0.1:" + SPECIAL_PORT + '/');
            specialRequestChannel.closeFuture().sync();
        }finally {
            reqBossGroup.shutdownGracefully();
            reqWorkerGroup.shutdownGracefully();
        }
    }

    public void sendMessage(Serializable object) {
        specialRequestChannel.write(object);
    }
}
