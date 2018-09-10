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

/**
 * Created by HUPENG on 2017/4/30.
 */
public class BaseAction extends ActionSupport{
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
        protected List<Map<String, Object>> getResult(String type, String condition){
            List<Map<String, Object>> result = new ArrayList<>();
            switch (type){
                case "firm":{
                    MongoDBUtil mongoDb = new MongoDBUtil("wxby");
                    MongoCollection<Document> collection = mongoDb.getCollection("law_firm");
                    MongoCursor<Document> cursor = collection.find().limit(3).iterator();
                    while (cursor.hasNext()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.putAll(cursor.next());
                        result.add(map);
                    }
                }break;
                case "counseling":{
                    MongoDBUtil mongoDb = new MongoDBUtil("wxby");
                    MongoCollection<Document> collection = mongoDb.getCollection("legal_counseling");
                    MongoCursor<Document> cursor = collection.find().limit(3).iterator();
                    while (cursor.hasNext()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.putAll(cursor.next());
                        result.add(map);
                    }
                }
            }

            return result;
    }

}