package com.cms.demo.service;

import com.cms.demo.bean.Conversation;
import com.cms.demo.bean.Message;
import com.cms.demo.bean.News;
import com.cms.demo.bean.User;
import com.cms.demo.util.Result;

import java.util.List;

public interface UserService {
    Result registerInfo(User user);

    Result userLogin(User user);

    Result addSomeNews(News news);

    User getUserByUsername(String username);

    List<Conversation> getAllConversationByUid(Integer id);

    List<Message> getMesseageByConversationId(Integer conversationId);

}
