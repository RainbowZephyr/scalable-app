package connections;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import exceptions.MultipleResponseException;
import org.apache.commons.lang.SerializationUtils;
import services.Dispatcher;
import services.RequestHandle;
import services.ServiceRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static utility.Constants.*;


/**
 * The class consumes messages off of the queue. Runnable to listen on port (on its own thread)
 * @author syntx
 *
 */
public class QueueConsumer implements Runnable, Consumer{
    private static QueueConsumer instance = new QueueConsumer();
    // default vlaues
    private String QUEUE_NAME;
    private String MQ_SERVER_ADDRESS;
    private int MQ_SERVER_PORT; // default port(change from rabbitMq config file 8albn fi /etc/rabbitMQ/config
    private Channel channel;
    private Connection connection;

    public String getQUEUE_NAME() {
        return QUEUE_NAME;
    }

    public void setQUEUE_NAME(String QUEUE_NAME) {
        this.QUEUE_NAME = QUEUE_NAME;
    }

    public String getMQ_SERVER_ADDRESS() {
        return MQ_SERVER_ADDRESS;
    }

    public void setMQ_SERVER_ADDRESS(String MQ_SERVER_ADDRESS) {
        this.MQ_SERVER_ADDRESS = MQ_SERVER_ADDRESS;
    }

    public int getMQ_SERVER_PORT() {
        return MQ_SERVER_PORT;
    }

    public void setMQ_SERVER_PORT(int MQ_SERVER_PORT) {
        this.MQ_SERVER_PORT = MQ_SERVER_PORT;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private QueueConsumer(){}
    public static QueueConsumer sharedInstance(){
        return instance;
    }

    public void init() throws IOException, TimeoutException {
        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();
        loadConfig();

        //host address of MQ server
        factory.setHost(MQ_SERVER_ADDRESS);
        factory.setPort(MQ_SERVER_PORT);

        //getting a connection
        connection = factory.newConnection();

        //creating a channel
        channel = connection.createChannel();
    }

    private void loadConfig() throws IOException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream("config/message_queues.properties");
        prop.load(in);
        in.close();
        MQ_SERVER_ADDRESS = prop.getProperty(this.getClass().getSimpleName() + "_HOST");
        MQ_SERVER_PORT = Integer.parseInt(
                prop.getProperty(this.getClass().getSimpleName() + "_PORT"));
        QUEUE_NAME = prop.getProperty(this.getClass().getSimpleName() + "_QUEUE");
    }

    public void run() {
        try {
            boolean autoAck = true;
            channel.basicConsume(QUEUE_NAME, autoAck,this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when consumer is registered.
     */
    public void handleConsumeOk(String consumerTag) {
    }

    /**
     * Called when new message is available.
     */
    public void handleDelivery(String consumerTag, Envelope env,
                               BasicProperties props, byte[] body) throws IOException {
        String jsonStr = (String)SerializationUtils.deserialize(body);

        // parse JSONString
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(jsonStr, Map.class);
        // Construct Service Request
        ServiceRequest serviceRequest = constructReq(map);
        try {
            Dispatcher.sharedInstance().dispatchRequest(new RequestHandle(
                    Producer.class.getSimpleName()), serviceRequest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MultipleResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServiceRequest constructReq(Map<String, Object> request) {
        String sessionId = (String) request.get(SESSION_ID_KEY);
        String appId = (String) request.get(APP_ID_KEY);
        String receivingAppId = (String) request.get(RECEIVING_APP_ID_KEY);
        String strAction = (String) request.get(ACTION_NAME_KEY);
        Map<String, Object> requestParams = (Map<String, Object>)
                request.get(REQUEST_PARAMETERS_KEY);
        return new ServiceRequest(strAction, sessionId, requestParams);
    }

    public void handleCancel(String consumerTag) {}
    public void handleCancelOk(String consumerTag) {}
    public void handleRecoverOk(String consumerTag) {}
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}
}