package com.Ray.Libra;

import java.sql.*;
import java.util.LinkedHashMap;

import com.Ray.Libra.LedgerState.AccountResource;
/**
 * SqlDB
 */
public class SqlDB {

    // SQL中的資料庫名稱
    static final String DB_Name = "TestLibraDB";

    // MySQL 8.0 以上版本 - JDBC 驅動名及數據庫 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_Name + "?useSSL=false&serverTimezone=UTC";

    // 數據庫的用戶名與密碼，需要根據自己的設置
    static final String USER = "root";
    static final String PASS = "Root1234";

    Connection conn = null;
    Statement stmt = null;

    // 若不是 DebugMode 則不印出任何字串
    private final boolean isDeBugMode;

    // NormalMode (不顯示任何字串)
    public SqlDB() {
        this.isDeBugMode = false;
    }

    // DebugMode (顯示其中字串以方便除錯)
    public SqlDB(boolean b) {
        this.isDeBugMode = b;
    }

    /**
     * 若為 DeBugMode 則打印文字。
     * 
     * @param s 預打印出的文字
     */
    void println(String s) {
        if (isDeBugMode) {
            System.out.println(s);
        }
    }

    /**
     * 主要 SQL 操作 輸入 SQL 指令與是否為 Query 指令
     * 
     * @param sql     預操作的 SQL 指令
     * @param isQuery 是否為 Query 指令
     * @return 若為Qurey值，則返回其Qurey結果
     */
    private LinkedHashMap<String, AccountResource> sqlOperating(String sql, boolean isQuery) {
        println("\n輸入 SQL: " + sql);
        LinkedHashMap<String, AccountResource> ars = new LinkedHashMap<>();
        AccountResource ar;
        try {
            // 註冊 JDBC 驅動
            Class.forName(JDBC_DRIVER);

            // 連接 SQL
            println("連接資料庫...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 執行SQL指令
            println("實例化Statement對象...");
            stmt = conn.createStatement();

            if (isQuery) {
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    int balance = rs.getInt("balance");
                    int sequence_number = rs.getInt("sequenceNumber");
                    String authentication_key = rs.getString("authenticationKey");
                    int sent_events_counts = rs.getInt("sentEventsCount");
                    int received_events_count = rs.getInt("receivedEventsCount");
                    ar = new AccountResource(balance, sequence_number, authentication_key, sent_events_counts,
                            received_events_count);
                    ars.put(authentication_key, ar);
                }
                rs.close();
            } else {
                stmt.executeUpdate(sql);
            }

            // 完成後關閉
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 處理 JDBC 錯誤
            String errorMessage = se.getMessage();
            System.out.println("errorMessage:" + errorMessage);
            if (errorMessage.contains("Duplicate entry")) {// 重複輸入
                String[] token = errorMessage.split(" ");
                println("account: " + token[2] + " 已存在!");
                return null;
            } else {
                se.printStackTrace();
            }
        } catch (Exception e) {
            // 處理 Class.forName 錯誤
            e.printStackTrace();
        } finally {
            // 關閉資源
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            } // do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return ars;
    }

    /**
     * (測試用)清空資料表
     */
    public void rmAllAccount(){
        String sql = "TRUNCATE `TestLibraDB`.`account_blob`";
        sqlOperating(sql, false);
    }
    /**
     * 用來測試是否能連接到SQL
     * @return
     */
    public boolean sqlConnectionTest(){
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
            
            // 完成後關閉
            stmt.close();
            conn.close();
            return true;
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
        return false;
    }

    // 新增單筆 account
    public boolean insertAccount(String account) {
        println("\n\ndo:insertAccount(account)");
        String sql = "INSERT INTO `account_blob` (`authenticationKey`) VALUES ('" + account + "')";
        return (sqlOperating(sql, false) != null);
    }

