package com.imudges.web.action;


import com.mongodb.util.JSON;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public class CounselingUpdateAction extends UpdateBaseAction {

    /**
     * 用户请求参数
     * */
    private String condition;
    private String type;

    /**
     * 返回结果
     * */
    private Map<String, Object> result;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String source_url) {
        this.condition = source_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    @Override
    public String execute() throws Exception {
//        if ("admin".equals(username) && "123".equals(password)){
//            result = getSuccessResult(null);
//        }else {
//            result = getFailResult(-1,"用户名或者密码错误");
//        }
        System.out.println(condition);
        result = getResult("counseling",condition, type);
        return SUCCESS;
    }
}