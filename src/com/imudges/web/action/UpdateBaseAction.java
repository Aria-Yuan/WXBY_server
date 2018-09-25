package com.imudges.web.action;

import com.circle.web.database.database.MongoDBUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.opensymphony.xwork2.ActionSupport;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class UpdateBaseAction extends ActionSupport {
    /**
     * 返回成功的结果
     * */
    protected Map<String,Object> getSuccessResult(Object data){
        Map<String,Object>result = new HashMap<>();
        result.put("code",1);
        result.put("msg","ok");
        result.put("data",data);
        return result;
    }
    /**
     * 返回失败的结果
     * */
    protected Map<String,Object>getFailResult(int code,String msg){
        Map<String,Object>result = new HashMap<>();
        result.put("code",code+"");
        result.put("msg",msg);
        result.put("data",null);
        return result;
    }

    /**
     * 返回搜寻的结果
     * */
    protected Map<String, Object> getResult(String type, String condition, String searchType){
        Map<String, Object> result = new HashMap<>();
        switch (type){
            case "counseling":{
                MongoDBUtil mongoDb = new MongoDBUtil("wxby");
                MongoCollection<Document> collection = mongoDb.getCollection("legal_counseling");
                Document target = new Document();
                if(searchType.equals("0")){//更新浏览量
                    target = Document.parse(condition);
                    target.append("_id",new ObjectId(target.getString("_id")));
                    Document oldOne = collection.find(new Document().append("_id",target.getObjectId("_id"))).first();
                    System.out.println("我是中文" + oldOne);
                    System.out.println("我是法文" + target);
                    collection.updateOne(oldOne,new Document("$set",target));
                    Document newOne = collection.find(new Document().append("_id",target.getObjectId("_id"))).first();
                    result.putAll(newOne);
                }else if(searchType.equals("1")){//更新提问
                    MongoCollection<Document> collection_l = mongoDb.getCollection("lawyer");
                    target = Document.parse(condition);
                    target.append("_id",new ObjectId(target.getString("_id")));
                    Document oldOne = collection.find(new Document().append("_id",target.getObjectId("_id"))).first();
                    collection.updateOne(oldOne,new Document("$set",target));
                    System.out.println(target);
                    Document newOne = collection.find(new Document().append("_id",target.getObjectId("_id"))).first();
                    newOne.append("questioner",newOne.getObjectId("questioner").toString());
                    MongoCursor<Document> lawyerCursor = collection_l.find(new Document("_id",newOne.getObjectId("lawyer"))).iterator();
                    Document lawyer = lawyerCursor.next();
                    lawyer.put("_id",lawyer.getObjectId("_id").toString());
                    lawyer.put("reg_id",lawyer.getObjectId("reg_id").toString());
                    newOne.put("lawyer", lawyer);
                    result.putAll(newOne);
                    }
                else{
                    result.putAll(target);
                }
            }
        }
        return result;
    }

}