package connections;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;


/**
 * Singleton Class that holds (all Requests Channels for responses, Outbound Queues)
 *
 * @author abdoo
 */
public class OutboundMessageQueue implements SocketConnection, Serializable {

    public String getQueueName() {
        return queueName;
    }

    public String getInstanceName(){
        return queueName.split("_")[0];
    }

    private String queueName, mqServerAddress;
    private int mqServerPort;
    private String JSON_MESSAGE = "application/json";
    private transient Channel channel; // need to serialize this
    private transient Connection connection;
    private transient AMQP.BasicProperties.Builder basicProperties;

    public OutboundMessageQueue(){

    }

    public OutboundMessageQueue(String mqServerAddress, int mqServerPort, String queueName) {
        try {
            this.queueName = queueName;
            this.mqServerAddress = mqServerAddress;
            this.mqServerPort = mqServerPort;
            init(mqServerAddress, mqServerPort);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void init(String mqServerAddress, int mqServerPort) throws IOException, TimeoutException {
        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();
        //hostAddress rabbitmq server
        factory.setHost(mqServerAddress);
        factory.setPort(mqServerPort);

        //getting a connection
        connection = factory.newConnection();
        //creating a channel
        channel = connection.createChannel();
        //declaring a queue for this channel. If queue does not exist,
        //it will be created on the server.
        channel.queueDeclare(queueName, false, false, false, null);
        basicProperties = new AMQP.BasicProperties.Builder();
        basicProperties.contentType(JSON_MESSAGE);
    }

    public void sendMessage(String respose, String reqUUID){
        // this next part of code is stupid , I know that bs I have no time to
        // figure out how to serialize a 3rd party class
        // Reinitialize the connection everytime sending a message
        try {
            init(mqServerAddress, mqServerPort);
            basicProperties.correlationId(reqUUID);
            sendMessage(respose);
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String repsonse) throws IOException {
        channel.basicPublish("", queueName, basicProperties.build(),
                SerializationUtils.serialize(repsonse.toString()));
    }

    public int getCount(){
        try {
            return channel.queueDeclarePassive(queueName).getMessageCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
