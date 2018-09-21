package com.cms.demo.service.impl;

import com.cms.demo.bean.*;
import com.cms.demo.dao.HomeDao;
import com.cms.demo.dao.UserDao;
import com.cms.demo.service.UserService;
import com.cms.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    HomeDao homeDao;
    @Override
    public Result registerInfo(User user) {
        Result result = new Result();
        User user1 = userDao.selectByUsername(user.getUsername());
        if (user1!=null){
            result.setCode(1);
            result.setMsgname("用户名已存在！");
        }else {
            int b=(int)(Math.random()*10);
            if (b%2==1){
                user.setHeadUrl("http://localhost:8080/userHead/boy001.jpg");
            }else {
                user.setHeadUrl("http://localhost:8080/userHead/girl002.jpg");
            }

            boolean res2 = userDao.insterRegisterInfo(user);
            result.setCode(0);
        }
        return result;
    }

    @Override
    public Result userLogin(User user) {
        Result result = new Result();
        User user1 = userDao.selectByUsername(user.getUsername());
        if (user1==null){
            result.setCode(1);
            result.setMsgname("该用户名不存在！");
        }else {
            if (!user1.getPassword().equals(user.getPassword())){
                result.setCode(1);
                result.setMsgpwd("密码不匹配！");
            }else {
                result.setCode(0);
            }
        }

        return result;
    }

    @Override
    public Result addSomeNews(News news) {
        Result result = new Result();
        boolean res = userDao.insertNews(news);
        if (res){
            result.setCode(0);
        }else {
            result.setCode(1);
        }
        return result;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userDao.selectByUsername(username);
        return user;
    }

    @Override
    public List<Conversation> getAllConversationByUid(Integer comtToUid) {
        //Jedis jedis = new Jedis();
        List<Conversation> conversations = userDao.getAllConversationByComtToUid(comtToUid);
        for (Conversation conversation :conversations) {
            conversation.setConversation(conversation);
        }
        return conversations;
    }

    @Override
    public List<Message> getMesseageByConversationId(Integer conversationId) {
        /**
         * 进来后 要把 未读消息 置 0
         */
        userDao.updateConversationUnread(conversationId);

        Conversation conversation = userDao.getConversationByConversationId(conversationId);
        Message message = new Message();
        message.setComtToUid(conversation.getComtToUid());
        message.setUid(conversation.getUid());
        List<Message> messages = userDao.getMessagesByComtToUidAndUid(message);
        return messages;
    }

    //私信内容




}
