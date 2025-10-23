import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InitDatabase {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://192.168.13.247:3306/";  // 连接到MySQL服务器，不指定数据库
        String username = "root";
        String password = "123456";
        String dbName = "library_management";
        String sqlFile = "target/classes/init.sql";
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // 尝试加载MySQL驱动（使用不同的驱动类名）
            try {
                // 先尝试新版本驱动
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("✅ MySQL驱动(com.mysql.cj.jdbc.Driver)加载成功");
            } catch (ClassNotFoundException e1) {
                // 如果失败，尝试旧版本驱动
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    System.out.println("✅ MySQL驱动(com.mysql.jdbc.Driver)加载成功");
                } catch (ClassNotFoundException e2) {
                    // 驱动加载失败，尝试不使用Class.forName
                    System.out.println("⚠️  无法显式加载MySQL驱动，尝试自动加载...");
                }
            }
            
            // 连接到MySQL服务器
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ 成功连接到MySQL服务器");
            
            // 创建Statement对象
            stmt = conn.createStatement();
            
            // 读取SQL文件
            List<String> sqlStatements = readSqlFile(sqlFile);
            System.out.println("✅ 成功读取SQL文件，共" + sqlStatements.size() + "条语句");
            
            // 执行SQL语句
            int executedCount = 0;
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    stmt.executeUpdate(sql);
                    executedCount++;
                    System.out.println("✅ 执行SQL: " + sql.trim().substring(0, Math.min(100, sql.trim().length())) + 
                                      (sql.trim().length() > 100 ? "..." : ""));
                }
            }
            
            System.out.println("✅ 数据库初始化完成！成功执行" + executedCount + "条SQL语句");
            
        } catch (Exception e) {
            System.err.println("❌ 数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                // 忽略关闭资源的异常
            }
        }
    }
    
    // 读取SQL文件并分割为语句
    private static List<String> readSqlFile(String filePath) throws Exception {
        List<String> statements = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过注释行和空行
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                
                // 添加到当前语句
                sb.append(line.trim());
                
                // 如果以分号结尾，说明是完整的语句
                if (sb.toString().endsWith(";")) {
                    statements.add(sb.toString());
                    sb.setLength(0); // 重置StringBuilder
                } else {
                    // 否则添加空格，保持SQL语句的完整性
                    sb.append(" ");
                }
            }
        }
        
        return statements;
    }
}