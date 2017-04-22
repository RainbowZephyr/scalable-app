package connections;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import load_balancer.Nginx;
import nginx.clojure.NginxHttpServerChannel;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeoutException;


/**
 * The class consumes messages off of the queue. Runnable to listen on port (on its own thread)
 *
 * @author syntx
 */
public class InboundMessageQueue implements Runnable, Consumer {

    private String queueName;
    private Channel channel;
    private Connection connection;
    private static Type NginxResponsesChannelsHashMapValueType =
            new TypeToken<NginxHttpServerChannel>(){}.getType();
    public InboundMessageQueue(String mqServerAddress, int mqServerPort, String queueName) {
        this.queueName = queueName;
        init(mqServerAddress, mqServerPort);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void init(String mqServerAddress, int mqServerPort){
        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();

        //host address of MQ server
        factory.setHost(mqServerAddress);
        factory.setPort(mqServerPort);

        //getting a connection
        try {
            connection = factory.newConnection();
            //creating a channel
            channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            boolean autoAck = true;
            channel.basicConsume(queueName, autoAck, this);
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
     * Called when new message is available. ( a request is ack only if it can be executed).
     */
    public void handleDelivery(String consumerTag, Envelope env,
                               BasicProperties props, byte[] body) throws IOException {
        // send back to the requester
        String jsonStr = (String) SerializationUtils.deserialize(body);
        String correlationId = props.getCorrelationId();
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(jsonStr, Map.class);
        // if a request, push in the corresponding queue
        if(isRequest(map)){
            Nginx.putInCorrespondingQueue(jsonStr, correlationId);
            return;
        }
        // get channel from the channel map
        NginxHttpServerChannel channel =
                Nginx.getChannelNginxSharedHashMap().get(correlationId);
        // correlationId = requestId for the request
        channel.sendResponse(200); // for now (l7ad ama a3raf ha3ml http response ezzai)
    }

    public void handleCancel(String consumerTag) {
    }

    public void handleCancelOk(String consumerTag) {
    }

    public void handleRecoverOk(String consumerTag) {
    }

    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {
    }

    private boolean isRequest(Map<String, Object> map) {
        return map.containsKey("request_parameters");
    }
}