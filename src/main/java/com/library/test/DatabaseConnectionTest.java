package com.library.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        // Database connection parameters
        String url = "jdbc:mysql://192.168.13.247:3306/library_db?useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "123456";
        
        Connection connection = null;
        
        try {
            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL driver loaded successfully");
            
            // Establish connection
            System.out.println("Trying to connect to database: " + url);
            connection = DriverManager.getConnection(url, username, password);
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection successful!");
            } else {
                System.out.println("Database connection failed!");
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver loading failed: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Database connection closed");
                } catch (SQLException e) {
                    System.out.println("Error closing database connection: " + e.getMessage());
                }
            }
        }
    }
}