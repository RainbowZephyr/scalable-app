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

    private String queueName;
    private String JSON_MESSAGE = "application/json";
    private Channel channel; // need to serialize this
    private Connection connection;
    private AMQP.BasicProperties.Builder basicProperties;

    public OutboundMessageQueue(){

    }

    public OutboundMessageQueue(String mqServerAddress, int mqServerPort, String queueName) {
        try {
            this.queueName = queueName;
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
        basicProperties.correlationId(reqUUID);
        try {
            sendMessage(respose);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String repsonse) throws IOException {
        channel.basicPublish("", queueName, basicProperties.build(),
                SerializationUtils.serialize(repsonse.toString()));
    }
}
