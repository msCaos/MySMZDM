package com.cms.demo.dao;

import com.cms.demo.bean.Comment;
import com.cms.demo.bean.LikeCount;
import com.cms.demo.bean.News;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeDao {

    List<News> getAllNewsInfo();

    News getById(int id);

    void insertComment(Comment comment);

    void addCommentCount(Integer newsId);

    List<Comment> getComentsByNewsId(int id);

    void  addNewsLikeCountById(Integer newsId);

    LikeCount selectUserLikeCountByNidAndUid(LikeCount likeCount);

    void insertToLikeCount(LikeCount likeCount);

    void deleteLikeCount(LikeCount likeCount);

    void minusNewsLikeCountById(Integer newsId);


    List<Comment> getComentsByNewsIdAndUid(Comment comment);
}
