package com.hisun.web.controller;

import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hisun.common.bean.User;
import com.hisun.common.exception.UserServiceException;
import com.hisun.common.util.ResultObject;
import com.hisun.service.UserService;

/**
 * 
 * @类名： LoginController.java
 * 
 * @描述：LoginController @作者： PAUL @修改日期： 2016年3月28日
 *
 */
@Controller
public class LoginController
{
    @Resource
    private UserService userService;


    @RequestMapping(value = "index.xhtml", method = RequestMethod.GET)
    public ModelAndView gotoIndex()
    {
        System.out.println("index");
        ModelAndView model = new ModelAndView("index");
        return model;
    }


    @RequestMapping(value = "login.xhtml", method = RequestMethod.GET)
    public ModelAndView gotoLogin()
    {
        System.out.println("login");
        ModelAndView model = new ModelAndView("login");
        return model;
    }


    @RequestMapping(value = "user_login.json", method = RequestMethod.POST)
    @ResponseBody
    public ResultObject userLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "username", required = false) String username,
        @RequestParam(value = "password", required = false) String password, @RequestParam(value = "authCode", required = false) String authCode,
        @RequestParam(value = "autoLogin", required = false) int autoLogin) throws Exception
    {
        String code = (String) request.getSession().getAttribute("authCode");
        System.out.println(code);
        System.err.println(authCode);
        if (!authCode.equals(code))
        {
            return new ResultObject(110, "验证码不准确!");
        }

        try
        {
            User user = userService.login(username, password);
            request.getSession().setAttribute("user", user);
        }
        catch (UserServiceException e)
        {
            return new ResultObject(110, e.getMessage());
        }

        // 如果用户选择自动登录
        if (autoLogin > 0)
        {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0)
            {
                for (Cookie c : cookies)
                {
                    if (c.getName().equals("username"))
                    {
                        c.setMaxAge(0);
                    }
                }
                // 定义新的cookie
                String name = URLEncoder.encode("username", "UTF-8");
                Cookie namecookie = new Cookie(name, username);
                // 定义cookie生命周期
                namecookie.setMaxAge(1000);
                // 把cookie加入到浏览器
                response.addCookie(namecookie);
            }
        }
        return new ResultObject();
    }

}