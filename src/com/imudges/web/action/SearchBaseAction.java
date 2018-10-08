package com.imudges.web.action;

import com.circle.web.database.database.MongoDBUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.opensymphony.xwork2.ActionSupport;
import jdk.nashorn.internal.scripts.JO;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;


import java.text.SimpleDateFormat;
import java.util.*;
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
            MongoDBUtil mongoDb = new MongoDBUtil("wxby");
            switch (type){
                case "firm":{
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
                    cursor.close();
                }break;
                case "counseling":{
                    MongoCollection<Document> collection = mongoDb.getCollection("legal_counseling");
                    MongoCollection<Document> collection_l = mongoDb.getCollection("lawyer");
                    MongoCollection<Document> collection_q = mongoDb.getCollection("register");
                    MongoCursor<Document> cursor;
                    if(searchType.equals("0") && !keyWord.isEmpty()){//关键字搜寻
                        List<Document> condition = new ArrayList<>();
                        //设置正则表达
                        Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
                        condition.add(new Document("content" , regular));
                        cursor = collection.find(new Document("$or",condition)).limit(15).iterator();
                    }else if(searchType.equals("1")){//新增
                        Document counseling = Document.parse(keyWord);
                        counseling.append("questioner",new ObjectId(counseling.getString("questioner")));
                        counseling.append("lawyer",new ObjectId(counseling.getString("lawyer")));
                        System.out.println(counseling);
                        collection.insertOne(counseling);
                        cursor = collection.find(counseling).limit(1).iterator();
                    }else if(searchType.equals("2")){
                        cursor = collection.find(new Document("questioner", new ObjectId(keyWord))).limit(15).iterator();
                    }else if(searchType.equals("3")){
                        MongoCursor<Document> lawyerCursor1 = collection_l.find(new Document("reg_id",new ObjectId(keyWord))).iterator();
                        cursor = collection.find(new Document("lawyer", lawyerCursor1.next().getObjectId("_id"))).limit(15).iterator();
                    }
                    else{
                        cursor = collection.find().limit(15).iterator();
                    }
                    while (cursor.hasNext()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Document a = cursor.next();
                        a.put("_id", a.getObjectId("_id").toString());
                        MongoCursor<Document> questionerCursor = collection_q.find(new Document("_id",a.getObjectId("questioner"))).iterator();
                        a.put("questioner", questionerCursor.next().getString("name"));
                        MongoCursor<Document> lawyerCursor = collection_l.find(new Document("_id",a.getObjectId("lawyer"))).iterator();
                        Document lawyer = lawyerCursor.next();
                        lawyer.put("_id",lawyer.getObjectId("_id").toString());
                        lawyer.put("reg_id",lawyer.getObjectId("reg_id").toString());
                        a.put("lawyer", lawyer);
                        map.putAll(a);
                        result.add(map);
                    }
                    cursor.close();
                }break;
                case "law":{
                    MongoCollection<Document> collection = mongoDb.getCollection("law");
                    MongoCursor<Document> cursor;
                    if(searchType.equals("0") && !keyWord.isEmpty()) {//关键字搜寻
                        JSONObject con_json = JSONObject.fromObject(keyWord);
                        Document condition = new Document();
                        //检索项目
                        String item = null;
                        switch(con_json.getInt("item")){
                            case 0: item = "name";break;
                            case 1: item = "content";break;
                        }

                        //关键字部分
                        List<Document> andOr = new ArrayList<>();
                        List<Document> and = new ArrayList<>();
                        Pattern regularkey = Pattern.compile("(?i)" + con_json.getString("keyword") + ".*$", Pattern.MULTILINE);
                        Pattern regularand = Pattern.compile("(?i)" + con_json.getString("and") + ".*$", Pattern.MULTILINE);
                        try{
                            and.add(new Document(item,regularkey));
                            and.add(new Document(item,regularand));
                            andOr.add(new Document("$and",and));
                        }catch (Exception e){
                            andOr.add(new Document(item,regularkey));
                        }

                        List<Document> or = new ArrayList<>();
                        Pattern regularor = Pattern.compile("(?i)" + con_json.getString("or") + ".*$", Pattern.MULTILINE);
                        try{
                            or.add(new Document(item,regularkey));
                            or.add(new Document(item,regularor));
                            andOr.add(new Document("$or",and));
                        }catch (Exception e){
                            andOr.add(new Document(item,regularkey));
                        }

                        List<Document> not = new ArrayList<>();
                        not.add(new Document("$or",andOr));
                        Pattern regularnot = Pattern.compile("(?i)" + con_json.getString("not") + ".*$", Pattern.MULTILINE);
                        try{
                            not.add(new Document("$not",new Document(item,regularnot)));
                            condition.append("$and",not);
                        }catch (Exception e){
                            condition.append("$or",andOr);
                        }

                        //有效状态
                        int state = con_json.getInt("state");
                        if(state == 0){
                            condition.append("abandon","Not abandon yet");
                        }

                        cursor = collection.find(condition).limit(15).iterator();
                    }else{
                        cursor = collection.find().limit(15).iterator();
                    }
                    while (cursor.hasNext()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Document a = cursor.next();
                        a.put("_id", a.getObjectId("_id").toString());
                        map.putAll(a);
                        result.add(map);
                    }
                    cursor.close();
                }break;
                case "lawyer":{
                    MongoCollection<Document> collection = mongoDb.getCollection("lawyer");
                    MongoCursor<Document> cursor;
                    if(searchType.equals("0") && !keyWord.isEmpty()){//关键字搜寻
                        List<Document> condition = new ArrayList<>();
                        //设置正则表达
                        Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
                        condition.add(new Document("name" , regular));
                        condition.add(new Document("major" , regular));
                        cursor = collection.find(new Document("$or",condition)).limit(15).iterator();
                    }else if(searchType.equals("1")) {//PK搜寻
                        cursor = collection.find(new Document("_id",new ObjectId(keyWord))).limit(15).iterator();
                    }
                    else{
                        cursor = collection.find().limit(15).iterator();
                    }
                    while (cursor.hasNext()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Document a = cursor.next();
                        a.put("_id", a.getObjectId("_id").toString());
                        a.put("reg_id", a.getObjectId("reg_id").toString());
                        map.putAll(a);
                        result.add(map);
                    }
                    cursor.close();
                }
                case "judgement":{
                    MongoCollection<Document> collection = mongoDb.getCollection("judgement");
                    MongoCursor<Document> cursor;
                    if(searchType.equals("0") && !keyWord.isEmpty()){//关键字搜寻
                        List<Document> condition = new ArrayList<>();
                        JSONObject con_json = JSONObject.fromObject(keyWord);

                        try{
                            //设置正则表达
                            //关键字
                            List<Document> keywords = new ArrayList<>();
                            Pattern regular = Pattern.compile("(?i)" + con_json.getString("keyword") + ".*$", Pattern.MULTILINE);
                            keywords.add(new Document("j_content", regular));
                            keywords.add(new Document("j_laws", regular));
                            condition.add(new Document("$or" , keywords));
                        }catch (Exception e){
                            System.out.println("is null object");
                        }

                        try{
                            //XX年XX字XX号
                            Pattern regularName = Pattern.compile("(?i)" + con_json.getString("year") + ".*年度.*" + con_json.getString("zihao") + ".*字第.*" + con_json.getString("num") + ".*號", Pattern.MULTILINE);
                            condition.add(new Document("j_id", regularName));
                        }catch (Exception e){
                            System.out.println("is null object");
                        }

                        try{
                            //裁判案由
                            Pattern regularR = Pattern.compile("(?i)" + con_json.getString("reason") + ".*$", Pattern.MULTILINE);
                            condition.add(new Document("j_reason", regularR));
                        }catch (Exception e){
                            System.out.println("is null object");
                        }

                        try{
                            //裁判主文
                            Pattern regularC = Pattern.compile("(?i)主  文.*" + con_json.getString("content") + ".*理  由.*$", Pattern.MULTILINE);
                            condition.add(new Document("j_reason", regularC));
                        }catch (Exception e){
                            System.out.println("is null object");
                        }


                        try{
                            //裁判类别
                            try{
                                switch (con_json.getString("type")){
                                    case "0": {
                                        System.out.println("請問你是什麼情況");
                                        Pattern regularN = Pattern.compile("(?i).*" + "裁定.*$", Pattern.MULTILINE);
                                        condition.add(new Document("j_id", regularN));
                                    }break;
                                    case "1": {
                                        Pattern regularN = Pattern.compile("(?i).*" + "判決.*$", Pattern.MULTILINE);
                                        condition.add(new Document("j_id", regularN));
                                    }break;
                                }
                            }catch (Exception e){

                            }
                        }catch (Exception e){
                            System.out.println("is null object");
                        }

                        cursor = collection.find(new Document("$and",condition)).limit(15).iterator();
                    }
                    else{
                        cursor = collection.find().limit(15).iterator();
                    }
                    while (cursor.hasNext()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Document a = cursor.next();
                        a.put("_id", a.getObjectId("_id").toString());
                        map.putAll(a);
                        result.add(map);
                    }
                    cursor.close();
                }
            }
            mongoDb.close();
            return result;
    }

    protected Map<String, Object> getCaseConsultResult(String id){
        Map<String, Object> result = new HashMap<>();
//        System.out.println(id);
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("case_consult");
        MongoCollection<Document> jCollection = mongoDb.getCollection("judgement");
        MongoCollection<Document> lCollection = mongoDb.getCollection("law");
        MongoCursor<Document> cursor = collection.find(new Document("case_id", id)).iterator();
        Document data = cursor.next();
        if (data.getInteger("state") == 1){
            System.out.println(data.get("case_id"));
            result.put("case_id", id);
            result.put("content", data.get("content"));
            result.put("result", data.get("result"));

            ArrayList<Document> similars = new ArrayList<>();
            for (ObjectId ids: data.get("similar", new ArrayList<ObjectId>())){
                MongoCursor<Document> jcursor = jCollection.find(new Document("_id", ids)).iterator();
                //            System.out.println(jcursor.next());
                //            similars.add(jcursor.next());
                Document temp = jcursor.next();
                Document tempData = new Document();
                tempData.put("j_id", temp.get("j_id"));
                tempData.put("j_date", temp.get("j_date"));
                tempData.put("j_reason", temp.get("j_reason"));
                tempData.put("_id", temp.get("_id").toString());
                similars.add(tempData);
                System.out.println(tempData);
            }
            result.put("similar", similars);
            //        System.out.println();
            //        System.out.println();
            //        System.out.println("similar: " + similars);
            //        System.out.println();
            //        System.out.println();
            //        System.out.println();

            ArrayList<Document> refers = new ArrayList<>();
            for (ObjectId ids: data.get("refer", new ArrayList<ObjectId>())){
                MongoCursor<Document> lcursor = lCollection.find(new Document("_id", ids)).iterator();
                Document temp = lcursor.next();
                Document tempData = new Document();
                tempData.put("_id", temp.get("_id").toString());
                tempData.put("name", temp.get("name").toString());
                tempData.put("start", temp.get("start").toString());
                tempData.put("abandon", temp.get("abandon").toString());
                if (!temp.get("abandon").toString().equals("Not abandon yet")){
                    tempData.put("end", temp.get("end").toString());
                }
                tempData.put("article", temp.get("article").toString());
                tempData.put("content", temp.get("content").toString());
                refers.add(tempData);
            }
            result.put("refer", refers);
            //        System.out.println();
            //        System.out.println();
            //        System.out.println();
            System.out.println(refers);
            //        System.out.println();
            //        System.out.println();
            //        System.out.println();

            result.put("state", data.get("state"));

            //        mongoDb.close();

            //        System.out.println(result);
        }else{
            result.put("state", data.get("state"));
            System.out.println(data.get("state"));
        }
        mongoDb.close();
        return result;
    }

    protected Map<String, Object> getJudgementConsult(String id){

        Map<String, Object> result = new HashMap<>();

        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("judgement");

        MongoCursor<Document> cursor = collection.find(new Document("id", id)).iterator();

        int state = 0;
        //1 成功 0 失败 -1 没资料
        if (cursor.hasNext()) {
            state = 1;
            result.put("state", state);
            Document data = cursor.next();
            result.put("data", data);
            System.out.println(data);
        }else {
            state = -1;
            result.put("state", state);
        }

        mongoDb.close();

        return result;

    }

    protected Map<String, Object> loginAndRegister(String tp, String username, String password){

        int type = Integer.valueOf(tp);
        Map<String, Object> result = new HashMap<>();
        int code = 0;
        String message = "", dbFind = "", dbInsert = "";
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("register");
        MongoCursor<Document> cursorr;

//        System.out.println(type);

        if (username.equals("")) {

            code = 0;
            message = "賬號不可以為空喔！";

        } else{

            switch (type){
                //1 name登录 2 phone登录 3 id登录 -1 phone注册 -2 id注册
                //return 0 无账号 1 成功 -1 密码错误 -2 账号重复

                case 1:
                    if (type == 1){
                        dbFind = "name";
                    }
                case 2:
                    if (type == 2){
                        dbFind = "phone";
                    }
                case 3:
                    if (type == 3){
                        dbFind = "reg_id";
                    }

                    String rightPassword;

                    cursorr = collection.find(new Document().append(dbFind, username)).iterator();


                    if (cursorr.hasNext()) {
                        Document cursor = cursorr.next();
                        rightPassword = cursor.getString("password");

                        if (password.equals(rightPassword)) {
                            code = 1;
                            message = "登錄成功~";
                            result.put("_id", cursor.getObjectId("_id").toString());
                            result.put("role", cursor.get("role")+"");
                            result.put("name", cursor.getString("name"));
                        }else{
                            code = -1;
                            message = "抱歉，密碼有錯誤喔~";
                        }

                    }else{

                        code = 0;
                        message = "沒有這個賬號喔~";

                    }

                    System.out.println("type: " + type + "   username:" + username + "   password:" + password);

                    break;

                case -1:
                    if (type == -1) {
                        dbInsert = "phone";
                        message = "該手機已經被註冊，請換一個其他的手機號或是點擊忘記密碼試試！";
                    }

                case -2:
                    if (type== -2) {
                        dbInsert = "reg_id";
                        message = "該帳號已經被註冊，請換一個其他的手機號或是點擊忘記密碼試試！";
                    }

                    cursorr = collection.find(new Document().append("phone", username)).iterator();

                    if (cursorr.hasNext()){

                        code = -2;

                    }else{

                        Document docu = new Document();
                        docu.put(dbInsert, username);
                        docu.put("password", password);
                        docu.put("name", "用戶" + (Math.random()*9+1)*100000);
                        collection.insertOne(docu);

                        code = 1;
                        message = "註冊成功！";

                    }

                    break;
            }
        }

        result.put("resultCode", code);
        result.put("resultMessage", message);

        System.out.println("resultCode:" + code + "   resultMessage:" + message + "    id: " + result.get("_id"));
        mongoDb.close();
        return result;

    }

    protected Map<String, Object> getQuickConsultResult(String id){

        System.out.println(id);

        Map<String, Object> result = new HashMap<>();

        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("quick_response");
        MongoCursor<Document> cursor = collection.find(new Document("_id", new ObjectId(id))).iterator();

        if (cursor.hasNext()){
            result.put("state", 1);
            Document data = cursor.next();
            Document trueData = data;
            trueData.put("_id", data.get("_id").toString());
            trueData.put("author", data.get("author").toString());
//            System.out.println(data.get("lawyer_reply"));
            for (Document reply: data.get("lawyer_reply", new ArrayList<Document>())){

                reply.put("author", reply.get("author").toString());
                reply.put("parent", reply.get("parent").toString());
                reply.put("reply_id", reply.get("reply_id").toString());

                int count = 0;
                System.out.println(reply.get("replies"));
                if(!reply.get("replies").equals(new ArrayList<>())) {
                    List<String> tp = new ArrayList<>();
                    for (ObjectId oros : reply.get("replies", new ArrayList<ObjectId>())) {

                        tp.add(oros.toString());
                        count++;

                    }
                    reply.put("replies", tp);
                }

            }
            result.put("data", trueData);
        }else{
            result.put("state", 0);
        }

        System.out.println(result);

        mongoDb.close();

        return result;

    }



}