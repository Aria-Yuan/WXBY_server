package com.imudges.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class MainSearchAction extends SearchBaseAction  {

    private String key;
    private String pageType;

//    private int state = 0;
//    private Runnable gf = new GetFirm();
//    private Runnable gl = new GetLaw();
//    private Runnable glr = new GetLawyer();
//    private Runnable gj = new GetJudgement();
//    private Runnable gc = new GetCounseling();

//    private Thread getFirm = new Thread(gf);
//    private Thread getLaw = new Thread(gl);
//    private Thread getLawyer = new Thread(glr);
//    private Thread getJudgement = new Thread(gj);
//    private Thread getCounseling = new Thread(gc);

    private List<Map<String,Object>> result;

//    private List<Map<String, Object>> newsResult = new ArrayList<>();
//    private List<Map<String, Object>> firmResult = new ArrayList<>();
//    private List<Map<String, Object>> lawyerResult = new ArrayList<>();
//    private List<Map<String, Object>> judgementResult = new ArrayList<>();
//    private List<Map<String, Object>> counselingResult = new ArrayList<>();
//    private List<Map<String, Object>> lawResult = new ArrayList<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

////    news
//    protected List<Map<String, Object>> getNewsResult(){
//
//        return newsResult;
//    }
//
//    //law
//    protected List<Map<String, Object>> getLaw(){
//        try {
//            Document condition = new Document();
//            condition.append("keyword",key);
//            condition.append("item", 0);
//            lawResult = getLawResult(condition.toJson(), "0");
//            condition.append("item", 1);
//            lawResult.addAll(getLawResult( condition.toJson(), "0"));
//            System.out.println(lawResult.size());
//        }catch (Exception e){
//        }
//
//        return lawResult;
//    }
//
//    //lawyer
//    protected List<Map<String, Object>> getLawyer(){
//        try {
//            lawyerResult = getLawyerResult(key, "0");
//        }catch (Exception e){
//        }
//
//        return lawyerResult;
//    }
//
//    //case
//    protected List<Map<String, Object>> getJudgement(){
//        try {
//            Document condition = new Document();
//            condition.append("keyword",key);
//            judgementResult = getJudgementResult(condition.toJson(), "0");
//        }catch (Exception e){
//        }
//
//        return judgementResult;
//    }
//
//    //firm
//    protected List<Map<String, Object>> getFirm() {
//        try {
//            firmResult = getFirmResult(key, "0");
//        }catch (Exception e){
//        }
//
//        return firmResult;
//    }
//
//
//    //counseling
//    protected List<Map<String, Object>> getCounseling() {
//        try {
//            counselingResult = getCounselingResult(key, "0");
//        }catch (Exception e){
//        }
//
//        return counselingResult;
//    }

    //    news
    protected void getNewsResult(){

//        return newsResult;
    }

    //law
    protected void getLaw(){
        try {
            Document condition = new Document();
            condition.append("keyword",key);
            condition.append("item", 0);
            result = getLawResult(condition.toJson(), "0");
            condition.append("item", 1);
            result.addAll(getLawResult( condition.toJson(), "0"));
//            System.out.println(lawResult.size());
        }catch (Exception e){
        }

//        return lawResult;
    }

    //lawyer
    protected void getLawyer(){
        try {
            result = getLawyerResult(key, "0");
        }catch (Exception e){
        }

//        return lawyerResult;
    }

    //case
    protected void getJudgement(){
        try {
            Document condition = new Document();
            condition.append("keyword",key);
            result = getJudgementResult(condition.toJson(), "0");
        }catch (Exception e){
        }

//        return judgementResult;
    }

    //firm
    protected void getFirm() {
        try {
            result = getFirmResult(key, "0");
        }catch (Exception e){
        }

//        return firmResult;
    }


    //counseling
    protected void getCounseling() {
        try {
            result = getCounselingResult(key, "0");
        }catch (Exception e){
        }

//        return counselingResult;
    }

    public String execute() throws Exception{
        if (getPageType().equals("0")) {
//            getLawyer.start();
            getLawyer();
//            while (getLawyer.isAlive()) { }
//            if (lawyerResult.size() == 0)
//                result.put("lawyer", "0");
//            else result.put("lawyer", lawyerResult);
        }else if (getPageType().equals("1")){
//            getCounseling.start();
            getCounseling();
//            while (getCounseling.isAlive()) { }
//            if (counselingResult.size() == 0)
//                result.put("counseling", "1");
//            else result.put("counseling", counselingResult);
        } else if (getPageType().equals("2")) {
//            getLaw.start();
            getLaw();
//            while (getLaw.isAlive()){ }
//            if (lawResult.size() == 0)
//                result.put("law", "2");
//            else result.put("law", lawResult);
        } else if (getPageType().equals("3")) {
//            getFirm.start();
            getFirm();
//            while (getFirm.isAlive()) { }
//            if (firmResult.size() == 0)
//                result.put("firm", "3");
//            else result.put("firm", firmResult);
        }else if(getPageType().equals("4")) {
//            getJudgement.start();
            getJudgement();
//            while (getJudgement.isAlive()) { }
//            if (judgementResult.size() == 0)
//                result.put("judgement", "4");
//            else result.put("judgement", judgementResult);
        }else if(getPageType().equals("4")) {
//            getJudgement.start();
            getNewsResult();
//            while (getJudgement.isAlive()) { }
//            if (newsResult.size() == 0)
//                result.put("news", "5");
//            else result.put("news", newsResult);
        }
        System.out.println(result);
        return SUCCESS;
    }

//        result.put("lawyer", 1);
//        result.put("counsel", 2);
//        result.put("law", 3);
//        result.put("firm", 4);
//        result.put("judgement", 5);
/*
    class GetFirm implements Runnable{
        @Override
        public void run(){
            try {
                firmResult = getResult("firm", key, "0");
//                System.out.println(firmResult);
                state ++;
            }catch (Exception e){
                state ++;
            }
        }
    }

    class GetLaw implements Runnable{
        @Override
        public void run(){
            try {
                Document condition = new Document();
                condition.append("keyword",key);
                condition.append("item", 0);
                lawResult = getResult("law", condition.toJson(), "0");
                condition.append("item", 1);
                lawResult.addAll(getResult("law", condition.toJson(), "0"));
                state ++;
            }catch (Exception e){
                state ++;
            }
        }
    }

    class GetLawyer implements Runnable{
        @Override
        public void run(){
            try {
                lawyerResult = getResult("lawyer", key, "0");
                state ++;
            }catch (Exception e){
                state ++;
            }
        }
    }

    class GetJudgement implements Runnable{
        @Override
        public void run(){
            try {
                Document condition = new Document();
                condition.append("keyword",key);
                judgementResult = getResult("judgement", condition.toJson(), "0");
                state ++;
            }catch (Exception e){
                state ++;
            }
        }
    }

    class GetCounseling implements Runnable{
        @Override
        public void run(){
            try {
                counselingResult = getResult("counseling", key, "0");
                state ++;
            }catch (Exception e){
                state ++;
            }
        }
    }
*/
}
