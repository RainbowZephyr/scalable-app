package connections;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import load_balancer.Nginx;
import nginx.clojure.NginxClojureRT;
import nginx.clojure.NginxHttpServerChannel;
import nginx.clojure.java.ArrayMap;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static nginx.clojure.MiniConstants.NGX_HTTP_OK;


/**
 * The class consumes messages off of the queue. Runnable to listen on port (on its own thread)
 *
 * @author syntx
 */
public class InboundMessageQueue implements Runnable, Consumer {

    private String queueName;
    private Channel channel;
    private Connection connection;

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
        Gson gson = new Gson(); // will cause a memory leak
        Map<String, Object> map = gson.fromJson(jsonStr, Map.class);

        // if a request, push in the corresponding queue
        if(isRequest(map)){
            Nginx.putInCorrespondingQueue(jsonStr, correlationId);
            return;
        }

        NginxClojureRT.log.info("SharedMap : " + correlationId+ ":" + Nginx.getChannelNginxSharedHashMap());
        // get channel from the channel map
        NginxHttpServerChannel channel =
                Nginx.getChannelNginxSharedHashMap().remove(Long.parseLong(correlationId));
        NginxClojureRT.log.info("HERE : "+ channel);
        // correlationId = requestId for the request
        channel.sendResponse(new Object[] { NGX_HTTP_OK,
                        ArrayMap.create("content-type", "text/json"),
                        jsonStr});
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