package com.imudges.web.action;

import java.util.List;
import java.util.Map;

public class MyConsultQuickListAction extends SearchBaseAction {

    private String keyword;

    private List<Map<String,Object>> result;

    public String execute() throws Exception{

        result = getQuickConsultList(getKeyword(), "1");
        return SUCCESS;

    }

    public List<Map<String,Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String,Object>> result) {
        this.result = result;
    }


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
