package com.imudges.web.action;

import com.circle.web.database.database.MongoDBUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.opensymphony.xwork2.ActionSupport;
import org.bson.Document;

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
        result.put("code",0);
        result.put("msg","ok");
        result.put("data",data);
        return result;
    }
    /**
     * 返回失败的结果
     * */
    protected Map<String,Object>getFailResult(int code,String msg){
        Map<String,Object>result = new HashMap<>();
        result.put("code",code);
        result.put("msg",msg);
        result.put("data",null);
        return result;
    }

    /**
     * 返回搜寻的结果
     * */
    protected Map<String, Object> getResult(String type, Document target, String searchType){
        Map<String, Object> result = new HashMap<>();
        switch (type){
            case "counseling":{
                MongoDBUtil mongoDb = new MongoDBUtil("wxby");
                MongoCollection<Document> collection = mongoDb.getCollection("legal_counseling");
                MongoCursor<Document> cursor;
                if(searchType.equals("0")){//更新
                    Document oldOne = collection.find(new Document().append("_id",target.getString("_id"))).first();
                    collection.updateOne(oldOne,target);
                    Document newOne = collection.find(new Document().append("_id",target.getString("_id"))).first();
                    result.putAll(newOne);
                }else{
                    result.putAll(target);
                }
            }
        }
        return result;
    }

}