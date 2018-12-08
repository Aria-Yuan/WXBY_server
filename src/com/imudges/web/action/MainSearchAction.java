package com.imudges.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class MainSearchAction extends SearchBaseAction  {

    private String key;

    private Map<String,Object> result = new HashMap<>(
    );

    private List<Map<String, Object>> newsResult;
    private List<Map<String, Object>> firmResult;
    private List<Map<String, Object>> lawyerResult;
    private List<Map<String, Object>> judgementResult;
    private List<Map<String, Object>> counselingResult;
    private List<Map<String, Object>> lawResult;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    //news
    protected List<Map<String, Object>> searchNews(){

        return newsResult;
    }

    //firm
    protected List<Map<String, Object>> getLaw(){
        try {
            Document condition = new Document();
            condition.append("keyword",key);
            condition.append("item", 0);
            lawResult = getLawResult(condition.toJson(), "0");
            condition.append("item", 1);
            lawResult.addAll(getLawResult(condition.toJson(), "0"));
            System.out.println(lawResult.size());
        }catch (Exception e){
        }

        return lawResult;
    }

    //lawyer
    protected List<Map<String, Object>> getLawyer(){
        try {
            lawyerResult = getLawyerResult(key, "0");
        }catch (Exception e){
        }

        return lawyerResult;
    }

    //case
    protected List<Map<String, Object>> getJudgement(){
        try {
            Document condition = new Document();
            condition.append("keyword",key);
            judgementResult = getJudgementResult(condition.toJson(), "0");
        }catch (Exception e){
        }

        return judgementResult;
    }

    //law
    protected List<Map<String, Object>> getFirm() {
        try {
            firmResult = getFirmResult(key, "0");
        }catch (Exception e){
        }

        return firmResult;
    }


    //counseling
    protected List<Map<String, Object>> getCounseling() {
        try {
            counselingResult = getCounselingResult(key, "0");
        }catch (Exception e){
        }

        return counselingResult;
    }


    public String execute() throws Exception{

        //result.put("news", getNews());
//        result.put("firm", getFirm());
//        result.put("lawyer", getLawyer());
//        result.put("judgement", getJudgement());
//        result.put("counsel", getCounseling());
        result.put("law", getLaw());
        System.out.println(result);
        return SUCCESS;

    }


}
