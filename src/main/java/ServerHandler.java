import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.bson.Document;

/**
 * Created by amr on 3/31/17.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private  MongoDatabase db;


    public ServerHandler(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        MongoCollection<Document> coll = db.getCollection("yoyo");

        Document document = new Document();

        document.put("name", "Amr Abu Greedah is in the handler for the last time!");

        System.out.println(document);
        coll.insertOne(document);

        System.out.println("Hi, i am the channelReadMethod in the ServerHandler and i just got this message>>>>>" + msg);
        ctx.writeAndFlush("hi");


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }
}

