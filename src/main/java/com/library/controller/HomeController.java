package com.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 简单的首页控制器
 * 直接跳转到登录页面
 */
@Controller
public class HomeController {

    /**
     * 处理根路径请求
     */
    @RequestMapping("/")
    public String home() {
        // 直接返回登录页面
        return "redirect:/index.html";
    }
}