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

import java.util.Map;

public class main {
    //private final static int REQUEST_PORT = 6001, SPECIAL_PORT = 6002;
    private final static String HOST = " 127.0.0.1";

    public static void main(String[] args) throws Exception {
        SocketConnectionToController.sharedInstance().init();

    }

}
