package connections;

import com.google.gson.Gson;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import load_balancer.StatisticsRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private final String appInstanceConfPath = "/home/abdoo/IdeaProjects/scalable-app/loadbalancer/config/apps_instances.properties";
    private ConcurrentHashMap<String, Integer> statisticsJson = new ConcurrentHashMap<>();
    private List<StatisticsRecord> statisticsJsonList = new ArrayList<>();
    private Map<String, List<StatisticsRecord>> jsonMap = new HashMap<>();
    private Gson gson = new Gson();

    public ConcurrentHashMap<String, Integer> getStatisticsMap() {
        return statisticsJson;
    }

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
                        sleep(5000); // send every 30 sec
                        // send data here
                        SocketConnectionToController.sharedInstance().sendMessage(getStatistics(5000));
                        resetStatisticsHashMap();
                    } catch (InterruptedException e) {
                        // if sleep is interrupted, so what ?
                    }
                }
            }
        };
        //init data to be sent
        readQueueNamesIntoMemory();
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

    private void readQueueNamesIntoMemory() {
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
                String instanceName = temp[i].split("=")[0]; // inbound for the instance
                statisticsJson.put(instanceName, 0);
            }
        }
    }

    private String getStatistics(int seconds){
        for (Map.Entry<String, Integer> entry : statisticsJson.entrySet()) {
            statisticsJsonList.add(new StatisticsRecord(entry.getKey(), entry.getValue()));
        }
        jsonMap.put("content",statisticsJsonList);
        return gson.toJson(jsonMap);
    }

    private void resetStatisticsHashMap(){
        for (Map.Entry<String, Integer> entry : statisticsJson.entrySet()) {
            statisticsJson.put(entry.getKey(), 0);
        }
    }
}
