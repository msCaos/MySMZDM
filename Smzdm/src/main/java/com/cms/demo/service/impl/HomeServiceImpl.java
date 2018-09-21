package com.cms.demo.service.impl;

import com.cms.demo.bean.*;
import com.cms.demo.dao.HomeDao;
import com.cms.demo.dao.UserDao;
import com.cms.demo.service.HomeService;
import com.cms.demo.util.HomeResult;
import com.cms.demo.util.RedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    HomeDao homeDao;

    @Autowired
    UserDao userDao;
    @Override
    public List<Vo> getAllInfo() {
        List<Vo> vos = new ArrayList<>();
        List<News> newsList = homeDao.getAllNewsInfo();
        for (News news:newsList) {
            Vo vo = new Vo();
            User user = news.getUser();
            Long count = this.selectLikeCount(news.getId());
            news.setLikeCount(count);
            vo.setNews(news);
            vo.setUser(user);
            vos.add(vo);
        }
        /**
         * 返回之前再獲取這條消息的用戶信息
         */
        return vos;
    }

    @Override
    public News getDetailByTd(int id) {
        News news = homeDao.getById(id);
        Long count = this.selectLikeCount(id);
        news.setLikeCount(count);
        return news;
    }

    @Override
    public void addUserComment(Comment comment) {

        homeDao.insertComment(comment);

        //通过newsId 拿到 一个 news 对象
        homeDao.addCommentCount(comment.getNewsId());
        News news = homeDao.getById(comment.getNewsId());
        news.setUid(news.getUser().getId());

        //填充一个 conversation 对象 ，每次填充前 先查询
        //第一次评论这个ComtToUid 的news ---insert
        //还是第二次评论----update
        Jedis jedis = new Jedis();
        String keyCommentOwnerId ="cometNewsOwnerId"+news.getUid();
        Long result = jedis.sadd(keyCommentOwnerId, String.valueOf(comment.getUserId()));

        Conversation conversation = new Conversation();
        conversation.setContent(comment.getContent());
        conversation.setComtToUid(news.getUid());
        conversation.setUid(comment.getUserId());
        conversation.setCreatedDate(comment.getCreatedDate());

        /**
         * 此处站内信部分只包含 评价 ，赞 私信 的 部分 也都应该被纳入到
         *  数据库 的统计范畴
         */
        Message message = new Message();
        message.setComtToUid(news.getUid());
        message.setContent(comment.getContent());
        message.setCreatedDate(comment.getCreatedDate());
        message.setUid(comment.getUserId());
        userDao.insertMessage(message);

        if (result==0){
            //已经存在 ，update
            userDao.updateConversationByUid(conversation);
        }else {
            //不曾存在，insert
            conversation.setId(1);
            conversation.setUnread(1);
            userDao.insertConversation(conversation);
        }

    }

    @Override
    public List<Commentvo> getAllComments(int id) {
        List<Commentvo> commentvos = new ArrayList<>();
        List<Comment> commentList = homeDao.getComentsByNewsId(id);
        for (Comment coment:commentList) {
            Commentvo commentvo = new Commentvo();
            commentvo.setComment(coment);
            commentvo.setUser(coment.getUser());
            commentvos.add(commentvo);
        }
        return commentvos;
    }

    @Override
    public Long selectLikeCount(Integer newsId) {
        Jedis jedis = new Jedis();
        String keylike = "like"+newsId;
        String keydis = "dislike"+newsId;
        Long scardLike = jedis.scard(keylike);
        Long scardDis = jedis.scard(keydis);
        return scardLike-scardDis;
    }

    @Override
    public RedResult addLikeCount(Integer newsId, Integer uid) {
        News news = homeDao.getById(newsId);
        Date date = new Date();
        Long count = this.selectLikeCount(newsId);
        RedResult result = new RedResult();
        Jedis jedis = new Jedis();
        String keylike = "like"+newsId;
        String keydis = "dislike"+newsId;
        Boolean ismemberLike = jedis.sismember(keylike, String.valueOf(uid));
        Boolean ismemberDis = jedis.sismember(keydis, String.valueOf(uid));
        /**
         * uid已经在集合中，说明已经赞过，不能再赞,此时还要判断它是否踩过，如果踩过，则把这个 id 从
         *          踩 的集合中 移除
         *         先判断是否踩过，如果踩过，移除，可以直接赞
         */
        if (ismemberDis){
            jedis.srem(keydis,String.valueOf(uid));
            jedis.sadd(keylike, String.valueOf(uid));
            result.setMsg(count+1);
            result.setCode(0);
        }else {
            //还没有踩过，需要判断是否赞过
            if (ismemberLike){
                result.setMsg(count);
                result.setCode(0);
            }else {
                //说明该用户还没有点赞，加入到点赞集合
                jedis.sadd(keylike,String.valueOf(uid));
                result.setMsg(count+1);
                result.setCode(0);

                /**
                 * 解决把 赞  消息添加进数据库
                 */
                Message message = new Message();
                message.setComtToUid(news.getUid());
                message.setContent("赞了你的分享");
                message.setCreatedDate(date);
                message.setUid(uid);
                userDao.insertMessage(message);
            }
        }

        /**
         * 这里解决当点赞 的同时对 用户发送消息
         */

        news.setUid(news.getUser().getId());
        String keyCommentOwnerId ="cometNewsOwnerId"+news.getUid();
        Long res = jedis.sadd(keyCommentOwnerId, String.valueOf(uid));
        Conversation conversation = new Conversation();
        conversation.setCreatedDate(date);
        conversation.setUid(uid);
        conversation.setComtToUid(news.getUid());
        conversation.setContent("赞了你的分享");
        if (res==1){
            //添加成功，表示该 uid 不存在，insert
            conversation.setUnread(1);
            conversation.setId(1);
            userDao.insertConversation(conversation);
        }else if (!ismemberLike){
            //添加失败，表示该 uid 存在，update
            userDao.updateConversationByUid(conversation);
        }
        return result;
    }

    @Override
    public RedResult minusLikeCount(Integer newsId, Integer uid) {
        Long count = this.selectLikeCount(newsId);
        RedResult result = new RedResult();
        Jedis jedis = new Jedis();
        String keylike = "like"+newsId;
        String keydis = "dislike"+newsId;
        Boolean ismemberLike = jedis.sismember(keylike, String.valueOf(uid));
        Boolean ismemberDis = jedis.sismember(keydis, String.valueOf(uid));
        //先判断是否赞过，如果赞过，移除，可以直接踩
        if (ismemberLike){
            jedis.srem(keylike,String.valueOf(uid));
            result.setMsg(count-1);
            result.setCode(0);
        }else {
            //没有赞过，在看是否踩过
            if (ismemberDis){
                result.setMsg(count);
                result.setCode(0);
            }else {
                //说明该用户还没有点赞，加入到点赞集合
                jedis.sadd(keydis, String.valueOf(uid));
                result.setMsg(count-1);
                result.setCode(0);
            }
        }
        return result;
    }

    /**
     *用redis 的话：数据库的设计 news{newsId ,uid ,likecount}
     */
