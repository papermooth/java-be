package com.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接测试工具类
 * 实现CommandLineRunner接口，在应用启动时自动运行测试
 */
@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n===== 开始测试数据库连接 =====");
        
        // 测试1: 使用DataSource直接测试连接
        testConnectionWithDataSource();
        
        // 测试2: 使用JdbcTemplate测试查询
        testConnectionWithJdbcTemplate();
        
        System.out.println("===== 数据库连接测试结束 =====\n");
    }

    /**
     * 使用DataSource测试数据库连接
     */
    private void testConnectionWithDataSource() {
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(5); // 5秒超时
            if (isValid) {
                System.out.println("✓ DataSource连接测试成功!");
                System.out.println("  - 数据库URL: " + connection.getMetaData().getURL());
                System.out.println("  - 数据库用户: " + connection.getMetaData().getUserName());
                System.out.println("  - 数据库产品: " + connection.getMetaData().getDatabaseProductName() + " " + 
                                   connection.getMetaData().getDatabaseProductVersion());
            } else {
                System.out.println("✗ DataSource连接测试失败: 连接无效");
            }
        } catch (SQLException e) {
            System.out.println("✗ DataSource连接测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 使用JdbcTemplate测试数据库查询
     */
    private void testConnectionWithJdbcTemplate() {
        try {
            // 执行一个简单的SQL查询来测试连接
            String sql = "SELECT 1 FROM dual";
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
            
            if (result != null && result == 1) {
                System.out.println("✓ JdbcTemplate查询测试成功!");
                
                // 尝试查询数据库中的表信息
                try {
                    int tableCount = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()", 
                            Integer.class);
                    System.out.println("  - 数据库中存在 " + tableCount + " 个表");
                } catch (Exception e) {
                    System.out.println("  - 无法获取表数量信息: " + e.getMessage());
                }
            } else {
                System.out.println("✗ JdbcTemplate查询测试失败: 返回结果异常");
            }
        } catch (Exception e) {
            System.out.println("✗ JdbcTemplate查询测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 手动触发数据库连接测试的方法
     * 可以通过Controller或其他地方调用此方法进行手动测试
     */
    public String testConnectionManually() {
        StringBuilder result = new StringBuilder();
        result.append("<h2>数据库连接测试结果</h2>");
        
        // 测试DataSource连接
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(5);
            if (isValid) {
                result.append("<p style='color:green;'>✓ DataSource连接测试成功!</p>");
                result.append("<p>数据库URL: " + connection.getMetaData().getURL() + "</p>");
                result.append("<p>数据库用户: " + connection.getMetaData().getUserName() + "</p>");
            } else {
                result.append("<p style='color:red;'>✗ DataSource连接测试失败: 连接无效</p>");
            }
        } catch (SQLException e) {
            result.append("<p style='color:red;'>✗ DataSource连接测试失败: " + e.getMessage() + "</p>");
        }
        
        // 测试JdbcTemplate查询
        try {
            String sql = "SELECT 1 FROM dual";
            Integer queryResult = jdbcTemplate.queryForObject(sql, Integer.class);
            if (queryResult != null && queryResult == 1) {
                result.append("<p style='color:green;'>✓ JdbcTemplate查询测试成功!</p>");
            } else {
                result.append("<p style='color:red;'>✗ JdbcTemplate查询测试失败: 返回结果异常</p>");
            }
        } catch (Exception e) {
            result.append("<p style='color:red;'>✗ JdbcTemplate查询测试失败: " + e.getMessage() + "</p>");
        }
        
        return result.toString();
    }
}