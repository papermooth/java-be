import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * 独立的数据库连接测试类
 * 可以直接编译运行，不需要等待整个Spring Boot应用构建完成
 */
public class TestDatabaseConnection {

    public static void main(String[] args) {
        System.out.println("===== 开始数据库连接测试 =====");
        
        // 数据库连接参数
        String serverUrl = "jdbc:mysql://192.168.13.247:3306?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true";
        String dbUrl = "jdbc:mysql://192.168.13.247:3306/library_management?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true";
        String username = "root";
        String password = "123456";
        String databaseName = "library_management";
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            // 加载MySQL驱动
            System.out.println("正在加载MySQL驱动...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL驱动加载成功!");
            
            // 先连接到MySQL服务器（不指定数据库）
            System.out.println("正在连接MySQL服务器...");
            connection = DriverManager.getConnection(serverUrl, username, password);
            System.out.println("✓ MySQL服务器连接成功!");
            
            // 检查数据库是否存在
            System.out.println("\n检查数据库 '" + databaseName + "' 是否存在...");
            statement = connection.createStatement();
            resultSet = statement.executeQuery(
                "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = '" + databaseName + "'");
            
            if (!resultSet.next()) {
                System.out.println("数据库 '" + databaseName + "' 不存在，尝试创建...");
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                System.out.println("✓ 数据库 '" + databaseName + "' 创建成功!");
            } else {
                System.out.println("✓ 数据库 '" + databaseName + "' 已存在");
            }
            
            // 关闭当前连接，重新连接到指定数据库
            connection.close();
            System.out.println("\n正在连接到数据库 '" + databaseName + "'...");
            connection = DriverManager.getConnection(dbUrl, username, password);
            
            // 测试连接是否有效
            if (connection != null && connection.isValid(5)) {
                System.out.println("✓ 数据库连接成功!");
                
                // 获取数据库元数据
                System.out.println("\n数据库信息:");
                System.out.println("- URL: " + dbUrl);
                System.out.println("- 用户名: " + username);
                System.out.println("- 数据库产品: " + connection.getMetaData().getDatabaseProductName() + " " + 
                                  connection.getMetaData().getDatabaseProductVersion());
                System.out.println("- JDBC驱动: " + connection.getMetaData().getDriverName() + " " + 
                                  connection.getMetaData().getDriverVersion());
                
                // 执行简单查询测试
                System.out.println("\n执行测试查询...");
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT 1 FROM dual");
                
                if (resultSet.next() && resultSet.getInt(1) == 1) {
                    System.out.println("✓ 查询测试成功!");
                }
                
                // 尝试获取数据库中的表信息
                try {
                    System.out.println("\n查询数据库表信息...");
                    resultSet = statement.executeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()");
                    
                    System.out.println("数据库中的表:");
                    int tableCount = 0;
                    while (resultSet.next()) {
                        System.out.println("- " + resultSet.getString(1));
                        tableCount++;
                    }
                    System.out.println("总计: " + tableCount + " 个表");
                    
                    // 如果没有表，可以提示用户
                    if (tableCount == 0) {
                        System.out.println("\n注意: 数据库中没有表，应用启动后可能需要初始化表结构");
                    }
                } catch (SQLException e) {
                    System.out.println("无法获取表信息: " + e.getMessage());
                }
                
                // 显示数据库连接状态摘要
                System.out.println("\n===== 数据库连接测试结果摘要 =====");
                System.out.println("✓ MySQL服务器连接: 成功");
                System.out.println("✓ 数据库 '" + databaseName + "': 可用");
                System.out.println("✓ 查询测试: 成功");
                System.out.println("=================================");
                
            } else {
                System.out.println("✗ 数据库连接失败: 连接无效");
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("✗ 数据库驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("✗ 数据库连接异常: " + e.getMessage());
            e.printStackTrace();
            
            // 分析常见的连接错误
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Connection refused")) {
                System.out.println("\n可能的原因:");
                System.out.println("1. MySQL服务器未启动");
                System.out.println("2. 服务器地址或端口错误");
                System.out.println("3. 防火墙阻止了连接");
            } else if (errorMessage.contains("Access denied")) {
                System.out.println("\n可能的原因:");
                System.out.println("1. 用户名或密码错误");
                System.out.println("2. 用户没有远程访问权限");
            } else if (errorMessage.contains("Unknown database")) {
                System.out.println("\n可能的原因:");
                System.out.println("1. 数据库不存在");
                System.out.println("2. 数据库名称拼写错误");
                System.out.println("\n注意: 此测试程序已经尝试自动创建数据库");
            }
            
        } finally {
            // 关闭资源
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("关闭资源时出错: " + e.getMessage());
            }
        }
        
        System.out.println("\n===== 数据库连接测试结束 =====");
    }
}