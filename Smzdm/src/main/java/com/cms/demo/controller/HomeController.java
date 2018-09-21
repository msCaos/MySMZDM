package com.cms.demo.controller;

import com.cms.demo.bean.*;
import com.cms.demo.service.HomeService;
import com.cms.demo.util.HomeResult;
import com.cms.demo.util.RedResult;
import com.cms.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
@Controller
public class HomeController {

    @Autowired
    HomeService homeService;
    @RequestMapping("/pet.cloud.raising")
    public String myhome(Model model,Integer pop, HttpSession session){
        List<Vo> vos = homeService.getAllInfo();
        model.addAttribute("vos",vos);
        if (pop!=null){
            if (pop==1){
                User user = (User) session.getAttribute("user");
                if (user==null){
                    model.addAttribute("pop",1);
                }
            }
        }
        return "/home";
    }


    /**
     *進來這個網頁的同時獲取所有的
     * 評論信息
     */
    @RequestMapping("/news/{id}")
    public String newsDetail(@PathVariable int id,Model model){
        News news = homeService.getDetailByTd(id);
        model.addAttribute("news",news);
        List<Commentvo> comments = homeService.getAllComments(id);
        model.addAttribute("comments",comments);
        return "detail";
    }

    /**
     *消息評論成功的同時news中對應的消息
     * commentCount 要增加1
     */
    @RequestMapping("/addComment")
    public String addComment(Comment comment, HttpSession session){
        User user = (User) session.getAttribute("user");
        Date date = new Date();
        comment.setCreatedDate(date);
        comment.setUserId(user.getId());

        homeService.addUserComment(comment);


        System.out.println(comment);
        return "redirect:/news/"+comment.getNewsId();
    }

    /**
     * 处理点赞的时候一个用户只能赞一次，每次赞之前查询点赞用户的信息，是否已经赞过
     * 有数量加一，取消点赞  在这之前都应该判断数量 已经赞过不能再赞,还没赞过不能取消
     * 同时注意 没有登录的用户不能点赞 {"msg":2,"code":0}  {"msg":6,"code":0}
     * 不是这个返回码就  出现错误，请重试  msg 里面是点赞数
     *
     * 说明要存储点赞用户的信息以及
     * 把点赞 作为站内信发给被点赞的 用户，该用户的站内信会话数量加一
     * 点赞成功 {"msg":1,"code":0}
     * 取消点赞成功 {"msg":1,"code":0}
     */

    @ResponseBody
    @RequestMapping("/like")
    public RedResult accountLike(Model model , HttpSession session, Integer newsId){
//        int account = homeService.selectLikeCount(newsId);
        Long count = homeService.selectLikeCount(newsId);
        User user = (User) session.getAttribute("user");
        if (user!=null){
//            HomeResult result = homeService.addLikeCount(newsId,user.getId());
            RedResult result = homeService.addLikeCount(newsId, user.getId());
            return result;
        }else {
            RedResult result = new RedResult();
            result.setCode(0);
            result.setMsg(count);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping("/dislike")
    public RedResult accountDislike(Model model , HttpSession session, Integer newsId){
        Long count = homeService.selectLikeCount(newsId);
        User user = (User) session.getAttribute("user");
        if (user!=null){
            RedResult result = homeService.minusLikeCount(newsId,user.getId());
            return result;
        }else {
            RedResult result = new RedResult();
            result.setCode(0);
            result.setMsg(count);
            return result;
        }
    }



}
