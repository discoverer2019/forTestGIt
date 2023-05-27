package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    // cookie示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie// cookie只能存储一个键值对
        Cookie cookie = new Cookie("code", "mycookie");
        // 设置cookie的生效范围
        cookie.setPath("/community/alpha");
        // 设置cookie的生存时间
        //cookie.setMaxAge(600);
        // 发送cookie  ，这个cookie会保存在响应头中
        // 如：code=mycookie; Max-Age=600; Expires=Thu, 05-Jan-2023 11:54:14 GMT; Path=/community/alpha
        response.addCookie(cookie);
        return " set  cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code, HttpServletRequest request) {
        String cookie = request.getHeader("Cookie");
        System.out.println(cookie); //code=mycookie
        System.out.println(code);   //mycookie
        return "get cookie";
    }

    // session 示例
    // 创建session的时候，会创建一个cookie
    // Set-Cookie: JSESSIONID=2C5E287BA04AC957A23D1B31A6E18E1E; Path=/community; HttpOnly
    // 获取的时候，会从客户端携带的cookie里，找到key值为JESSIONID的cookie，获取value，作为session的id从而获取session
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        System.out.println(session.getId());
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session, HttpServletRequest request) {
        String cookie = request.getHeader("Cookie");
        int i = cookie.lastIndexOf(';');
        System.out.println(cookie.substring(i));
        System.out.println(cookie);
        System.out.println(session.getId());
        Object id = session.getAttribute("id");
        System.out.println(id);
        Object name = session.getAttribute("name");
        System.out.println(name);
        return "set session";
    }
}
