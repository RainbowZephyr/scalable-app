package GridFS;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.*;
import org.bson.types.ObjectId;

import java.util.Base64;
import java.util.UUID;

public class DataBaseOperations {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static MongoCollection mongoCollection;
    private static GridFSBucket bucket;
    private static UUID imageID;
    private static Base64.Decoder decoder;
    private static Base64.Encoder encoder;


    // takes UserId and path of the image.
    //creates a unique ID for the image.
    //stores image from the path into DB with a filename in the form of UserId\\uniqueId

    public static void initalize() {
        mongoClient = new MongoClient("localhost");
        mongoDatabase = mongoClient.getDatabase("Images");
        bucket = GridFSBuckets.create(mongoDatabase);
    }

    public synchronized static String store(int userID, String base64) {
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
    public static String retrieve(int userID, String imageID) {
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

    public synchronized static void delete(int userID, String imageID) {
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


//    public static void main(String[] args) {
//
//        byte d[] = new byte[0];
//        try {
//            d = Files.readAllBytes(Paths.get("TSWBG.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        initalize();
//        System.out.println(findObjectID(1, "6d098738-16c6-4e43-ab2e-81f30aa794c5"));
//        delete(1, "6d098738-16c6-4e43-ab2e-81f30aa794c5");
//        System.out.println(findObjectID(1, "6d098738-16c6-4e43-ab2e-81f30aa794c5"));
//    }
}

