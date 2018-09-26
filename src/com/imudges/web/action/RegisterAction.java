package com.imudges.web.action;

import java.util.List;
import java.util.Map;

public class RegisterAction extends SearchBaseAction {

    private String type;
    private String username;
    private String password;

    private Map<String,Object> result;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String execute() throws Exception{

        result = loginAndRegister(type, username, password);
        return SUCCESS;

    }

}
