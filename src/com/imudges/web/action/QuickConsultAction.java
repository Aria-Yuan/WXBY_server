package com.imudges.web.action;

import java.util.Map;

public class QuickConsultAction extends BaseConsultActivity {

    private String id;

    private String author_id;

    private String author_name;

    private String content;

    private Map<String,Object> result;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public String execute() throws Exception{

        result = quickConsultAction(id, author_id, author_name, content);
        return SUCCESS;

    }

}
