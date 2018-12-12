package com.imudges.web.action;

import java.util.Map;

public class DeleteQuickConsultCommentAction extends BaseConsultAction {

    private String quick_id;

    private String reply_index;

    private String reply_id;

    private String parent_index;

    private String parent_id;

    private Map<String,Object> result;

    public String execute() throws Exception{

        setResult(deleteQuickConsultComment(getQuick_id(), getReply_index(), getReply_id(), getParent_index(), getParent_id()));
        return SUCCESS;

    }

    public String getQuick_id() {
        return quick_id;
    }

    public void setQuick_id(String quick_id) {
        this.quick_id = quick_id;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getReply_index() {
        return reply_index;
    }

    public void setReply_index(String reply_index) {
        this.reply_index = reply_index;
    }

    public String getParent_index() {
        return parent_index;
    }

    public void setParent_index(String parent_index) {
        this.parent_index = parent_index;
    }
}
