package connections;

import com.google.gson.Gson;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.Thread.sleep;


public class SocketConnectionToController implements SocketConnection, Runnable{
    private static SocketConnectionToController instance = new SocketConnectionToController();
    private static String hostAddress;
    private static int specialPort;
    private ChannelHandlerContext ctx;
    private Thread sendingThread;
    private Runnable runnableTask;
    private volatile boolean isSend;
    private final String DELIMITER = ";";
    private final String appInstanceConfPath = "loadbalancer/config/apps_instances.properties";
    private final String messageQueueConfPath = "loadbalancer/config/message_queue_server.properties";
    private Map<String, Integer> statisticsJson = new HashMap<>();
    private Gson gson = new Gson();

    private Map<String, OutboundMessageQueue> messageQueueMap = new HashMap<>();


    private SocketConnectionToController() {
    }

    public void setControllerRemoteAddress(String hostAddress, int port){
        SocketConnectionToController.hostAddress = hostAddress;
        specialPort = port;
    }

    public static SocketConnectionToController sharedInstance() {
        return instance;
    }

    public void init() {
        // init task for writing to controller
        runnableTask = new Runnable() {
            @Override
            public void run() {
                while(isSend){
                    try {
                        sleep(1000);
                        // send data here
                        SocketConnectionToController.sharedInstance().sendMessage(getStatistics());
                    } catch (InterruptedException e) {
                        // if sleep is interrupted, so what ?
                    }
                }
            }
        };
        //init data to be sent
        loadMessageQueues();
        // init server
        EventLoopGroup reqBossGroup = new NioEventLoopGroup(5);
        EventLoopGroup reqWorkerGroup = new NioEventLoopGroup();
        // server instance to listen on Special requests Port
        ServerBootstrap reqServerBootstrap = new ServerBootstrap();
        reqServerBootstrap.group(reqBossGroup, reqWorkerGroup);
        reqServerBootstrap.channel(NioServerSocketChannel.class);
        reqServerBootstrap.handler(new LoggingHandler(LogLevel.TRACE));
        // add the appropriate child handler
        reqServerBootstrap.childHandler(new ServerInitializer());
        // bind to special request port

        try {
            reqServerBootstrap.bind(hostAddress,
                    specialPort).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String response) {
        response += "\n";
        ctx.writeAndFlush(response);
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        init();
    }

    public void stopSendingDataToController() {
        // interrupt thread
        isSend = false;
        sendingThread.interrupt();
    }

    public void startSendingDataToController() {
        sendingThread = new Thread(runnableTask);
        isSend = true;
        sendingThread.start();
    }

    public boolean isSending() {
        return isSend;
    }

    private void readQueueNamesIntoMemory(String mqServerAddress, int mqServerPort) {
        // load the names of the instances into sharedHashMap
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(appInstanceConfPath);
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration e = prop.propertyNames();
        // write to the shared map
        while(e.hasMoreElements()){
            String key = (String) e.nextElement();
            String [] temp = prop.getProperty(key).split(DELIMITER);

            for(int i=0; i<temp.length; i++){
                String mqOutboundQueueName = temp[i].split("=")[0] + "_InboundQueue"; // inbound for the instance
                messageQueueMap.put(mqOutboundQueueName,
                        new OutboundMessageQueue(
                                mqServerAddress,
                                mqServerPort,
                                mqOutboundQueueName)
                );
            }
        }
    }

    private void loadMessageQueues() {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(messageQueueConfPath);
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String mqServerAddress = prop.getProperty("MessageQueueServerAddress");
        int mqServerPort = Integer.parseInt(prop.getProperty("MessageQueueServerPort"));
        readQueueNamesIntoMemory(mqServerAddress, mqServerPort);
    }

    private String getStatistics(){
        for(Map.Entry<String, OutboundMessageQueue> entry: messageQueueMap.entrySet()){
            statisticsJson.put(entry.getKey(), entry.getValue().getCount());
        }
        return gson.toJson(statisticsJson);
    }
}
