package com.cms.demo.controller;

import com.cms.demo.bean.Conversation;
import com.cms.demo.bean.Message;
import com.cms.demo.bean.News;
import com.cms.demo.bean.User;
import com.cms.demo.service.UserService;
import com.cms.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @ResponseBody
    @RequestMapping("/reg")
    /**
     * 注册成功后自动登录并且跳转/重定向到首页显示所有信息
     * 注册失败返回Jsno 对象
     */
    public Result register(User user){
        Result result = userService.registerInfo(user);
        return result;
    }

    @ResponseBody
    @RequestMapping("/login")
    /**
     * 登录成功后跳转到显示所有信息
     * {"code":1,"msgname":"用户名不存在"}
     * 失败返回Jsno 对象
     */
    public Result login(User user, HttpSession session,Model model){
        Result result = userService.userLogin(user);
        if (result.getCode()==0){
            User user1 = userService.getUserByUsername(user.getUsername());
            session.setAttribute("user",user1);
            model.addAttribute("user",user1);
        }
        return result;
    }

    @RequestMapping("/user/{id}")
    public String detail(@PathVariable int id, HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user!=null){
            return "personal";
        }else {
            return "redirect:/pet.cloud.raising?pop=1" ;
        }
    }

    @ResponseBody
    @RequestMapping("/user/addNews")
    public Result addNews(News news,HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user!=null){
            Date data = new Date();
            news.setCreatedDate(data);
            news.setUid(user.getId());
            news.setCommentCount(0);
            news.setLikeCount(0L);
            Result result = userService.addSomeNews(news);
            return result;
        }else {
            Result result = new Result();
            result.setCode(1);
            return result;
        }
    }

    // 发私信 user/msg/addMessage

    @RequestMapping("/logout")
    public String userLogout(HttpSession session){
        session.invalidate();
        return "redirect:/pet.cloud.raising";
    }

    /**
     *在站内信里面关闭消息会删除这个conversationId 的
     * 所有信息
     *
     * 但是在删除下面消息队列里面的消息的时候
     *有一个 msg 对象 删除的 id 对应的应该是这个 消息队列里面的 id.
     *
     * 显示消息队列的时候。登录用户的站内信包括所有对此用户发的消息的赞以及评论数量的统计
     * 还要区分是赞 还是 评论消息 回显内容
     * 数据库设计应该是统计所有 newsId的 赞 以及 评论 当用户登录后根据其 uid 查找出其对应的
     * 消息 list
     * 谁发的 在哪发的
     *
     */
    @RequestMapping("/msg/list")
    public String messeageList(Model model,HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user!=null){
            List<Conversation> conversations = userService.getAllConversationByUid(user.getId());
            model.addAttribute("conversations",conversations);
            return "letter";
        }else {
            return "redirect:/pet.cloud.raising?pop=1";
        }
    }

    @RequestMapping("/msg/detail")
    public String letterDetail(Integer conversationId,Model model){
        List<Message> messages = userService.getMesseageByConversationId(conversationId);
        model.addAttribute("messages",messages);
        return "letterDetail";
    }

    @RequestMapping("/user/tosendmsg")
    public String sendMesg(){
        return "sendmsg";
    }


    //{"msg":"","code":"0"}
    @ResponseBody
    @RequestMapping("/user/msg/addMessage")
    public Result addMessage(String toName ,String content){
        Message message = new Message();
        Date date = new Date();
        message.getUser().setUsername(toName);
        message.setContent(content);
        message.setCreatedDate(date);
        return null;
    }


}
