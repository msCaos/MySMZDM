package com.cms.demo;

import com.cms.demo.bean.LikeCount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
        int newsId =1;
        Integer uid=1;
        Jedis jedis = new Jedis();
        LikeCount likeCount = new LikeCount();
//        jedis.set("a", "b","nx");
//        jedis.set("a", "c","xx");
//       String s = jedis.get("a");
//        jedis.set("a", "c");

        jedis.sadd("link1","b","c","d");

        Set<String> a = jedis.smembers("link1");
        // System.out.println("**********s**************"+s);
        System.out.println("**********s**************"+a);
        String s = "like";
        String key = s+newsId;
        /**
         * 思路：为每一个表分别构建一个 like+newsId  是一个集合 uid  sadd,每次
         * 点赞进来后先查询 里面 uid 是否存在，若存在，则还是返回 之前的点赞数
         * 否则，就把 uid 加入到集合中。 数组的长度是点赞的人数
         * 同样，踩进来后，也放到 dislike +newsId , 数组的长度是点踩的人数
         * 最后点赞的人数为  SCARD(like) - SCARD（dislike）
         */


    }

}
