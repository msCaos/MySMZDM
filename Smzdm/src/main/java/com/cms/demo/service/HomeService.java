package com.cms.demo.service;

import com.cms.demo.bean.Comment;
import com.cms.demo.bean.Commentvo;
import com.cms.demo.bean.News;
import com.cms.demo.bean.Vo;
import com.cms.demo.util.HomeResult;
import com.cms.demo.util.RedResult;

import java.util.List;

public interface HomeService {
    List<Vo> getAllInfo();

    News getDetailByTd(int id);

    void addUserComment(Comment comment);

    List<Commentvo> getAllComments(int id);

//    int selectLikeCount(Integer newsId);

//    HomeResult addLikeCount(Integer newsId,Integer uid);
//
//    HomeResult minusLikeCount(Integer newsId, Integer id);
    Long selectLikeCount(Integer newsId);

    RedResult addLikeCount(Integer newsId, Integer uid);

    RedResult minusLikeCount(Integer newsId, Integer id);
}
