package connections;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;


public class SocketConnectionToController implements SocketConnection {
    private static SocketConnectionToController instance = new SocketConnectionToController();
    private static String hostAddress;
    private static int specialPort;
    private ChannelHandlerContext ctx;
    private Channel specialRequestChannel;

    private SocketConnectionToController() {
    }

    public static SocketConnectionToController sharedInstance() {
        return instance;
    }

    public void init(String hostAddress, int port) throws InterruptedException, IOException {
        EventLoopGroup reqBossGroup = new NioEventLoopGroup(5);
        EventLoopGroup reqWorkerGroup = new NioEventLoopGroup();
        try {
            // server instance to listen on Special requests Port
            ServerBootstrap reqServerBootstrap = new ServerBootstrap();
            reqServerBootstrap.group(reqBossGroup, reqWorkerGroup);
            reqServerBootstrap.channel(NioServerSocketChannel.class);
            reqServerBootstrap.handler(new LoggingHandler(LogLevel.TRACE));
            // add the appropriate child handler
            reqServerBootstrap.childHandler(new ServerInitializer());
            // bind to special request port
            specialRequestChannel = reqServerBootstrap.bind(hostAddress,
                    port).sync().channel();
//            System.err.println("Listening For Controller Requests on http" + "://"+hostAddress+":" + port + '/');
            specialRequestChannel.closeFuture().sync();
        } finally {
            reqBossGroup.shutdownGracefully();
            reqWorkerGroup.shutdownGracefully();
        }
    }

    public void sendMessage(String response) {
        response += "\n";
        ctx.writeAndFlush(response);
    }
}
