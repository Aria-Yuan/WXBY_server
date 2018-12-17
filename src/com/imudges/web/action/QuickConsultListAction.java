package com.imudges.web.action;

import java.util.List;
import java.util.Map;

public class QuickConsultListAction extends SearchBaseAction  {

    private String keyword;

    private String type;

    private List<Map<String, Object>> result;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    @Override
    public String execute() throws Exception {
        result = getQuickConsultList(keyword,type);
        return SUCCESS;
    }
}
