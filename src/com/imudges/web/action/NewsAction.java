package com.imudges.web.action;

import java.util.Map;

public class NewsAction extends SearchBaseAction {

    /**
     * 返回结果
     * */
    private Map<String,Object> result;

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    @Override
    public String execute() throws Exception {
        result = getNews();
        return SUCCESS;
    }
}
