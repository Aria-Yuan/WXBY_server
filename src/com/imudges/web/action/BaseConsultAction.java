package com.imudges.web.action;

import com.circle.web.database.database.MongoDBUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.opensymphony.xwork2.ActionSupport;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BaseConsultAction extends ActionSupport {

    MongoDBUtil mongoDb = new MongoDBUtil("wxby");

    protected Map<String, Object> getCaseConsultResult(String id){
        Map<String, Object> result = new HashMap<>();
//        System.out.println(id);
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

    protected Map<String, Object> getJudgementConsult(String id) {

        Map<String, Object> result = new HashMap<>();

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
        } else {
            state = -1;
            result.put("state", state);
        }

        mongoDb.close();

        return result;
    }

    protected Map<String, Object> getQuickConsultResult(String id){

        System.out.println(id);

        Map<String, Object> result = new HashMap<>();

        MongoCollection<Document> collection = mongoDb.getCollection("quick_response");
        MongoCursor<Document> cursor = collection.find(new Document("_id", new ObjectId(id))).iterator();


        if (cursor.hasNext()){
            int index = 0;

            result.put("state", 1);
            Document data = cursor.next();
            Document trueData = data;
            trueData.put("_id", data.get("_id").toString());
            trueData.put("author", data.get("author").toString());
//            System.out.println(data.get("lawyer_reply"));
            for (Document reply: data.get("lawyer_reply", new ArrayList<Document>())){

                reply.put("index", index);index++;
                reply.put("author", reply.get("author").toString());
                reply.put("parent", reply.get("parent").toString());
                reply.put("reply_id", reply.get("_id").toString());

                int count = 0;
//                System.out.println(reply.get("replies"));
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

    }//获取quick的结果

    protected Map<String, Object> quickConsultAction(String id, String author_id, String author_name,
                                                     String content){
        Map<String, Object> result = new HashMap<>();
        result.put("state", 0);

        MongoCollection<Document> collection = mongoDb.getCollection("quick_response");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (collection.find(new Document("quick_id", id)).iterator().hasNext()){

            result.put("state", -1);

            return result;
        }
        else {

            Document docu = new Document();
            docu.append("quick_id", id);
            docu.append("author", new ObjectId(author_id));
            docu.append("author_name", author_name);
            docu.append("content", content);
            docu.append("picture", new ArrayList<String>());
            try {
                docu.append("create_time", df.parse(df.format(new Date().getTime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            docu.append("like", 0);
            docu.append("lawyer_reply", new ArrayList<Document>());
            docu.append("view_count", 0);

            collection.insertOne(docu);
            String objectId = collection.find(new Document("quick_id", id)).iterator().next().get("_id").toString();
            System.out.println(objectId);

            result.put("state", 1);
            result.put("id", objectId);

            return result;
        }
    }//quick提问

    protected Map<String, Object> quickConsultReply(String quick_id, String parentId, String authorName, String authorId,
                                                    String content, boolean isLOrA, String parent_index){
        Map<String, Object> result = new HashMap<>();
        MongoCollection<Document> collection = mongoDb.getCollection("quick_response");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        ArrayList<Document> comments = collection.find(new Document("_id", new ObjectId(quick_id))).iterator().next().get("lawyer_reply", new ArrayList<>());
        result.put("state", 0);
//        System.out.println(comments);
        ObjectId reply_id = new ObjectId();

        Document newComment = new Document();
        newComment.append("author", new ObjectId(authorId));
        newComment.append("author_name", authorName);
        newComment.append("create_time", df.format(new Date().getTime()));
        newComment.append("content", content);
        newComment.append("replies", new ArrayList<>());
        newComment.append("parent", new ObjectId(parentId));
        newComment.append("_id", reply_id);
        newComment.append("like", 0);
        newComment.append("parent_index", parent_index);
        newComment.append("is_lawyer_author", isLOrA);
//        System.out.println(newComment);
//            comments.add(newComment);
        System.out.println(collection.updateOne(new Document("_id", new ObjectId(quick_id)),new Document("$addToSet", new Document("lawyer_reply",newComment))));

        if (!parent_index.equals("-1")){

            int index = Integer.valueOf(parent_index);

            Document parentReply = collection.find(new Document("_id", new ObjectId(quick_id))).iterator().next()
                                        .get("lawyer_reply", new ArrayList<Document>()).get(index);
            List replies = parentReply.get("replies", new ArrayList());
            replies.add(reply_id);
            parentReply.put("replies", replies);
//            System.out.println(parentReply);
            String condition = "lawyer_reply." + parent_index;
            System.out.println(collection.updateOne(new Document("_id", new ObjectId(quick_id)),new Document("$set", new Document(condition,parentReply))));
            result.put("state", 1);
//            for (Document comment: comments){
//                if (comment.get("_id").toString().equals(parentId)){
//                    comment.put("")
//                }
//                System.out.println(collection.updateOne(new Document("_id", new ObjectId(quick_id)), new Document("$set", new Document("lawyer_reply.3",new Document("3", newComment)) )));
//            }

        }else{
            result.put("state", 1);
        }
        System.out.println(result);

        return result;

    }//quick回复

    protected Map<String, Object> deleteQuickConsultComment(String quick_id, String reply_index, String reply_id,
                                                            String parent_index, String parent_id){
        Map<String, Object> result = new HashMap<>();
        result.put("state", 0);
        MongoCollection<Document> collection = mongoDb.getCollection("quick_response");

        collection.find(new Document("_id", new ObjectId(quick_id))).iterator();

        if (!parent_index.equals("-1")){

            int index = Integer.valueOf(parent_index);

            Document parentReply = collection.find(new Document("_id", new ObjectId(quick_id))).iterator().next()
                    .get("lawyer_reply", new ArrayList<Document>()).get(index);
            List replies = parentReply.get("replies", new ArrayList());
            replies.remove(new ObjectId(reply_id));
//            System.out.println("                   11   " + reply_id);
//            System.out.println("                   11   " + replies);
            parentReply.put("replies", replies);
//            System.out.println(parentReply);
            String condition = "lawyer_reply." + parent_index;
            System.out.println(collection.updateOne(new Document("_id", new ObjectId(quick_id)),new Document("$set", new Document(condition,parentReply))));
        }

        String condition = "lawyer_reply." + reply_index;

        System.out.println(collection.updateOne(new Document("_id", new ObjectId(quick_id)),new Document("$set", new Document(condition, null))));
        System.out.println(collection.updateOne(new Document("_id", new ObjectId(quick_id)),new Document("$pull", new Document("lawyer_reply", null))));
        result.put("state", 1);
        return result;
    }//quick回复删除

    protected Map<String, Object> getQuickConsultReplyList(String quick_id, String parent_id, String parent_index){
        Map<String, Object> result = new HashMap<>();
        result.put("state", 0);
        MongoCollection<Document> collection = mongoDb.getCollection("quick_response");
        List<Document> total = collection.find(new Document("_id", new ObjectId(quick_id))).iterator().next()
                .get("lawyer_reply", new ArrayList<>());
        List<ObjectId> replies = total.get(Integer.valueOf(parent_index)).get("replies", new ArrayList<>());
        List<Document> lawyer_reply = new ArrayList<>();
        for (ObjectId id : replies) {
            for (int i = 0; i < total.size(); i ++) {
                Document docu = total.get(i);
                if (docu.get("_id").equals(id)) {
                    docu.put("index", i);i++;
                    docu.put("author", docu.get("author").toString());
                    docu.put("parent", docu.get("parent").toString());
                    docu.put("reply_id", docu.get("_id").toString());
                    if(!docu.get("replies").equals(new ArrayList<>())) {
                        List<String> tp = new ArrayList<>();
                        for (ObjectId oros : docu.get("replies", new ArrayList<ObjectId>())) {
                            tp.add(oros.toString());
                        }
                        docu.put("replies", tp);
                    }
                    lawyer_reply.add(docu);
                }
            }
        }

        System.out.println(lawyer_reply);

        result.put("lawyer_reply", lawyer_reply);
        result.put("state", 1);
        return result;
    }

}

