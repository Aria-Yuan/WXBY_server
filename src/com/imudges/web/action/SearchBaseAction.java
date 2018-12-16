package com.imudges.web.action;

import com.circle.web.database.database.MongoDBUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.opensymphony.xwork2.ActionSupport;
import jdk.nashorn.internal.scripts.JO;
import net.sf.json.JSONObject;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;


import javax.print.Doc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Projections.fields;

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
    protected List<Map<String, Object>> getJudgementResult(String keyWord, String searchType) throws UnsupportedEncodingException {
//            keyWord = new String(keyWord.getBytes("ISO-8859-1"),"UTF-8");
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        System.out.println(keyWord);
        MongoCollection<Document> collection = mongoDb.getCollection("judgement");
        MongoCursor<Document> cursor;
        if(searchType.equals("0") && !keyWord.equals("")){//关键字搜寻
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

            //期间
            try{
                String start = con_json.getString("start");
                System.out.println(start);
                SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
                Date start_date = sdf.parse(start);
                condition.add(new Document("date", new BasicDBObject("$gte", start_date)));

            }catch(Exception e){
                System.out.println("我错了");
            }
            try{
                String end = con_json.getString("end");
                System.out.println(end);
                SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
                Date end_date = sdf.parse(end);
                condition.add(new Document("date", BasicDBObjectBuilder.start("$gte", sdf.parse(con_json.getString("start"))).add("$lte", end_date).get()));

            }catch(Exception e){

            }

            //judge
            try{
                String judge = con_json.getString("judge");
                String regex = "";

                for (String a : judge.split("\\s")){
                    regex += a + "\\s*";
                }
                Pattern p = Pattern.compile("法\\s*官\\s*" + regex, Pattern.MULTILINE);
                condition.add(new Document("j_content", p));

            }catch(Exception e){

            }

            try{
                cursor = collection.find(new Document("$and",condition))
                        .projection(new Document("j_id", 1)
                                .append("j_reason", 1)
                                .append("_id", 1)
                                .append("j_content", 1))
                        .sort(new Document("j_rank."+con_json.getString("keyword"), -1).append("view_count",-1))
                        .limit(15).iterator();
            }catch(Exception e){
                cursor = collection.find(new Document("$and",condition))
                        .projection(new Document("j_id", 1)
                                .append("j_reason", 1)
                                .append("_id", 1)
                                .append("j_content", 1))
                        .sort(new Document("view_count",-1))
                        .limit(15).iterator();
            }

        }else if(searchType == "1"){//PK搜寻
            Document old = collection.find(new Document("_id",new ObjectId(keyWord))).first();
            collection.updateOne(old,new Document("$set", new Document("view_count",old.getInteger("view_copunt")+1)));
            cursor = collection.find(new Document("_id",new ObjectId(keyWord)))
                    .projection(new Document("j_rank", 0)).limit(1).iterator();
        }
        else{
            cursor = collection.find().projection(new Document("j_rank", 0)).limit(15).iterator();
        }
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
        mongoDb.close();
        return result;
    }

    protected List<Map<String, Object>> getLawyerResult(String keyWord, String searchType){
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("lawyer");
        MongoCursor<Document> cursor;
        if(searchType.equals("0") && !keyWord.equals("")){//关键字搜寻
            List<Document> condition = new ArrayList<>();
            //设置正则表达
            Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
            condition.add(new Document("name" , regular));
            condition.add(new Document("major" , regular));
            cursor = collection.find(new Document("$or",condition))
                    .projection(new Document("_id",1)
                            .append("name",1)
                            .append("job",1)
                            .append("company",1)
                            .append("major",1)
                            .append("price",1)
                            .append("comment",1)).sort(new Document("comment",-1)).limit(15).iterator();
        }else if(searchType.equals("1")) {//PK搜寻
            cursor = collection.find(new Document("_id",new ObjectId(keyWord))).limit(1).iterator();
        }else if(searchType.equals("2")) {//PK搜寻（律师详情）
            cursor = collection.find(new Document("_id",new ObjectId(keyWord))).limit(1).iterator();
            Document lawyer = cursor.next();
            List<ObjectId> counseling_list = (List<ObjectId>)lawyer.get("counseling_list");
            MongoCollection<Document> counselingCollection = mongoDb.getCollection("legal_counseling");
            List<String> counseling_lst = new ArrayList<>();
            for(int i = 0 ; i < counseling_list.size() && i < 3; i++){
                Document counseling = counselingCollection.find(new Document("_id", counseling_list.get(i)))
                        .projection(new Document("_id",1)
                                .append("create_time",1)
                                .append("view_count",1)
                                .append("content",new Document("$slice",1))).iterator().next();
                System.out.println(counseling.toJson());
                counseling.put("_id", counseling.getObjectId("_id").toString());
                SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
                counseling.put("create_time", sdf.format(counseling.getDate("create_time")));
                counseling_lst.add(counseling.toJson());
            }
            lawyer.put("counseling_list", counseling_lst);
            lawyer.put("_id", lawyer.getObjectId("_id").toString());
            Map<String, Object> map = new HashMap<String, Object>();
            map.putAll(lawyer);
            result.add(map);
            return result;
        }
        else{
            cursor = collection.find()
                    .projection(new Document("_id",1)
                            .append("name",1)
                            .append("job",1)
                            .append("company",1)
                            .append("major",1)
                            .append("price",1)
                            .append("comment",1)).sort(new Document("comment",-1)).limit(15).iterator();
        }
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
        mongoDb.close();
        return result;
    }

    protected List<Map<String, Object>> getLawResult(String keyWord, String searchType){
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("law");
        MongoCursor<Document> cursor;
        if(searchType.equals("0") && !keyWord.equals("")) {//关键字搜寻
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
            try{
                Pattern regularand = Pattern.compile("(?i)" + con_json.getString("and") + ".*$", Pattern.MULTILINE);
                and.add(new Document(item,regularkey));
                and.add(new Document(item,regularand));
                andOr.add(new Document("$and",and));
            }catch (Exception e){
                andOr.add(new Document(item,regularkey));
            }

            List<Document> or = new ArrayList<>();
            try{
                Pattern regularor = Pattern.compile("(?i)" + con_json.getString("or") + ".*$", Pattern.MULTILINE);
                or.add(new Document(item,regularkey));
                or.add(new Document(item,regularor));
                andOr.add(new Document("$or",or));
            }catch (Exception e){

            }

            List<Document> not = new ArrayList<>();
            not.add(new Document("$or",andOr));
            try{
                Pattern regularnot = Pattern.compile("(?i)" + con_json.getString("not") + ".*$", Pattern.MULTILINE);
                not.add(new Document(item,new Document("$ne",regularnot)));
                condition.append("$and",not);
            }catch (Exception e){
                condition.append("$or",andOr);
            }

            //有效状态
            try{
                int state = con_json.getInt("state");
                if(state == 0){
                    condition.append("abandon","Not abandon yet");
                }else if(state == 1){
                    condition.append("abandon",new Document("$ne","Not abandon yet"));
                }
            }catch (Exception e){

            }

            //期间
            try{
                String start = con_json.getString("start");
                System.out.println(start);
                SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
                Date start_date = sdf.parse(start);
                condition.append("sdate", new BasicDBObject("$gte", start_date));

            }catch(Exception e){
                System.out.println("我错了");
            }
            try{
                String end = con_json.getString("end");
                System.out.println(end);
                SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
                Date end_date = sdf.parse(end);
                condition.append("sdate", BasicDBObjectBuilder.start("$gte", sdf.parse(con_json.getString("start"))).add("$lte", end_date).get());

            }catch(Exception e){

            }
            System.out.println(condition);
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
        mongoDb.close();
        return result;
    }

    protected List<Map<String, Object>> getCounselingResult(String keyWord, String searchType){
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        System.out.println(keyWord);
        MongoCollection<Document> collection = mongoDb.getCollection("legal_counseling");
        MongoCollection<Document> collection_l = mongoDb.getCollection("lawyer");
        MongoCollection<Document> collection_q = mongoDb.getCollection("register");
        MongoCursor<Document> cursor;
        if(searchType.equals("0") && !keyWord.equals("")){//关键字搜寻
            List<Document> condition = new ArrayList<>();
            //设置正则表达
            Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
            condition.add(new Document("content.question" , regular));
            cursor = collection.find(new Document("$or",condition))
                    .projection(new Document("_id", 1)
                    .append("lawyer",1)
                    .append("create_time",1)
                    .append("view_count", 1)
                    .append("state",1)
                    .append("content", new Document("$slice",1)))
                    .sort(new Document("view_count",-1)).limit(15).iterator();
        }else if(searchType.charAt(0) == '0'){//推荐列表
            if(searchType.charAt(1) == '0'){
                cursor = collection.find()
                        .projection(new Document("_id", 1)
                                .append("lawyer",1)
                                .append("create_time",1)
                                .append("view_count", 1)
                                .append("state",1)
                                .append("content", new Document("$slice",1))).limit(15).iterator();
//                        .sort(new Document("view_count",-1))
            }else if(searchType.charAt(1) == '1'){
                cursor = collection.find()
                        .projection(new Document("_id", 1)
                                .append("lawyer",1)
                                .append("create_time",1)
                                .append("view_count", 1)
                                .append("state",1)
                                .append("content", new Document("$slice",1)))
                        .sort(new Document("_id",-1)).limit(15).iterator();
            }else{
                cursor = collection.find()
                        .projection(new Document("_id", 1)
                                .append("lawyer",1)
                                .append("create_time",1)
                                .append("view_count", 1)
                                .append("state",1)
                                .append("content", new Document("$slice",1)))
                        .sort(new Document("view_count",-1).append("_id",-1)).limit(15).iterator();
            }
        }else if(searchType.equals("1")){//新增
            Document counseling = Document.parse(keyWord);
            counseling.append("questioner",new ObjectId(counseling.getString("questioner")));
            counseling.append("lawyer",new ObjectId(counseling.getString("lawyer")));
            //生成編號
            Random ran = new Random(System.currentTimeMillis());
            String text = ran.nextLong() + "";
            counseling.append("id", text);

            System.out.println(counseling);
            collection.insertOne(counseling);
            cursor = collection.find(new Document("id", text)).limit(1).iterator();

            //更新律师的counselingList
            Document lawyer = collection_l.find(new Document("_id", counseling.getObjectId("lawyer"))).limit(1).iterator().next();
            List<ObjectId> counselinglst = (List<ObjectId>)lawyer.get("counseling_list");
            counselinglst.add(collection.find(new Document("id", text)).limit(1).iterator().next().getObjectId("_id"));
            lawyer.put("counseling_list", counselinglst);
            collection_l.updateOne(new Document("_id", counseling.getObjectId("lawyer")), new Document("$set", lawyer));

        }else if(searchType.equals("2")){//取得某用户的所有提问
            cursor = collection.find(new Document("questioner", new ObjectId(keyWord)))
                    .projection(new Document("questioner",0))
                    .sort(new Document("state",1).append("time",-1)).limit(15).iterator();
        }else if(searchType.equals("3")){//取得某律师的所有回答
            cursor = collection.find(new Document("lawyer", new ObjectId(keyWord)))
                    .projection(new Document("_id", 1)
                    .append("lawyer",1)
                    .append("create_time",1)
                    .append("view_count", 1)
                    .append("state",1)
                    .append("content", new Document("$slice",1)))
                    .sort(new Document("state",-1).append("view_count",-1)).limit(15).iterator();
        }else if(searchType.equals("5")){//取得某律师的所有回答(律师视角)
            MongoCursor<Document> lawyerCursor1 = collection_l.find(new Document("reg_id",new ObjectId(keyWord))).iterator();
            cursor = collection.find(new Document("lawyer", lawyerCursor1.next().getObjectId("_id")))
                    .projection(new Document("_id", 1)
                            .append("lawyer",1)
                            .append("create_time",1)
                            .append("view_count", 1)
                            .append("state",1)
                            .append("content", new Document("$slice",1)))
                    .sort(new Document("state",1).append("time",-1)).limit(15).iterator();
        }else if(searchType.equals("4")){//以pk搜寻
//            Document old = collection.find(new Document("_id",new ObjectId(keyWord))).limit(1).iterator().next();
//            collection.updateOne(old,new Document("$set",new Document("view_count",old.getInteger("view_count")+1)));
            cursor = collection.find(new Document("_id", new ObjectId(keyWord)))
                    .projection(new Document("questioner",0)).limit(1).iterator();
        }else{
            cursor = collection.find().projection(new Document("questioner",0)).limit(15).iterator();
        }
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            MongoCursor<Document> lawyerCursor = collection_l.find(new Document("_id",a.getObjectId("lawyer")))
                    .projection(new Document("counseling_list",0)
                            .append("reg_id",0)).iterator();
            Document lawyer = lawyerCursor.next();
            lawyer.put("_id",lawyer.getObjectId("_id").toString());
            a.put("lawyer", lawyer);
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
        mongoDb.close();
        return result;
    }

    protected List<Map<String, Object>> getFirmResult(String keyWord, String searchType){
        List<Map<String, Object>> result = new ArrayList<>();
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
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
        mongoDb.close();
        return result;
    }

    protected List<Map<String, Object>> getUserCaseConsultResult(String id){
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("case_consult");

        MongoCursor<Document> cursor = collection.find(new Document("owner", new ObjectId(id)))
                .projection(new Document("id", 1)
                .append("content", 1)
                .append("state", 1)
                .append("create_time", 1)).iterator();

        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
        mongoDb.close();
        return result;
    }

    protected Map<String, Object> getCaseConsultResult(String id){
//        String id = "201811011156180970557865";
        System.out.println(id);
        Map<String, Object> result = new HashMap<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("case_consult");
        MongoCollection<Document> jCollection = mongoDb.getCollection("judgement");
        MongoCollection<Document> lCollection = mongoDb.getCollection("law");

        MongoCursor<Document> a = collection.find(new Document("id", id)).iterator();
        Document res = a.next();

//        try {
//            String text = "1";

            if(res.getInteger("state") == 0){
                Document newOne = new Document("$set", new Document("state", 1));
                collection.updateOne(res, newOne);

                Thread t = new Thread(){
                    public void run(){
                        try{
                            String[] args1 = new String[] { "python ", "D:\\prediction.py", id};
                            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件
                            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                            String line = null;
                            while ((line = in.readLine()) != null) {
                                System.out.println(line);
                            }
                            in.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }

//            proc.waitFor();
//            List<String> neighborlst = (ArrayList<String>)res.get("neighborlst");
//            List<ObjectId> neighborlstCopy = new ArrayList<>();
//
//            MongoCollection<Document> collectionJ = mongoDb.getCollection("judgement");
//            for (int i = 0 ; i < neighborlst.size(); i++) {
//                String j_id = neighborlst.get(i).split("_")[1].split(".")[0];
//                System.out.println(j_id);
//                Pattern regularkey = Pattern.compile("(?i)" + j_id + ".*$", Pattern.MULTILINE);
//                ObjectId j = collectionJ.find(new Document("j_id",regularkey)).iterator().next().getObjectId("_id");
//                neighborlstCopy.add(j);
//            }
//            Document update_one = new Document("$set", new Document("neighborlst", neighborlstCopy));

            MongoCursor<Document> b = collection.find(new Document("id", id)).iterator();
            res = b.next();

            result.put("state", res.getInteger("state"));

            if(res.getInteger("state") == 2){
                ArrayList<Document> similars = new ArrayList<>();
                for (ObjectId ids: res.get("neighborlst", new ArrayList<ObjectId>())){
                    MongoCursor<Document> jcursor = jCollection.find(new Document("_id", ids)).iterator();
                    //            System.out.println(jcursor.next());
                    //            similars.add(jcursor.next());
                    Document temp = jcursor.next();
                    Document tempData = new Document();
                    tempData.put("j_id", temp.get("j_id"));
                    tempData.put("j_date", temp.get("j_date"));
                    tempData.put("j_reason", temp.get("j_reason"));
                    tempData.put("j_content", temp.get("j_content"));
                    tempData.put("j_laws", temp.get("j_laws"));
                    tempData.put("_id", temp.get("_id").toString());
                    similars.add(tempData);
                }
                result.put("similar", similars);

                //处理法条
                List<Document> lawLst = new ArrayList<>();
                List<String> lawIdLst = new ArrayList<>();
                for(Document judgement : similars){
                    String s = judgement.getString("j_laws");
                    System.out.println(s);
                    while(s.contains("。")){
                        Pattern p = Pattern.compile("條.*?。", Pattern.DOTALL);
                        Matcher matcher = p.matcher(s);

                        s = matcher.replaceAll("條");
                    }

                    Pattern p = Pattern.compile("第[^0-9-]條", Pattern.DOTALL);
                    Matcher matcher = p.matcher(s);
                    s = matcher.replaceAll("");

                    System.out.print(s);

                    String[] aaa = s.trim().split("條\n");
                    Document condition = new Document();
                    for(int i= 0; i < aaa.length; i++){
                        if(aaa[i].contains("\n")){
                            System.out.print(aaa[i]);
                            condition.clear();
                            condition.append("name",aaa[i].split("\n")[0]);
                        }
                        p = Pattern.compile("[^0-9-]", Pattern.DOTALL);
                        matcher = p.matcher(aaa[i]);
                        p = Pattern.compile("第[\\s*]" + matcher.replaceAll("") + "\\s*條$", Pattern.MULTILINE);
                        condition.append("article", p);

                        MongoCursor<Document> cursorl = lCollection.find(condition).iterator();
                        while (cursorl.hasNext()){
                            Document temp = cursorl.next();
                            if(!lawIdLst.contains(temp.getObjectId("_id").toString())){
                                lawIdLst.add(temp.getObjectId("_id").toString());
                                temp.append("_id",temp.getObjectId("_id").toString());
                                lawLst.add(temp);
                            }
                        }

                    }
                }
                result.put("refer", lawLst);

//                System.out.println(res.get("result").toString() + "**********************************");
                result.put("result", res.get("result"));
                result.put("content", res.getString("content"));
            }
//            result.put("neighbor", res.get("neighborlst"));
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        System.out.println(id);
//        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
//        MongoCollection<Document> collection = mongoDb.getCollection("case_consult");
//        MongoCollection<Document> jCollection = mongoDb.getCollection("judgement");
//        MongoCollection<Document> lCollection = mongoDb.getCollection("law");
//        MongoCursor<Document> cursor = collection.find(new Document("case_id", id)).iterator();
//        Document data = cursor.next();
//        if (data.getInteger("state") == 1){
//            System.out.println(data.get("case_id"));
//            result.put("case_id", id);
//            result.put("content", data.get("content"));
//            result.put("result", data.get("result"));
//

//            //        System.out.println();
//            //        System.out.println();
//            //        System.out.println("similar: " + similars);
//            //        System.out.println();
//            //        System.out.println();
//            //        System.out.println();
//
//            ArrayList<Document> refers = new ArrayList<>();
//            for (ObjectId ids: data.get("refer", new ArrayList<ObjectId>())){
//                MongoCursor<Document> lcursor = lCollection.find(new Document("_id", ids)).iterator();
//                Document temp = lcursor.next();
//                Document tempData = new Document();
//                tempData.put("_id", temp.get("_id").toString());
//                tempData.put("name", temp.get("name").toString());
//                tempData.put("start", temp.get("start").toString());
//                tempData.put("abandon", temp.get("abandon").toString());
//                if (!temp.get("abandon").toString().equals("Not abandon yet")){
//                    tempData.put("end", temp.get("end").toString());
//                }
//                tempData.put("article", temp.get("article").toString());
//                tempData.put("content", temp.get("content").toString());
//                refers.add(tempData);
//            }
//            result.put("refer", refers);
//            //        System.out.println();
//            //        System.out.println();
//            //        System.out.println();
//            System.out.println(refers);
//            //        System.out.println();
//            //        System.out.println();
//            //        System.out.println();
//
//            result.put("state", data.get("state"));
//
//            //        mongoDb.close();
//
//            //        System.out.println(result);
//        }else{
//            result.put("state", data.get("state"));
//            System.out.println(data.get("state"));
//        }


        mongoDb.close();
        return result;
    }

    protected Map<String, Object> getJudgementConsult(String id){

        Map<String, Object> result = new HashMap<>();

        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("judgement");

        Document old = collection.find(new Document("id",id)).first();
        collection.updateOne(old,new Document("$set",new Document("view_count",old.getInteger("view_count")+1)));
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

    protected List<Map<String, Object>> getNewsResult(String keyWord, String searchType){
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("hotnews");
        MongoCursor<Document> cursor;
        if(searchType.equals("0") && !keyWord.isEmpty()){//关键字搜寻
            List<Document> condition = new ArrayList<>();
            //设置正则表达
            Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
            condition.add(new Document("title" , regular));
            condition.add(new Document("article" , regular));
            cursor = collection.find(new Document("$or",condition))
                    .sort(new Document("_id",-1)).limit(15).iterator();
//        }else if(searchType.equals("1")){//按日期搜寻
//            Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
//            cursor = collection.find(new Document("firm_addr",regular)).limit(15).iterator();
        }else if(searchType.equals("1")){//pk搜寻
            cursor = collection.find(new Document("_id",new ObjectId(keyWord)))
                    .sort(new Document("_id",-1)).limit(1).iterator();
        }
        else{
            cursor = collection.find()
                    .sort(new Document("_id",-1)).limit(10).iterator();
        }
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
        mongoDb.close();
        return result;
    }

    protected List<Map<String, Object>> getCommentsResult(String keyWord, String searchType){
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("comment");
        MongoCursor<Document> cursor;
        if(searchType.equals("0") && !keyWord.isEmpty()){//关键字搜寻
            List<Document> condition = new ArrayList<>();
            //设置正则表达
            Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
            condition.add(new Document("title" , regular));
            condition.add(new Document("article" , regular));
            cursor = collection.find(new Document("$or",condition))
                    .sort(new Document("_id",-1)).limit(15).iterator();
//        }else if(searchType.equals("1")){//按日期搜寻
//            Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
//            cursor = collection.find(new Document("firm_addr",regular)).limit(15).iterator();
        }else if(searchType.equals("1")){//pk搜寻
            cursor = collection.find(new Document("_id",new ObjectId(keyWord)))
                    .sort(new Document("_id",-1)).limit(1).iterator();
        }
        else{
            cursor = collection.find()
                    .sort(new Document("_id",-1)).limit(10).iterator();
        }
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
        mongoDb.close();
        return result;
    }

    protected List<Map<String, Object>> getNewsKeyword(String keyWord, String searchType){
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("hotnews");
        MongoCursor<Document> cursor;
        if(searchType.equals("0")){//关键字搜寻
            List<Document> condition = new ArrayList<>();
            //设置正则表达
            Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
            condition.add(new Document("title" , regular));
            condition.add(new Document("article" , regular));
            cursor = collection.find(new Document("$or",condition)).limit(15).iterator();
        } else{
            cursor = collection.find().limit(10).iterator();
        }
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
        mongoDb.close();
        return result;
    }

    protected List<Map<String, Object>> getCommentKeyword(String keyWord,String searchType){
        List<Map<String, Object>> result = new ArrayList<>();
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("comment");
        MongoCursor<Document> cursor;
        if(searchType.equals("0")){//关键字搜寻
            List<Document> condition = new ArrayList<>();
            //设置正则表达
            Pattern regular = Pattern.compile("(?i)" + keyWord + ".*$", Pattern.MULTILINE);
            condition.add(new Document("title" , regular));
            condition.add(new Document("article" , regular));
            cursor = collection.find(new Document("$or",condition)).limit(15).iterator();
        } else{
            cursor = collection.find().limit(10).iterator();
        }
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Document a = cursor.next();
            a.put("_id", a.getObjectId("_id").toString());
            map.putAll(a);
            result.add(map);
        }
        cursor.close();
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

    protected Map<String, Object> getCaseResult(String id, String content, String owner){
        Map<String, Object> result = new HashMap<>();

        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("case_consult");

        String test = "事實及理由上訴人經合法通知,未於言詞辯論期日到場,核無民事訴訟法第386條所列各款情形,爰依被上訴人聲請,由其一造辯論而為判決二、被上訴人起訴主張:上訴人於民國101年11月1日,向被上訴人承租新北市○區∞o路00o0號3樓房屋(下稱系爭房屋),租期1年即自101年11月1日起至102年10月31日止,租金每月新台幣(下同)1萬2,000元,於每月1日前給付。嗣租期屆滿,上訴人拒絕遷讓返還系爭房屋,並累計積欠租金3萬6,θθ0元未付,迭經被上訴人催討,上訴人均置之不理。又本件租期既已屆滿,上訴人即屬無權占有系爭房屋,另應自102年11月1日起至返還房屋之日止,按月賠償被上訴人相當於未收租金額計算之損害金。爰依租賃及不當得利之法律關係,求為判決∶上訴人應將系爭房屋全部遷讓返還被上訴人,並應給付被上訴人3萬6,000元,暨應自102年11月1日起至返還房屋之日止,按月給付被上訴人1萬2,000元(原審判決上訴人敗訴,並依職權為准、免假執行宣告,上訴人不服提起上訴)。並答辯聲明:上訴駁回。三、上訴人則以:被上訴人未給與上訴人搬遷費用,上訴人無法遷讓返還系爭房屋等語,資為抗辯。並上訴聲明:(一)原判決廢棄;(二)被上訴人在第一審之訴駁回。四、被上訴人主張上訴人於101年11月1日,向被上訴人承租系爭房屋,租期1年至102年10月31日止,租金每月1萬2,000元,嗣租期屆滿,上訴人拒絕遷讓返還系爭房屋,並累計積欠租金3萬6,000元未付,且無權占有系爭房屋,致被上訴人受有每月相當租金額之不當得利損害金,被上訴人自得依租賃及不當得利之法律關係,請求上訴人將系爭房屋全部遷讓返還被上訴人,並給付租金3萬6,000元,暨自102年11月1日起至返還房屋之日止,按月給付不當得利損害金1萬2,000元等情,有租賃契約書在卷可稽(原審司板簡調字卷第6、7頁)且為上訴人所不爭執,自堪信為真實,並屬於法有據。上訴人雖抗辯被上訴人未給與上訴人搬遷費用,上訴人無法遷讓返還系爭房屋云云,惟所辯尚屬如何遷讓房屋之執行問題,自不能據以對抗被上訴人。五、從而,被上訴人依租賃及不當得利之法律關係,求為判決上訴人應將系爭房屋全部遷讓返還被上訴人,並應給付被上訴人3萬6,000元,暨應自102年11月1日起至返還房屋之日止按月給付被上訴人1萬2,000元,自屬應予准許。原審為上訴人敗訴之判決,核無違誤,上訴論旨指摘原判決不當,求予廢棄改判,為無理由六、兩造其餘之攻擊或防禦方法及未經援用之證據,經本院斟酌後,認為均不足以影響本判決之結果,自無逐一詳予論駁之必要,併此敘明。七、據上論結,本件上訴為無理由,依民事訴訟法第436條之1第3項、第463條、第385條第1項前段、第449條第1項、第78條判決如主文";
        Document wawawa = new Document();
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        wawawa.append("content", test);
        wawawa.append("id", id);
        wawawa.append("owner", new ObjectId(owner));
        wawawa.append("state", 0);
        wawawa.append("create_time",sdf.format(new Date()));
        collection.insertOne(wawawa);
        result.put("type", 1);

        mongoDb.close();
        return result;
    }

    protected Map<String, Object> getNews(){
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("hotnews");
        MongoCursor<Document> cursor = collection.find().limit(15).iterator();
        Map<String,Object>result = new HashMap<>();
        List<Map<String, Object>> hots = new ArrayList<>();
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.putAll(cursor.next());
//            map.put("_id", new ObjectId(map.get("_id").toString()).toString());
            hots.add(map);
        }
        result.put("hotNews", hots);

//        collection = mongoDb.getCollection("comment");
//        cursor = collection.find().limit(1).iterator();
//        List<Map<String, Object>> commnet = new ArrayList<>();
//        while (cursor.hasNext()) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.putAll(cursor.next());
////            map.put("_id", new ObjectId(map.get("_id").toString()).toString());
//            commnet.add(map);
//        }
//        result.put("comment", commnet);

        cursor.close();
        mongoDb.close();
//        List<Map<String,Object>> a = new ArrayList<>();
//        result.put("lalala", a);
        return result;
    }

    protected Map<String, Object> getComment(){
        MongoDBUtil mongoDb = new MongoDBUtil("wxby");
        MongoCollection<Document> collection = mongoDb.getCollection("comment");
        MongoCursor<Document> cursor = collection.find().limit(15).iterator();
        Map<String,Object>result = new HashMap<>();
        List<Map<String, Object>> hots = new ArrayList<>();
//        while (cursor.hasNext()) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.putAll(cursor.next());
////            map.put("_id", new ObjectId(map.get("_id").toString()).toString());
//            hots.add(map);
//        }
//        result.put("hotNews", hots);

//        collection = mongoDb.getCollection("comment");
//        cursor = collection.find().limit(1).iterator();
        List<Map<String, Object>> commnet = new ArrayList<>();
        while (cursor.hasNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.putAll(cursor.next());
//            map.put("_id", new ObjectId(map.get("_id").toString()).toString());
            commnet.add(map);
        }
        result.put("comment", commnet);

        cursor.close();
        mongoDb.close();
//        List<Map<String,Object>> a = new ArrayList<>();
//        result.put("lalala", a);
        return result;
    }

}