//    @Override
//    public HomeResult addLikeCount(Integer newsId,Integer uid) {
//        HomeResult result = new HomeResult();
//        int count = this.selectLikeCount(newsId);
//        LikeCount likeCount = new LikeCount();
//        likeCount.setNewsId(newsId);
//        likeCount.setUid(uid);
//        Date date = new Date();
//        likeCount.setCreatedDate(date);
//        LikeCount lCount = homeDao.selectUserLikeCountByNidAndUid(likeCount);
//        if (lCount!=null){
//            //此时为踩，可以赞一下，相当于啥都没干
//            if (lCount.getLikecount()==-1){
//                homeDao.addNewsLikeCountById(newsId);
//                homeDao.deleteLikeCount(likeCount);
//                result.setMsg(count+1);
//                result.setCode(0);
//            }else  if (lCount.getLikecount()==1){
//                //说明已存在该用户对该新闻的点赞
//                result.setMsg(count);
//                result.setCode(0);
//            }
//        }else {
//            //不存在该用户对该新闻的点赞，在点赞改变news的同时还要把点赞用户信息放进
//            //likeCount 数据库 现在发现 likeCount 的设计多余，只要能查询出不为空就说明
//            //已经存在用户对该 news 的点赞
//            homeDao.addNewsLikeCountById(newsId);
//            likeCount.setLikecount(1);
//            homeDao.insertToLikeCount(likeCount);
//            result.setMsg(count+1);
//            result.setCode(0);
//        }
//        return result;
//    }
//
//    @Override
//    public int selectLikeCount(Integer newsId) {
//        News news = homeDao.getById(newsId);
//        return news.getLikeCount();
//    }
//
//    @Override
//    public HomeResult minusLikeCount(Integer newsId, Integer uid) {
//        HomeResult result = new HomeResult();
//        int count = this.selectLikeCount(newsId);
//        LikeCount likeCount = new LikeCount();
//        Date date = new Date();
//        likeCount.setCreatedDate(date);
//        likeCount.setNewsId(newsId);
//        likeCount.setUid(uid);
//        LikeCount lCount = homeDao.selectUserLikeCountByNidAndUid(likeCount);
//        //该用户点赞记录还存在，有可能是赞过，likecount 值为1
//        // 删除该记录，返回赞数减 1 ,数据库数量减去 1
//        if (lCount!=null){
//
//            //已经踩过，不能再踩
//            if (lCount.getLikecount()==-1){
//                result.setMsg(count);
//                result.setCode(0);
//            }else if (lCount.getLikecount()==1){
//                //这是赞，踩一下，删掉存储的用户信息
//                homeDao.minusNewsLikeCountById(newsId);
//                homeDao.deleteLikeCount(likeCount);
//                result.setMsg(count-1);
//                result.setCode(0);
//            }
//        }else {
//            //该用户点赞记录不存在，此时为踩，点赞数减去一的同时将用户信息放入
//            // 统计点赞信息的数据库
//            likeCount.setLikecount(-1);
//            homeDao.insertToLikeCount(likeCount);
//            homeDao.minusNewsLikeCountById(newsId);
//            result.setMsg(count-1);
//            result.setCode(0);
//        }
//        return result;
//    }


}
