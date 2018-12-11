package com.imudges.web.action;

import java.util.Map;

public class QuickConsultReplyAction extends BaseConsultActivity {

    private String quick_id;

    private String parent_id;

    private String author_id;

    private String author_name;

    private String content;

    private boolean isLOrA;

    private String index;

    private Map<String,Object> result;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLOrA() {
        return isLOrA;
    }

    public void setLOrA(boolean LOrA) {
        isLOrA = LOrA;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public String getQuick_id() {
        return quick_id;
    }

    public void setQuick_id(String quick_id) {
        this.quick_id = quick_id;
    }

    public String execute() throws Exception{

        result = quickConsultReply(quick_id, parent_id, author_name, author_id, content, isLOrA, index);
        return SUCCESS;

    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
