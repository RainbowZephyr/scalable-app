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

        ////////////// parse to json

        JsonParserFactory factory = JsonParserFactory.getInstance();
        JSONParser parser = factory.newJsonParser();
        Map jsonData = parser.parseJson(json);
        /////////////

        String service_type = (String) jsonData.get("service_type");                     // get service type value
        String request_parameters =  (String) jsonData.get("request_parameters");        // get request parameter value
        Map json_request_parameters = parser.parseJson(request_parameters);              // parse request parameter to json

        if(service_type == "add_comment_request"){   // if the coming command is the wallApp_add_comment_request

            String user_id = (String) json_request_parameters.get("user_id");             
            String post_id = (String) json_request_parameters.get("post_id");
            String comment_text = (String) json_request_parameters.get("comment_text");


            MongoCollection<Document> coll = db.getCollection("addCommentCollection");
            Document document = new Document();
            document.put("user_id", user_id);
            document.put("post_id", post_id);
            document.put("comment_tex", comment_text);
            coll.insertOne(document);

            ////// create wallApp_comment_response json then         ctx.writeAndFlush(thisJson);


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

