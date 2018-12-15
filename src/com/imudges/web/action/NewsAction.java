package com.imudges.web.action;

import java.util.List;
import java.util.Map;

public class NewsAction extends SearchBaseAction {

    private String condition;
    private String type;

    /**
     * 返回结果
     * */
    private List<Map<String,Object>> result;

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

    public List<Map<String,Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String,Object>> result) {
        this.result = result;
    }

    @Override
    public String execute() throws Exception {
        result = getNewsResult(condition,type);
        return SUCCESS;
    }
}
