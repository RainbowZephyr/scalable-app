import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.bson.Document;

import java.util.Map;

/**
 * Created by amr on 3/31/17.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private MongoDatabase db;

    public ServerHandler(MongoDatabase db) {
        this.db = db;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //////////// read msg

        ByteBuf in = (ByteBuf) msg;
        String json = "";

        try {
            while (in.isReadable()) { // (1)
                json = json + (char) in.readByte();
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
        //{"session_id":"null", "app_id":"null", "receiving_app_id":"null", "service_type": "add_comment_request", "request_parameters":{"user_id":6, "post_id": 6, "comment_text":"yaaay!"}}
        ////////////// parse to json
        System.out.println(json);
        JsonParserFactory factory = JsonParserFactory.getInstance();
        JSONParser parser = factory.newJsonParser();
        Map jsonData = parser.parseJson(json);
        System.out.println(jsonData);
        /////////////

        String service_type = (String) jsonData.get("service_type");                     // get service type value
        System.out.println("service type is " + service_type);
        Map request_parameters = (Map) jsonData.get("request_parameters");        // get request parameter value


        if(service_type.equals("add_comment_request")){   // if the coming command is the wallApp_add_comment_request
            System.out.println("Yoyoyo!");
            String user_id = (String) request_parameters.get("user_id");
            String post_id = (String) request_parameters.get("post_id");
            String comment_text = (String) request_parameters.get("comment_text");


            MongoCollection<Document> coll = db.getCollection("addCommentCollection");
            Document document = new Document();
            document.put("user_id", user_id);
            document.put("post_id", post_id);
            document.put("comment_tex", comment_text);
            System.out.println("Hii there!");
            coll.insertOne(document);

            ctx.writeAndFlush(document);

            //// create wallApp_comment_response json then         ctx.writeAndFlush(thisJson);


        }





        //////////
//        MongoCollection<Document> coll = db.getCollection("yoyo");
//
//        Document document = new Document();
//
//        document.put("name", "Amr Abu Greedah is in the handler!");
//
//        System.out.println(document);
//        coll.insertOne(document);
//
//        System.out.println("Hi, i am the channelReadMethod in the ServerHandler and i just got this message>>>>>" + msg);
      //  ctx.writeAndFlush("hi");


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }
}

