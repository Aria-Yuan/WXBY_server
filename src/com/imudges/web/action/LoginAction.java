package com.imudges.web.action;

import java.util.Map;

/**
 * Created by HUPENG on 2017/4/30.
 */
public class LoginAction extends SearchBaseAction {
    /**
     * 用户请求参数
     * */
    private String username;
    private String password;

    /**
     * 返回结果
     * */
    private Map<String,Object>result;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    @Override
    public String execute() throws Exception {
        if ("admin".equals(username) && "123".equals(password)){
            result = getSuccessResult(null);
        }else {
            result = getFailResult(-1,"用户名或者密码错误");
        }
        return SUCCESS;
    }

}