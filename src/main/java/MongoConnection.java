/**
 * Created by Aleksandr on 16.07.2015.
 */
//import java.sql.*;
//import java.sql.DriverManager;
//import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.UnknownHostException;
import java.util.Map;

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

        MongoClientURI uri1  = new MongoClientURI("mongodb://DT:DoubleTrouble@ds055842.mongolab.com:55842/doubletrouble");
        MongoClient client1 = new MongoClient(uri1);
        DB db = client1.getDB(uri1.getDatabase());

        DBCollection ids1 = db.getCollection("ids");

        BasicDBObject findQuery = new BasicDBObject("buf", new BasicDBObject("$eq", "buf"));

        DBCursor docs = ids1.find(findQuery);

        while(docs.hasNext()){
            DBObject doc = docs.next();
            System.out.println(
                    "Last id =  " + doc.get("lastid")
            );
            id = Integer.parseInt(doc.get("lastid").toString());
        }

        id++;

        BasicDBObject updateQuery = new BasicDBObject("buf", "buf");
        ids1.update(updateQuery, new BasicDBObject("$set", new BasicDBObject("lastid", id)));

        client1.close();

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
    public Map<String, String> searchRecords(String searchWord) {
        try {
            BasicDBObject findQuery = new BasicDBObject("record", new BasicDBObject("%regex", searchWord));
            DBCursor docs = rec.find(findQuery);//.sort(orderBy);
            Map<String, String> records = new HashMap<String, String>();
            while (docs.hasNext()) {
                DBObject doc = docs.next();
                System.out.println(
                        "Gotcha! Record[" + doc.get("id") + "] = " + doc.get("record")
                );
                records.put(doc.get("id").toString(), doc.get("record").toString());
            }
            return records;
        }
        catch (MongoException e) {
            e.printStackTrace();
            return null;
        }
    }

    //@Override
    public String getRecord(int Gottenid) {
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
                return doc.get("record").toString();
            }
            }
            catch (MongoException e) {
                e.printStackTrace();
            }
        return "";
    }

    //@Override
    public Map<String, String> getRecords() {
        try {
            BasicDBObject findQuery = new BasicDBObject();
            BasicDBObject orderBy = new BasicDBObject("id", 1);
            DBCursor docs = rec.find(findQuery).sort(orderBy);
            Map<String, String> records = new HashMap<String, String>();
            while (docs.hasNext()) {
                DBObject doc = docs.next();
                System.out.println(
                        "Gotcha! Record[" + doc.get("id") + " = " + doc.get("record")
                );
                records.put(doc.get("id").toString(), doc.get("record").toString());
            }
            return records;
        }
        catch (MongoException e) {
            e.printStackTrace();
            return null;
        }
    }

    //@Override
    public boolean deleteRecord(int Gottenid) {
        try {

            BasicDBObject findQuery = new BasicDBObject("id", new BasicDBObject("$eq", Gottenid));

            DBCursor docs = rec.find(findQuery);

            while(docs.hasNext()) {
                DBObject doc = docs.next();
                System.out.println(
                        "Gotcha! We deleting record =  " + doc.get("record")
                );
                doc.get("record").toString();
                rec.remove(doc);
            }
            return true;
        }
        catch (MongoException e) {
            e.printStackTrace();
            return false;
        }
    }

    //@Override
    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }
}