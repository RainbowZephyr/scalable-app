import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestControllerCommands {
    final static String CONTROLLER_HOST = "127.0.0.1";
    final static int CONTROLLER_PORT = 6002;

    public static void main(String[]args) throws IOException, InterruptedException {
        bootNettyClient(CONTROLLER_HOST, CONTROLLER_PORT);
    }


    private static void bootNettyClient(String host, int port) throws
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
                    ch.pipeline().addLast(new ApplicationResponseHandler());
                }
            });

            // Start the client.
            io.netty.channel.Channel channel = b.connect(host, port).sync().channel(); // (5)

            ChannelFuture lastWriteFuture = null;
            System.out.println("Paste in your formatted JSON & press Enter(Return) twice...");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            StringBuffer toBeSentData = new StringBuffer();
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                if(line.isEmpty()){
                    toBeSentData.append("\n");
                    lastWriteFuture = channel.writeAndFlush( toBeSentData.toString() );
                    toBeSentData.setLength(0);
                    System.out.println("SENT COMMAND TO CONTROLLER.");
                }
                // Sends the received line to the server.
                toBeSentData.append(line);
            }



            // Wait until the connection is closed.
            channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    // NETTY CONNECTION
    static class ApplicationResponseHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("APPLICATION RESPONDED WITH: \n"+ msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
