package com.imudges.web.action;

import java.util.Map;

public class PredictAction extends SearchBaseAction  {
    private String id;

    private String content;

    private String owner;

    private String picturelst;

    private Map<String,Object> result;


    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public String execute() throws Exception{

        result = getCaseResult(id, content, owner, picturelst);
        return SUCCESS;

    }

    public String getPicturelst() {
        return picturelst;
    }

    public void setPicturelst(String picturelst) {
        this.picturelst = picturelst;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