    // 新增多筆 accounts
    public boolean insertAccount(String[] accounts) {
        if(accounts.length <1){
            return false;
        }
        println("\n\ndo:insertAccount(accounts[])");
        String sql = "INSERT INTO `account_blob` (`authenticationKey`) VALUES ";
        // 假設陣列只有一筆資料
        if (accounts.length == 1) {
            return insertAccount(accounts[0]);
        }
        for (int i = 0; i < accounts.length; i++) {
            if (i == 0) {
                sql += "('" + accounts[i] + "')";
            } else {
                sql += ", ('" + accounts[i] + "')";
            }
        }
        // System.out.println(sql);
        return (sqlOperating(sql, false) != null);
    }

    // 查詢單筆 account
    public LinkedHashMap<String, AccountResource> queryAccount(String account) {
        println("\n\ndo:queryAccount(account)");
        String sql = "SELECT * FROM `account_blob` WHERE `authenticationKey` LIKE '" + account + "'";
        return sqlOperating(sql, true);
    }

    // 查詢多筆 account (查詢slice)
    public LinkedHashMap<String, AccountResource> queryAccount(String[] accounts) {
        println("\n\ndo:queryAccount(accounts[])");
        String sql = "SELECT * FROM `account_blob` WHERE";
        for (int i = 0; i < accounts.length; i++) {
            if (i == 0) {
                sql += " `authenticationKey` LIKE '" + accounts[i] + "'";
            } else {
                sql += " OR `authenticationKey` LIKE '" + accounts[i] + "'";
            }
        }
        return sqlOperating(sql, true);
    }

    // 修改單筆 account
    public boolean updateAccount(AccountResource ar) {
        println("\n\ndo:updateAccount(AccountResource)");
        ar.getBalance();

        int balance = ar.getBalance();
        int sequence_number = ar.getSequence_number();
        String authentication_key = ar.getAuthentication_key();
        int sent_events_counts = ar.getSent_events_counts();
        int received_events_count = ar.getReceived_events_count();
        String sql = "UPDATE `account_blob` SET ";

        sql += "`balance` = '" + balance + "', `sequenceNumber` = '" + sequence_number + "', `sentEventsCount` = '"
                + sent_events_counts + "', `receivedEventsCount` = '" + received_events_count + "' ";
        sql += "WHERE `account_blob`.`authenticationKey` = '" + authentication_key + "'";

        return (sqlOperating(sql, false) != null);
    }

    // (未完成)修改多筆 account
    public boolean updateAccount(LinkedHashMap<String, AccountResource> ars) {
        int updateNum = ars.size();
        String sql = "UPDATE `account_blob` SET ";

        return (sqlOperating(sql, false) != null);
    }

    // (test用)刪除帳號
    public boolean deleteAccount(String account){
        String sql ="DELETE FROM `account_blob` WHERE `account_blob`.`authenticationKey` = \'"+account+"\'";

        return (sqlOperating(sql, false) != null);
    }
    

