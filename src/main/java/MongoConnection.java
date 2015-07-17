/**
 * Created by Aleksandr on 16.07.2015.
 */
//import java.sql.*;
//import java.sql.DriverManager;
//import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.net.UnknownHostException;
import com.mongodb.*;

public class MongoConnection implements Recordable {
    public final String HOST = "ds055842.mongolab.com";
    public final String PORT = "55842";
    public final String DBNAME = "doubletrouble";

    private final String USER;
    private final String PASS;
    MongoClientURI uri;
    MongoClient client;
    DB db;
    DBCollection rec;
    int id;
    private boolean isConnectionSuccessful;

    public MongoConnection(String user, String pass) throws UnknownHostException{
        this.USER = user;
        this.PASS = pass;
        initConnection();
    }


    private void initConnection() throws UnknownHostException{
        try {
            uri  = new MongoClientURI("mongodb://" + USER + ":" + PASS +
                                                     "@" + HOST + ":" + PORT + "/" + DBNAME);
            client = new MongoClient(uri);
            db = client.getDB(uri.getDatabase());
            rec = db.getCollection("rec");
            isConnectionSuccessful = true;
        } catch (UnknownHostException e) {
            System.out.println("Connection Failed! Due to next reasons: " + e.getMessage());
            e.printStackTrace();
            isConnectionSuccessful = false;
        }
    }

    private static int getLastId() throws UnknownHostException{
        // GETTING THE NEXT ID NUMBER FROM ANOTHER DOCUMENT
        int id=0;
        /*BasicDBObject ids = new BasicDBObject();
        ids.put("buf", "buf");
        ids.put("lastid", id);
        final BasicDBObject[] seedData1 = {ids};*/

        // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname

        MongoClientURI uri1  = new MongoClientURI("mongodb://DT:DoubleTrouble@ds055842.mongolab.com:55842/doubletrouble");
        MongoClient client1 = new MongoClient(uri1);
        DB db = client1.getDB(uri1.getDatabase());

        DBCollection ids1 = db.getCollection("ids1");

        BasicDBObject findQuery = new BasicDBObject("buf", new BasicDBObject("$eq", "buf"));

        /*DBCursor cur = ids1.find();
        while(cur.hasNext()) {
            System.out.println(cur.next());
        }
        BasicDBObject query1 = new BasicDBObject();
        query1.put("i", 71);
        DBCursor cur = ids1.find(query1);
        while(cur.hasNext()) {
            System.out.println(cur.next());
        }*/

        //BasicDBObject orderBy = new BasicDBObject("decade", 1);

        DBCursor docs = ids1.find(findQuery);//.sort(orderBy);

        while(docs.hasNext()){
            DBObject doc = docs.next();
            System.out.println(
                    "Last id =  " + doc.get("lastid")
            );
            id = Integer.parseInt(doc.get("lastid").toString());
        }

        client1.close();
        // END

        return id;
    }

    public static BasicDBObject[] createSeedData(String record) throws UnknownHostException{

        int id = getLastId();
        BasicDBObject rec1 = new BasicDBObject();
        rec1.put("id", id);
        rec1.put("record", record);

        final BasicDBObject[] seedData = {rec1};

        return seedData;
    }

    //@Override

    public boolean setRecord(String record){
        if (record == null || record.length() == 0) {
            System.out.println("Incorrect record!");
            return false;
        }
        if (!testConnection()) {
            return false;
        }
        try {
            BasicDBObject[] seedData = createSeedData(record);
            rec.insert(seedData);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    //@Override
    public List<String> searchRecords(String searchWord) {
     /*   if (testConnection()) {
          //  throw new DBException();
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String sql = "select record from records where id=" + id;
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getString("record");
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return new ArrayList<String>();
    }

    //@Override
    public String getRecord(int Gottenid) throws DBException {
        if (!testConnection()) {
            throw new DBException();
        }
        try {

            BasicDBObject findQuery = new BasicDBObject("id", new BasicDBObject("$eq", Gottenid));

        /*DBCursor cur = ids1.find();
        while(cur.hasNext()) {
            System.out.println(cur.next());
        }
        BasicDBObject query1 = new BasicDBObject();
        query1.put("i", 71);
        DBCursor cur = ids1.find(query1);
        while(cur.hasNext()) {
            System.out.println(cur.next());
        }*/

            //BasicDBObject orderBy = new BasicDBObject("decade", 1);

            DBCursor docs = rec.find(findQuery);//.sort(orderBy);

            while(docs.hasNext()) {
                DBObject doc = docs.next();
                System.out.println(
                        "Gotcha! Record =  " + doc.get("record")
                );
                return doc.get("lastid").toString();
            }
            }
            catch (MongoException e) {
                e.printStackTrace();
            }
        return "";
    }


    // What is this?
    private boolean testConnection() {
        try {
            Class.forName("mongo-java-driver-2.13.2");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MongoDB Driver? Include in your library path!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //@Override
    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }
}