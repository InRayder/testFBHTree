package com.Ray;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;

import com.Ray.Libra.SqlDB;
import com.Ray.Libra.LedgerState.AccountResource;


import org.junit.Test;

/**
 * SQLTest
 */
public class SQLTest {

    /**
     * 0. 測試前後需確認測試帳號是否存在，若存在則刪除 <br>
     * 1. countToSql: 連線到SQL <br>
     * 2. 可以新增一筆(必須先刪除該帳號) <br>
     * 3. 可以新增多筆(必須先刪除該帳號) <br>
     * 4. 可以查詢單筆(必須先新增該帳號) <br>
     * 5. 可以查詢多筆(必須先新增該帳號) <br>
     * 6. 可以修改(必須先新增該帳號)
     */

    private final boolean expected = true;
    SqlDB sd = new SqlDB(true);
    String account = "9f234476359091d616aa686452b1206e28a35b3d3967ea2b6daac241c9a5c044";
    String[] accounts = { "4474aa4d1d9428df98d385f92dcb6e09ca41b65a6f491fcc041a211cbe32a2f6",
            "b383b6b5ca621880e1a779a27555eea92b3acf3a1a9425f602dea813621bb57a",
            "6a6fbe0b7ad1a8dc90022a44450f5085fe605ab7e883b5baeb8265acd65411b5" };

    /**
     * 0. 確認測試帳號是否存在，若存在則刪除
     */
    public void checkAndDledeAccount() {
        System.out.println("0 checkAndDledeAccount...");
        if (checkAccount()) { // 若存在則刪除
            deleteAccount();
        }
    }

    /**
     * 0. 確認測試帳號是否存在，若不存在則建立
     */
    public void checkAndInsertAccount() {
        System.out.println("0 checkAndInsertAccount...");
        if (!checkAccount()) { // 若不存在則新增
            canInsertOne();
            canInsertMore();
        }

    }

    /**
     * 確認測試帳號是否存在 <br>
     * 若存在回傳 true <br>
     * 反之則回傳 fasle <br>
     * 
     * @return
     */
    public boolean checkAccount() {
        System.out.println("0-1 checkAccount...");
        boolean isExistence = true;
        isExistence &= (sd.queryAccount(account).get(account) != null);

        LinkedHashMap<String, AccountResource> ars = new LinkedHashMap<>();
        ars = sd.queryAccount(accounts);
        for (String s : accounts) {
            isExistence &= (ars.get(s) != null);
        }
        return isExistence;
    }

    /**
     * 0. 刪除測試帳號
     */
    public void deleteAccount() {
        System.out.println("0-2 deleteAccount...");
        boolean actual = true;
        actual &= sd.deleteAccount(account);
        for (String s : accounts) {
            actual &= sd.deleteAccount(s);
        }
        assertEquals(expected, actual);
    }

    /**
     * 1. countToSql: 連線到SQL
     */
    @Test
    public void connectionToSql() {
        System.out.println("1 connectionToSql...");
        boolean actual = true;
        actual = sd.sqlConnectionTest();
        assertEquals(expected, actual);
    }

    /**
     * 2. 可以新增一筆(必須先刪除該帳號)
     */
    @Test
    public void canInsertOne() {
        System.out.println("2 canInsertOne...");
        // 前置：須先確認測試帳戶不存在
        checkAndDledeAccount();

        boolean actual = true;
        if (sd.insertAccount(account)) {
            // 新增指令成功
            actual &= true;
            // 確認是否真的有上傳到SQL
            if (sd.queryAccount(account) != null) {
                // 有查詢到資料
                actual &= true;
            } else {
                // 查詢結果為空
                actual &= false;
            }
        } else {
            // 新增指令失敗
            actual &= false;
        }

        assertEquals(expected, actual);
    }

    /**
     * 3. 可以新增多筆(必須先刪除該帳號) <br>
     */
    @Test
    public void canInsertMore() {
        System.out.println("3 canInsertMore");
        // 前置：須先確認測試帳戶不存在
        checkAndDledeAccount();

        boolean actual = true;
        for (String s : accounts) {
            if (sd.insertAccount(s)) {
                // 新增指令成功
                actual &= true;
                // 確認是否真的有上傳到SQLｃ
                if (sd.queryAccount(account) != null) {
                    // 有查詢到資料
                    actual &= true;
                } else {
                    // 查詢結果為空
                    actual &= false;
                }
                assertEquals("是否有確實insert: ", expected, actual);
            } else {
                // 新增指令失敗
                actual &= false;
                assertEquals("是否有完成insert指令: ", expected, actual);
            }
        }
        assertEquals(expected, actual);
    }

    /**
     * 4. 可以查詢單筆(必須先新增該帳號) <br>
     */
    @Test
    public void canQueryOne() {
        System.out.println("4 canQueryOne...");
        // 前置：須先確認測試帳戶已被建立
        checkAndInsertAccount();

        boolean actual = true;
        if (sd.queryAccount(account) != null) {
            // 有查詢到資料
            actual &= true;
        } else {
            // 查詢結果為空
            actual &= false;
        }
        assertEquals(expected, actual);
    }

    /**
     * 5. 可以查詢多筆(必須先新增該帳號) <br>
     */
    @Test
    public void canQueryMore() {
        System.out.println("5 canQueryMore...");
        // 前置：須先確認測試帳戶已被建立
        checkAndInsertAccount();

        boolean actual = true;
        LinkedHashMap<String, AccountResource> ars = new LinkedHashMap<>();
        AccountResource ar;
        ars = sd.queryAccount(accounts);
        for (String s : accounts) {
            if ((ar = ars.get(s)) != null) {
                // 有查詢到資料
                actual &= true;
            } else {
                // 查詢結果為空
                actual &= false;
            }
        }
        assertEquals(expected, actual);
    }

    /**
     * 6. 可以修改(必須先新增該帳號)
     */
    @Test
    public void canUpdateData() {
        System.out.println("6 canUpdateData...");
        // 前置：須先確認測試帳戶已被建立
        checkAndInsertAccount();

        boolean actual = true;
        LinkedHashMap<String, AccountResource> ars = new LinkedHashMap<>();
        AccountResource ar;
        // 查詢並記錄目前 account 狀態(SQL中)
        ars = sd.queryAccount(account);
        ar = ars.get(account);
        int old_balance = ar.getBalance();// 記錄原本的餘額
        // 更改本地端 account 資料
        int balance = 1000;// 預計新增的balance
        ar.doMintLibra(balance);
        // 更新指令(更改SQL上的值)
        if (sd.updateAccount(ar)) {
            // 成功下達更新指令
            actual &= true;
        } else {
            // 指令下達後失敗
            actual &= false;
        }
        assertEquals("SQL更新指令，", expected, actual);
        // 查詢並記錄目前 acount 狀態(SQL中)
        ars = sd.queryAccount(account);
        ar = ars.get(account);

        // 比對 account 狀態，確認有更改成功
        if ((ar.getBalance() - old_balance) == balance) {
            // 更新金額正確
            actual &= true;
        } else {
            // 更新金額錯誤
            actual &= false;
        }
        assertEquals("account 變更金額", balance, ar.getBalance() - old_balance);

        assertEquals("整體結果", expected, actual);
    }

}