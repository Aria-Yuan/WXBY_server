package com.circle.web.database.database;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public class MongoDBUtil {
    private static final MongoClientURI mongoDBurl = new MongoClientURI("" +
            "mongodb://dajiayiqibiye:wxby@wxby-shard-00-00-7ea9c.mongodb.net:27017,wxby-shard-00-01-7ea9c.mongodb.net:27017,wxby-shard-00-02-7ea9c.mongodb.net:27017/test?ssl=true&replicaSet=WXBY-shard-0&authSource=admin&retryWrites=true");
    private static MongoClient mongoClient = null;
    private static  MongoDatabase db = null;

    //初始化连接
    public MongoDBUtil(String dbName)
        {
        try{
            // 连接到 mongodb 服务
            mongoClient = new MongoClient(mongoDBurl);

            // 连接到数据库
            db = mongoClient.getDatabase(dbName);
            System.out.println("Connect to database successfully");

        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    //连接集合
    public MongoCollection<Document> getCollection(String colName)
    {
        return db.getCollection(colName);
    }


}
