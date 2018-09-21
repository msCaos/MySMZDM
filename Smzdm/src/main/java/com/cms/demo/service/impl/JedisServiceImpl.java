package com.cms.demo.service.impl;

import com.cms.demo.bean.*;
import com.cms.demo.service.HomeService;
import com.cms.demo.util.HomeResult;
import com.cms.demo.util.RedResult;
import redis.clients.jedis.Jedis;

import java.util.List;

public class JedisServiceImpl  {
    public Long selectLikeCount(Integer newsId){
        Jedis jedis = new Jedis();
        String keylike = "like"+newsId;
        String keydis = "dislike"+newsId;
        Long scardLike = jedis.scard(keylike);
        Long scardDis = jedis.scard(keydis);
        return scardLike-scardDis;
    }

    public RedResult addLikeCount(Integer newsId, Integer uid) {
        Long count = this.selectLikeCount(newsId);
        RedResult result = new RedResult();
        Jedis jedis = new Jedis();
        String keylike = "like"+newsId;
        Boolean ismember = jedis.sismember("keylike", String.valueOf(uid));
        //uid已经在集合中，说明已经赞过，不能再赞
        if (ismember){
            result.setMsg(count);
            result.setCode(0);
        }else {
            //说明该用户还没有点赞，加入到点赞集合
            jedis.sadd("keylike", String.valueOf(uid));
            result.setMsg(count+1);
            result.setCode(0);
        }
        return result;
    }

    public RedResult minusLikeCount(Integer newsId, Integer uid) {
        Long count = this.selectLikeCount(newsId);
        RedResult result = new RedResult();
        Jedis jedis = new Jedis();
        String keydis = "dislike"+newsId;
        Boolean ismember = jedis.sismember("dislike", String.valueOf(uid));
        //uid已经在集合中，说明已经赞过，不能再赞
        if (ismember){
            result.setMsg(count);
            result.setCode(0);
        }else {
            //说明该用户还没有点赞，加入到点赞集合
            jedis.sadd("dislike", String.valueOf(uid));
            result.setMsg(count-1);
            result.setCode(0);
        }
        return result;
    }


}
