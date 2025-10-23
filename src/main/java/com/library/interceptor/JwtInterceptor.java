package com.library.interceptor;

import com.library.common.Result;
import com.library.utils.JwtUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Authorization头
        String token = request.getHeader("Authorization");

        // 如果是登录请求，直接放行
        if (request.getRequestURI().contains("/api/login") || request.getRequestURI().contains("/api/logout")) {
            return true;
        }

        // 验证Token
        if (token == null || !JwtUtils.validateToken(token)) {
            // 返回未授权错误
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            try {
                Result result = Result.error("未授权访问，请先登录");
                response.getWriter().append(JSON.toJSONString(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        // 从Token中获取用户名并设置到请求中
        String username = JwtUtils.getUsernameFromToken(token);
        request.setAttribute("username", username);

        return true;
    }
}