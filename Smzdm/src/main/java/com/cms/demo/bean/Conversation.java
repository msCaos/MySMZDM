package com.cms.demo.bean;

import java.util.Date;

public class Conversation {

    //通过一个conversationId 可以查出 一个 uid 评论的所有内容
    Integer conversationId;

    /**
     *  id用来统计和一个 user 评论了 给自己共发了多少条消息
     *     包括赞 评论 私信  一个 conversationId里面对应了多少条消息
     */
    Integer id;

    /**
     *  unread 发给用户的消息有多少条未读，上次点开之后消息进行统计
     *     未读消息清空为 0 ，到下次点开查看消息之间 ，如果再有消息进来
     *     就表示为未读。。通过上次该用户有几条消息 ，这次有几条，差值就是未读消息
     */
    Integer unread;

    //这个uid 表明是谁评论了这条消息，即评论这条消息的人的身份
    Integer uid;
    User user;

    /**
     * 评论的内容，把日期也加进去？这个评论包括 赞 评论 私信
     *     content 显示的是最近最新的一条消息 ，那就每次来一条消息就
     *      set 进去 ，覆盖掉之前的消息。则最后一条一定是最新的，同时 每次 set 一条 前
     *     先判空？ 在上次统计的数量上面加 1
     */
    String content;
    Conversation conversation;
    Integer comtToUid;

    //显示的是最近最新的一条消息 的创建时间
    Date createdDate;

    public Conversation() {
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUnread() {
        return unread;
    }

    public void setUnread(Integer unread) {
        this.unread = unread;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Integer getComtToUid() {
        return comtToUid;
    }

    public void setComtToUid(Integer comtToUid) {
        this.comtToUid = comtToUid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
