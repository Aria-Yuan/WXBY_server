package com.imudges.web.action;

import java.util.Map;

public class JudgementConsultAction extends SearchBaseAction  {

    private String id;

    private Map<String,Object> result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public String execute() throws Exception{

        result = getJudgementConsult(id);
        return SUCCESS;

    }

}
