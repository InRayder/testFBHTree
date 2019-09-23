package com.Ray;

/**
 * 參考網址：http://www.runoob.com/java/java-mysql-connect.html
 * 
 * DataBase：test
 * table：websites
 */

// 創建測試數據
// CREATE TABLE `websites` (
//   `id` int(11) NOT NULL AUTO_INCREMENT,
//   `name` char(20) NOT NULL DEFAULT '' COMMENT '站點名稱',
//   `url` varchar(255) NOT NULL DEFAULT '',
//   `alexa` int(11) NOT NULL DEFAULT '0' COMMENT 'Alexa 排名',
//   `country` char(10) NOT NULL DEFAULT '' COMMENT '國家',
//   PRIMARY KEY (`id`)
// ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

// 插入數據
// INSERT INTO `websites` VALUES ('1', 'Google', 'https://www.google.cm/', '1', 'USA'), ('2', '淘宝', 'https://www.taobao.com/', '13', 'CN'), ('3', '菜鸟教程', 'http://www.runoob.com', '5892', ''), ('4', '微博', 'http://weibo.com/', '20', 'CN'), ('5', 'Facebook', 'https://www.facebook.com/', '3', 'USA');


import java.sql.*;

public class MySQLDemo {

    static final String DB_Name = "test";
 
    // MySQL 8.0 以下版本 - JDBC 驅動名及數據庫 URL
    // static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    // static final String DB_URL = "jdbc:mysql://localhost:3306/"+DB_Name;

 
    // MySQL 8.0 以上版本 - JDBC 驅動名及數據庫 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/"+DB_Name+"?useSSL=false&serverTimezone=UTC";
 
 
    // 數據庫的用戶名與密碼，需要根據自己的設置
    static final String USER = "root";
    static final String PASS = "Root1234";
 
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try{
            // 註冊 JDBC 驅動
            Class.forName(JDBC_DRIVER);
        
            // 打開鏈接
            System.out.println("連接資料庫...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
            // 執行查詢
            System.out.println("實例化Statement對象...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, name, url FROM websites";
            ResultSet rs = stmt.executeQuery(sql);
        
            // 展開結果集數據庫
            while(rs.next()){
                // 通過字段檢索
                int id  = rs.getInt("id");
                String name = rs.getString("name");
                String url = rs.getString("url");
    
                // 輸出資料
                System.out.print("ID: " + id);
                System.out.print(", 站點名稱: " + name);
                System.out.print(", 站點 URL: " + url);
                System.out.print("\n");
            }
            // 完成後關閉
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            // 處理 JDBC 錯誤
            se.printStackTrace();
        }catch(Exception e){
            // 處理 Class.forName 錯誤
            e.printStackTrace();
        }finally{
            // 關閉資源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// do nothing
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}