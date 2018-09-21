package com.cms.demo.bean;

import java.util.Date;

public class Message {
    Integer id;
    Integer uid;
    Integer comtToUid;
    User user;
    Date createdDate;
    Message message;
    String content;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getComtToUid() {
        return comtToUid;
    }

    public void setComtToUid(Integer comtToUid) {
        this.comtToUid = comtToUid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
