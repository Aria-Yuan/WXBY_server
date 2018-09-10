package com.imudges.web.action;

import java.util.List;
import java.util.Map;

public class CounselingAction extends BaseAction {

    /**
     * 用户请求参数
     * */
    private String condition;

    /**
     * 返回结果
     * */
    private List<Map<String, Object>> result;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String source_url) {
        this.condition = source_url;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    @Override
    public String execute() throws Exception {
//        if ("admin".equals(username) && "123".equals(password)){
//            result = getSuccessResult(null);
//        }else {
//            result = getFailResult(-1,"用户名或者密码错误");
//        }
        result = getResult("counseling",condition);
        return SUCCESS;
    }

}
