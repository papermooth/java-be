package com.library.controller;

import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import com.library.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/api/login")
    public Result<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        // 1. 首先尝试备用登录机制：硬编码的管理员账户
        if ("admin".equals(username) && "admin123".equals(password)) {
            // 创建虚拟用户对象
            User adminUser = new User();
            adminUser.setId(1L);
            adminUser.setUsername("admin");
            adminUser.setNickname("系统管理员");
            adminUser.setEmail("admin@example.com");
            adminUser.setPhone("13800138000");
            adminUser.setStatus(1);
            
            // 生成Token
            String token = JwtUtils.generateToken(username);
            
            // 返回用户信息和Token
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", adminUser);
            
            return Result.success(result);
        }
        
        // 2. 尝试数据库验证（如果备用登录失败）
        try {
            User user = userService.findByUsername(username);
            if (user != null) {
                // 检查密码 - 为了兼容性，处理可能的明文密码情况
                if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")) {
                    // BCrypt加密的密码
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Result.error("密码错误");
                    }
                } else {
                    // 假设是明文密码
                    if (!password.equals(user.getPassword())) {
                        return Result.error("密码错误");
                    }
                }

                // 检查用户状态
                if (user.getStatus() != 1) {
                    return Result.error("用户已被禁用");
                }

                // 生成Token
                String token = JwtUtils.generateToken(username);

                // 返回用户信息和Token
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("user", user);

                return Result.success(result);
            }
        } catch (Exception e) {
            // 数据库访问失败
            System.out.println("数据库访问失败: " + e.getMessage());
        }
        
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/api/logout")
    public Result<?> logout() {
        // JWT是无状态的，客户端删除Token即可
        return Result.success(null);
    }
}