package com.imudges.web.action;

import java.util.Map;

public class QuickConsultReplyListAction extends BaseConsultAction {

    private String quick_id;

    private String reply_id;

    private String reply_index;

    private Map<String,Object> result;

    public String execute() throws Exception{

        result = getQuickConsultReplyList(quick_id, reply_id, reply_index);
        return SUCCESS;

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

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getReply_index() {
        return reply_index;
    }

    public void setReply_index(String reply_index) {
        this.reply_index = reply_index;
    }
}
