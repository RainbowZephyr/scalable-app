import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.util.UUID;

public class DataBaseOperations {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static MongoCollection mongoCollection;
    private static GridFSBucket bucket;


    private UUID imageID;


    // takes UserId and path of the image.
    //creates a unique ID for the image.
    //stores image from the path into DB with a filename in the form of UserId\\uniqueId

    public static void initalize(){
        mongoClient = new MongoClient("localhost");
        mongoDatabase = mongoClient.getDatabase("Images");
        bucket = GridFSBuckets.create(mongoDatabase);
    }

    public static void Store(int UserId, String path) {
        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            MongoDatabase db = mongo.getDatabase("imagedb");
            MongoCollection collection = db.getCollection("dummyColl");
            UUID uniqueKey = UUID.randomUUID();

            String newFileName = UserId + "\\" + uniqueKey;

            File imageFile = new File(path);
            // create a "photo" namespace
            GridFSBucket gfsPhoto = GridFSBuckets.create(db, "photo");

            // get image file from local drive
            GridFSInputFile gfsFile = gfsPhoto.createFile(imageFile);

            // set a new filename for identify purpose
            gfsFile.setFilename(newFileName);

            // print the result
            DBCursor cursor = gfsPhoto.getFileList();
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }            // save the image file into mongoDB
            gfsFile.save();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //takes UserId, unique image ID and gets the image from DB with filename in the form of UserID\\ImageId
    //Stores image onto computer on the path specified with the name ImageNewName
    public static void Retrieve(int UserId, String ImageId, String path, String ImageNewName) {
        try {
            Mongo mongo = new Mongo("localhost", 27017);
            DB db = mongo.getDB("imagedb");
            DBCollection collection = db.getCollection("dummyColl");
            String filename = UserId + "\\" + ImageId;
            GridFS gfsPhoto = new GridFS(db, "photo");
            // get image file by it's filename
            GridFSDBFile imageForOutput = gfsPhoto.findOne(filename);


            File dir = new File(path);
            dir.mkdir();
            path += "\\" + ImageNewName + ".png";
            // save it into a new image file
            imageForOutput.writeTo(path);

            // remove the image file from mongoDB
            //gfsPhoto.remove(gfsPhoto.findOne(filename));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {

		/*try {

			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("imagedb");
			DBCollection collection = db.getCollection("dummyColl");

			String newFileName = "mkyong-java-image";

			File imageFile = new File("d:\\JavaWebHosting.png");

			// create a "photo" namespace
			GridFS gfsPhoto = new GridFS(db, "photo");

			// get image file from local drive
			GridFSInputFile gfsFile = gfsPhoto.createFile(imageFile);

			// set a new filename for identify purpose
			gfsFile.setFilename(newFileName);

			// save the image file into mongoDB
			gfsFile.save();

			// print the result
			DBCursor cursor = gfsPhoto.getFileList();
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}

			// get image file by it's filename
			GridFSDBFile imageForOutput = gfsPhoto.findOne(newFileName);

			// save it into a new image file
			imageForOutput.writeTo("d:\\JavaWebHostingNew.png");

			// remove the image file from mongoDB
			gfsPhoto.remove(gfsPhoto.findOne(newFileName));

			System.out.println("Done");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
        Store(2, "d:\\img1.png");
        Retrieve(2, "1f8540e0-a0d2-41ab-8c65-14bd590b6471", "d:\\newFolder", "newimage");

    }
}

