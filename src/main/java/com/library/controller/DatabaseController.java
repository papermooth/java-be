package com.library.controller;

import com.library.util.DatabaseConnectionTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据库相关操作的Controller
 * 提供数据库连接测试等功能
 */
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private DatabaseConnectionTest databaseConnectionTest;

    /**
     * 测试数据库连接
     * 此接口会直接测试数据库连接并返回详细结果
     * @return 包含测试结果的HTML格式响应
     */
    @GetMapping("/test-connection")
    public String testConnection() {
        return databaseConnectionTest.testConnectionManually();
    }
    
    /**
     * 快速检查数据库连接状态
     * 返回简单的JSON格式，便于程序调用
     * @return 包含连接状态的简单文本
     */
    @GetMapping("/status")
    public String checkStatus() {
        try {
            // 这里简单调用测试方法的一部分逻辑
            // 不返回完整HTML，只返回简单状态
            String testResult = databaseConnectionTest.testConnectionManually();
            if (testResult.contains("✓ DataSource连接测试成功")) {
                return "{\"status\":\"success\",\"message\":\"数据库连接正常\"}";
            } else {
                return "{\"status\":\"error\",\"message\":\"数据库连接失败\"}";
            }
        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\"测试过程中出现异常: " + e.getMessage() + "\"}";
        }
    }
}
