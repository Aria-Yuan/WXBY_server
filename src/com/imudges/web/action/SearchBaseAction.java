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

/**
 * Created by HUPENG on 2017/4/30.
 */
public class SearchBaseAction extends ActionSupport{
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
        protected List<Map<String, Object>> getResult(String type, String keyWord, String searchType){
            List<Map<String, Object>> result = new ArrayList<>();
            switch (type){
                case "firm":{
                    MongoDBUtil mongoDb = new MongoDBUtil("wxby");
                    MongoCollection<Document> collection = mongoDb.getCollection("law_firm");
                    MongoCursor<Document> cursor;
                    if(searchType.equals("0")){//关键字搜寻
                        List<Document> condition = new ArrayList<>();
                        //设置正则表达
                        Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
                        condition.add(new Document("firm_name" , regular));
                        condition.add(new Document("firm_addr" , regular));
                        condition.add(new Document("firm_type" , regular));
                        condition.add(new Document("firm_dscrpt" , regular));
                        condition.add(new Document("firm_intro" , regular));
                        condition.add(new Document("firm_major" , regular));
                        cursor = collection.find(new Document("$or",condition)).limit(15).iterator();
                    }else if(searchType.equals("1")){//按地区搜寻
                        Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
                        cursor = collection.find(new Document("firm_addr",regular)).limit(15).iterator();
                    }else if(searchType.equals("2")){
                        cursor = collection.find(new Document("_id",keyWord)).limit(15).iterator();
                    } else{
                        cursor = collection.find().limit(10).iterator();
                    }

                    while (cursor.hasNext()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.putAll(cursor.next());
                        result.add(map);
                    }
                }break;
                case "counseling":{
                    MongoDBUtil mongoDb = new MongoDBUtil("wxby");
                    MongoCollection<Document> collection = mongoDb.getCollection("legal_counseling");
                    MongoCursor<Document> cursor;
                    if(searchType.equals("0")){//关键字搜寻
                        List<Document> condition = new ArrayList<>();
                        //设置正则表达
                        Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
                        condition.add(new Document("content" , regular));
                        cursor = collection.find(new Document("$or",condition)).limit(15).iterator();
                    }else{
                        cursor = collection.find().limit(15).iterator();
                    }

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