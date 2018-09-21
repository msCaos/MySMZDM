package com.cms.demo.bean;

public class User {
    Integer id;
    String username;
    String password;
    String headUrl;
    Integer rember;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public Integer getRember() {
        return rember;
    }

    public void setRember(Integer rember) {
        this.rember = rember;
    }
}
