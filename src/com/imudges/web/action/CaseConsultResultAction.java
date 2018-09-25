package com.imudges.web.action;

import java.util.Map;

public class CaseConsultResultAction extends SearchBaseAction {

    private String case_id;
    private String content;
    private String resultt;
    private String[] similar = new String[3];
    private String[] refer = new String[3];
    private String state;

    private Map<String,Object> result;


    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResultt() {
        return resultt;
    }

    public void setResultt(String resultt) {
        this.resultt = resultt;
    }

    public String[] getSimilar() {
        return similar;
    }

    public void setSimilar(String[] similar) {
        this.similar = similar;
    }

    public String[] getRefer() {
        return refer;
    }

    public void setRefer(String[] refer) {
        this.refer = refer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public String execute() throws Exception{

        result = getCaseConsultResult(case_id);
        return SUCCESS;

    }

}
