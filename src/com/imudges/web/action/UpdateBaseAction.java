package com.imudges.web.action;

import com.circle.web.database.database.MongoDBUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.opensymphony.xwork2.ActionSupport;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.UnsupportedEncodingException;
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
    protected List<Map<String, Object>> getResult(String type, String condition, String searchType) throws UnsupportedEncodingException {
//        condition = new String(condition.getBytes("ISO-8859-1"),"UTF-8");
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        switch (type){
            case "counseling":{
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
                    newOne.append("_id",newOne.getObjectId("_id").toString());
                    Map<String, Object> a = new HashMap<>();
                    a.putAll(newOne);
                    result.add(a);
                }else if(searchType.equals("1")){//更新提问
                    MongoCollection<Document> collection_l = mongoDb.getCollection("lawyer");
                    MongoCollection<Document> collection_r = mongoDb.getCollection("register");
                    System.out.println(condition);
                    target = Document.parse(condition);
                    target.append("_id",new ObjectId(target.getString("_id")));
                    Document oldOne = collection.find(new Document().append("_id",target.getObjectId("_id"))).first();
                    collection.updateOne(oldOne,new Document("$set",target));
                    System.out.println(target);
                    Document newOne = collection.find(new Document().append("_id",target.getObjectId("_id"))).first();
                    newOne.append("questioner",collection_r.find(new Document("_id",newOne.getObjectId("questioner"))).iterator().next().getString("name"));
                    MongoCursor<Document> lawyerCursor = collection_l.find(new Document("_id",newOne.getObjectId("lawyer"))).iterator();
                    Document lawyer = lawyerCursor.next();
                    lawyer.put("_id",lawyer.getObjectId("_id").toString());
                    lawyer.put("reg_id",lawyer.getObjectId("reg_id").toString());
                    newOne.put("lawyer", lawyer);
                    newOne.append("_id",newOne.getObjectId("_id").toString());
                    Map<String, Object> a = new HashMap<>();
                    a.putAll(newOne);
                    result.add(a);
                    }
                else{
                    Map<String, Object> a = new HashMap<>();
                    a.put("msg","请求错误");
                    result.add(a);
                }
            }
        }
        mongoDb.close();
        return result;
    }

}