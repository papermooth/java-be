import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExecuteSqlScript {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://192.168.13.247:3306/";
        String username = "root";
        String password = "123456";
        String sqlScriptPath = "target/classes/init.sql";
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // 加载驱动
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("MySQL驱动加载成功");
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    System.out.println("MySQL旧版驱动加载成功");
                } catch (ClassNotFoundException ex) {
                    System.out.println("无法加载MySQL驱动，尝试自动加载...");
                }
            }
            
            // 先连接到MySQL服务器（不指定数据库）
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("成功连接到MySQL服务器");
            
            stmt = conn.createStatement();
            
            // 读取SQL脚本文件
            List<String> sqlStatements = readSqlFile(sqlScriptPath);
            
            // 执行每个SQL语句
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    System.out.println("执行SQL: " + sql);
                    stmt.execute(sql);
                }
            }
            
            System.out.println("SQL脚本执行完成，数据库初始化成功！");
            
        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("读取SQL文件失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static List<String> readSqlFile(String filePath) throws IOException {
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过注释行
                if (line.trim().startsWith("--")) {
                    continue;
                }
                
                // 添加到当前语句
                currentStatement.append(line).append(" ");
                
                // 如果遇到分号，说明是一个完整的SQL语句
                if (line.trim().endsWith(";")) {
                    sqlStatements.add(currentStatement.toString());
                    currentStatement.setLength(0);
                }
            }
        }
        
        return sqlStatements;
    }
}