    /**
     * 測試用
     * 
     * @param args
     */
    public static void main(String[] args) {
        SqlDB sd = new SqlDB(true);
        String account = "9f234476359091d616aa686452b1206e28a35b3d3967ea2b6daac241c9a5c044";
        String[] accounts = { "4474aa4d1d9428df98d385f92dcb6e09ca41b65a6f491fcc041a211cbe32a2f6",
                "b383b6b5ca621880e1a779a27555eea92b3acf3a1a9425f602dea813621bb57a",
                "6a6fbe0b7ad1a8dc90022a44450f5085fe605ab7e883b5baeb8265acd65411b5" };

        LinkedHashMap<String, AccountResource> ars = new LinkedHashMap<>();
        AccountResource ar;

        // // 刪除一筆
        // if(sd.deleteAccount(account)){
        //     sd.println("成功");
        // }else{
        //     sd.println("失敗");
        // }
        

        // // 新增一筆 account
        // if (sd.insertAccount(account)) {
        // sd.println("新增成功!");
        // } else {
        // sd.println("新增失敗!");
        // }

        // // 新增多筆 account-方法一(失敗會終止，但只需下一次指令)
        // if (sd.insertAccount(accounts)) {
        // sd.println("新增成功!");
        // } else {
        // sd.println("新增失敗!");
        // }
        // // 新增多筆 account-方法二(失敗不會終止，需要下多次指令)
        // for (String s : accounts) {
        // if (sd.insertAccount(s)) {
        // sd.println("新增成功!");
        // } else {
        // sd.println("新增失敗!");
        // }
        // }

        // // 查詢單筆 account
        // ars = sd.queryAccount(account);
        // if ((ar = ars.get(account)) != null) {
        // sd.println(ar.getAccountStateBlob());
        // } else {
        // sd.println("account: " + account + " 不存在");
        // }

        // // 查詢多筆 account
        // ars = sd.queryAccount(accounts);
        // for (String s : accounts) {
        // if ((ar = ars.get(s)) != null) {
        // sd.println(ar.getAccountStateBlob());
        // } else {
        // sd.println("account: " + s + "不存在");
        // }
        // }

        // // 修改單筆 account
        // // SQL下載並打印資料
        // ars = sd.queryAccount(account);
        // ar = ars.get(account);
        // sd.println(ar.getAccountStateBlob());
        // // 本地端修改 (注入Libra)
        // ar.doMintLibra(1000);
        // // 回傳修改內內容
        // sd.updateAccount(ar);
        // // SQL下載並打印資料
        // ars = sd.queryAccount(account);
        // ar = ars.get(account);
        // sd.println(ar.getAccountStateBlob());

        sd.println("==============================================");

        /**
         * 修改多筆 account 9f234476359091d616aa686452b1206e28a35b3d3967ea2b6daac241c9a5c044
         * VV 100libra VV
         * b383b6b5ca621880e1a779a27555eea92b3acf3a1a9425f602dea813621bb57a
         */
        // String[] accounts2 = { "9f234476359091d616aa686452b1206e28a35b3d3967ea2b6daac241c9a5c044",
        //         "b383b6b5ca621880e1a779a27555eea92b3acf3a1a9425f602dea813621bb57a"};
        // AccountResource senderAr;
        // AccountResource receiverAr;
        // int balances = 100;
        // // 下載多筆 account
        // ars = sd.queryAccount(accounts2);
        // boolean isFirst= true;
        // // 本地端修改
        // for (String s : accounts2) {
        //     sd.println("匯入 Account: 0x"+s);
        //     ar = ars.get(s);
        //     // 本地端修改 (注入Libra)
        //     ar.doMintLibra(1000);
        //     sd.println(ar.getAccountStateBlob());// 打印內容
        //     int balance = ar.getBalance();
        //     int sequence_number = ar.getSequence_number();
        //     String authentication_key = ar.getAuthentication_key();
        //     int sent_events_counts = ar.getSent_events_counts();
        //     int received_events_count = ar.getReceived_events_count();
        //     if(isFirst){
        //         senderAr = new AccountResource(balance, sequence_number, authentication_key, sent_events_counts, received_events_count);
        //         senderAr.doAP2PTransaction(balances, true, true);
        //         ars.replace(senderAr.getAuthentication_key(), senderAr);
        //         isFirst = false;
        //     }else{
        //         receiverAr = new AccountResource(balance, sequence_number, authentication_key, sent_events_counts, received_events_count);
        //         receiverAr.doAP2PTransaction(balances, false, true);
        //         ars.replace(receiverAr.getAuthentication_key(), receiverAr);
        //     }
            
        // }
        // // 回傳結果
        // for (String s : accounts2) {
        //     ar = ars.get(s);
        //     sd.updateAccount(ar);
        // }
        // // SQL下載並打印資料
        // ars = sd.queryAccount(accounts2);
        // for (String s : accounts2) {
        //     sd.println(ars.get(s).getAccountStateBlob());
        // }

    }

}