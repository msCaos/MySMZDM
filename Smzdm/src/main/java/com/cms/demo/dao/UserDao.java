package com.cms.demo.dao;

import com.cms.demo.bean.Conversation;
import com.cms.demo.bean.Message;
import com.cms.demo.bean.News;
import com.cms.demo.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    boolean insterRegisterInfo(User user);

    User selectByUsername(String username);

    User selectUserById(int id);

    boolean insertNews(News news);

    List<News> selectNidByUid(Integer uid);

    void insertConversation(Conversation conversation);

    void updateConversationByUid(Conversation conversation);

    List<Conversation> getAllConversationByComtToUid(Integer comtToUid);

    Conversation getConversationByConversationId(Integer conversationId);

    void insertMessage(Message message);

    void updateConversationUnread(Integer conversationId);

    List<Message> getMessagesByComtToUidAndUid(Message message);
}
