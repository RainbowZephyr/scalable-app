package datastore.datastores;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.*;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by User Pc on 2017-04-13.
 */
public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static MongoCollection mongoCollection;
    private static GridFSBucket bucket;
    private static UUID imageID;
    private static Base64.Decoder decoder;
    private static Base64.Encoder encoder;

    public static void initalize(){
        mongoClient = new MongoClient("localhost");
        mongoDatabase = mongoClient.getDatabase("Images");
        bucket = GridFSBuckets.create(mongoDatabase);
    }
    public StringBuffer execute(Map<String, Object> parameters) {
        HashMap<String,Object> response=new HashMap<String,Object>();
        if(parameters.get("service_type").equals("addImage")){
            try {



                response.put("user_id",parameters.get("user_id"));
                response.put("session_id",parameters.get("session_id"));
                response.put("app_id",parameters.get("app_id"));
                response.put("recieving_app_id",parameters.get("recieving_app_id"));


                String imageBase64 = (String) ((Map)parameters.get("request_parameters").get("imageData"));

                store((Integer)parameters.get("user_id"), imageBase64);

                response.put("response_status","");
                response.put("response_load","");

            } catch (java.net.UnknownHostException e) {
                e.printStackTrace();
            } catch (MongoException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        if(parameters.get("service_type").equals("getImage")) {
            try {

                String image = (String) parameters.get("user_id");
                response.put("user_id", parameters.get("user_id"));
                response.put("session_id", parameters.get("session_id"));
                response.put("app_id", parameters.get("app_id"));
                response.put("recieving_app_id", parameters.get("recieving_app_id"));

                String base64 = retrieve((Integer)parameters.get("user_id"),(String)parameters.get("image_url"));

                HashMap<String, Object> imageData = new HashMap<String, Object>();

                imageData.put("imageData", base64);
                response.put("response_load", imageData);
                response.put("response_status", "");


                // remove the image file from mongoDB
                //gfsPhoto.remove(gfsPhoto.findOne(filename));
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
        if(parameters.get("service_type").equals("removeImage")) {
            try {

                String image = (String) parameters.get("user_id");
                response.put("user_id", parameters.get("user_id"));
                response.put("session_id", parameters.get("session_id"));
                response.put("app_id", parameters.get("app_id"));
                response.put("recieving_app_id", parameters.get("recieving_app_id"));

                delete((Integer)parameters.get("user_id"),(String) parameters.get("image_url"));

                HashMap<String, Object> imageData = new HashMap<String, Object>();
                imageData.put("imageData", "");
                response.put("response_load", imageData);
                response.put("response_status", "");


                // remove the image file from mongoDB
                //gfsPhoto.remove(gfsPhoto.findOne(filename));

            } catch (MongoException e) {

                e.printStackTrace();
            }
        }
        System.out.println("ECHO EXECUTED, CHECK the Response Queue");
        return new StringBuffer(response.toString());
    }

    private synchronized static String store(int userID, String base64) {
        try {
            imageID = UUID.randomUUID();
            String newFileName = userID + "/" + imageID;
            byte imageBinary[] = decoder.decode(base64);

            GridFSUploadStream uploadStream = bucket.openUploadStream(newFileName);
            uploadStream.write(imageBinary);
            uploadStream.close();
            return imageID.toString();
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    //takes UserId, unique image ID and gets the image from DB with filename in the form of UserID\\ImageId
    //Stores image onto computer on the path specified with the name ImageNewName
    private static String retrieve(int userID, String imageID) {
        try {
            GridFSDownloadStream downloadStream = bucket.openDownloadStream(userID + "/" + imageID);
            int fileLength = (int) downloadStream.getGridFSFile().getLength();
            byte[] imageBinary = new byte[fileLength];
            downloadStream.read(imageBinary);
            downloadStream.close();
            return encoder.encodeToString(imageBinary);
        } catch (MongoException e) {
            e.printStackTrace();
            return null;

        }
    }

    private synchronized static void delete(int userID, String imageID) {
        try {
            bucket.delete(findObjectID(userID, imageID));
        } catch (IllegalArgumentException e) {
            System.err.println("IMAGE NOT FOUND");
        }
    }

    private synchronized static ObjectId findObjectID(int userID, String imageID) {
        try {
            BasicDBObject query = new BasicDBObject("filename", userID + "/" + imageID);
            GridFSFindIterable files = bucket.find(query);
            return files.first().getObjectId();
        } catch (NullPointerException e) {
            return null;
        }
    }

}
