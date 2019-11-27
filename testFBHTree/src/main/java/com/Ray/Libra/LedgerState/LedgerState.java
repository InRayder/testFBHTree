package com.Ray.Libra.LedgerState;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.Ray.IMTree.IMTree;
import com.Ray.Libra.SqlDB;
import com.Ray.SMTree.SMTree;
import com.Ray.Utils.HashUtils;
import com.Ray.Utils.ObjectUtils;

/**
 * @author InRay
 */
public class LedgerState {

    private SqlDB DB = new SqlDB();

    private HashMap<String, AccountResource> ars = new HashMap<String, AccountResource>();
    public List<String> keys = new ArrayList<>();
    // private LinkedHashMap<String, AccountResource> ars = null;
    // private List<String> keys = null;

    int tree_h = 17;
    IMTree imt = new IMTree(tree_h);
    SMTree smt = new SMTree();
    boolean isIMT;// true == IMT, false == SMT
    boolean isNoStore = false;
    boolean isNoSQL = true;

    public LedgerState(String tree) {
        System.out.println("該次的樹為 " + tree);
        if (tree.equals("imt")) {
            System.out.println("樹高為:" + tree_h);
        }
        System.out.println("建立成功...");
        // 確認檔案存在
        File file = new File("LedgerState_key.dat");
        if (file.exists()) {
            ObjectUtils ou_key = new ObjectUtils("LedgerState_key");
            keys = (List<String>) ou_key.readObjectFromFile();
            if (tree.equals("imt")) { // 執行 IMT
                ObjectUtils ou_imt = new ObjectUtils("LedgerState_imt");
                imt = (IMTree) ou_imt.readObjectFromFile();
            } else {// 執行 SMT
                ObjectUtils ou_smt = new ObjectUtils("LedgerState_smt");
                smt = (SMTree) ou_smt.readObjectFromFile();
                System.out.println("smt size:" + smt.nodes.size());
            }
        }
        if (tree.equals("imt")) {
            isIMT = true;
        } else {
            isIMT = false;
        }
    }

    public LedgerState() {
        System.out.println("建立成功...");
        // 確認檔案存在
        File file = new File("LedgerState_key.dat");
        if (file.exists()) {
            // 讀取物件
            ObjectUtils ou_key = new ObjectUtils("LedgerState_key");
            ObjectUtils ou_imt = new ObjectUtils("LedgerState_imt");
            ObjectUtils ou_smt = new ObjectUtils("LedgerState_smt");
            keys = (List<String>) ou_key.readObjectFromFile();
            imt = (IMTree) ou_imt.readObjectFromFile();
            smt = (SMTree) ou_smt.readObjectFromFile();
            System.out.println("size:" + smt.nodes.size());
        }

    }

    /**
     * ========= <br>
     * ==測試用== <br>
     * ========= <br>
     * 
     * 建立一個(多個)虛擬的帳戶
     * <p>
     * 創造數筆假資料用來測試 創造1_000_000(100萬)約需 2~4 秒 <br>
     * 創造1_000_000_000(10億)會耗盡所有記憶體 <br>
     * 
     * @param max 輸入要創建的虛擬帳戶數量
     * @return
     */
    public void CreatePseudoAccount(int max) {
        String account;

        try {
            System.out.println("創建測試用帳戶中...");

            int n = 1000;
            int round = 0;
            int time = max;
            int new_n = 0;// 本 round/time 新增的 account
            if (max > n) { // 一次最多執行 n 筆
                round = max / n;// 執行輪數
                time = max % n; // 餘數
            }
            System.out.println("round:" + round);
            System.out.println("time:" + time);

            long t = System.nanoTime();// 計算時間
            System.out.print("round:");
            for (int u = 0; u < round; u++) {// 執行輪數部分
                System.out.print(u);
                Stack<String> s_round_accounts = new Stack<>();
                new_n = 0;
                for (int i = 0; i < n; i++) {
                    int seed = keys.size() - 1;
                    if (seed == -1) {
                        account = HashUtils.sha256(String.valueOf(System.nanoTime()));
                    } else {
                        account = HashUtils.sha256(keys.get(seed));
                    }

                    // account = HashUtils.sha256(String.valueOf(System.nanoTime()));

                    AccountResource ar;
                    if (ars.get(account) == null) { // 該account尚未被建立
                        s_round_accounts.push(account);
                        new_n++;
                        ar = new AccountResource(account);
                        ars.put(account, ar); // 儲存文本
                        keys.add(account); // 儲存 account

                        // 更新LedgerState
                        updateIMTree(account, ar); // 存入 IMT
                        updateSMTree(account, ar); // 存入 SMT
                    } else { // 該 account 已存在
                        System.out.println("該 account 已經被建立!");
                    }
                }
                String[] round_accounts = new String[new_n];
                for (int i = 0; i < new_n; i++) {
                    round_accounts[i] = s_round_accounts.pop();
                }
                // 將 account 建立至 SQL
                insertAccountToSql(round_accounts);
                String x = String.valueOf(u);
                for (int i = 0; i < x.length(); i++) {
                    System.out.print("\b");
                }

            }
            System.out.println();
            Stack<String> s_time_accounts = new Stack<>();
            new_n = 0;
            for (int i = 0; i < time; i++) {// 執行餘數部分
                account = HashUtils.sha256(new Random((int) System.nanoTime()).toString());
                // account = HashUtils.sha256(String.valueOf(System.nanoTime()));

                AccountResource ar;
                if (ars.get(account) == null) { // 該account尚未被建立
                    s_time_accounts.push(account);
                    new_n++;
                    ar = new AccountResource(account);
                    ars.put(account, ar); // 儲存文本
                    keys.add(account); // 儲存 account

                    // 更新LedgerState
                    updateIMTree(account, ar); // 存入 IMT
                    updateSMTree(account, ar); // 存入 SMT
                } else { // 該 account 已存在
                    System.out.println("該 account 已經被建立!");
                }
            }
            String[] time_accounts = new String[new_n];
            for (int i = 0; i < new_n; i++) {
                time_accounts[i] = s_time_accounts.pop();
            }

            System.out.println("執行時間(不含儲存): " + (double) (System.nanoTime() - t) / 1000000 + " ms");

            // 將 account 建立至 SQL
            insertAccountToSql(time_accounts);

            long time_Obj = System.nanoTime();
            // 存回Object
            updateKeysToObject(keys);
            updateIMTToObject(imt);
            updateSMTToObject(smt);

            System.out.println("執行時間(物件儲存): " + (double) (System.nanoTime() - time_Obj) / 1000000 + " ms");
            System.out.println("創建完成!");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * (測試用)清空所有帳戶
     */
    public void rmAllAccount() {
        if (isNoStore) {
            keys = new ArrayList<>();
        } else {
            keys = new ArrayList<>();
            imt = new IMTree(21);
            smt = new SMTree();
            updateIMTToObject(imt);
            updateSMTToObject(smt);
            updateKeysToObject(keys);
            if(!isNoSQL){
                DB.rmAllAccount();
            }            
        }
    }

    // SMT 測試用
    public void SMT_findDepth() {
        smt.findDepth();
    }

    public String getRootHashStr(String tree) {
        if (tree.equals("imt")) {
            return HashUtils.byte2hex(imt.getRootHash());
        } else {// smt
            return HashUtils.byte2hex(smt.getRootHash());
        }

    }

    /**
     * 有三種可能:
     * <ol>
     * <li>mem存在，storage存在</li>
     * <li>mem不存在，storage存在</li>
     * <li>mem不存在，storage不存在</li>
     * </ol>
     * <br>
     * 若發生以下情形則表示錯誤:
     * <ol>
     * <li>mem存在，storage不存在</li>
     * </ol>
     */
    private int accountResourceStates(String account) {
        if (ars.get(account) != null) {// mem存在
            if (keys.indexOf(account) != -1) {// storage存在(直接讀出)
                return 1;
            } else {// storage不存在(錯誤情況)
                return 4;
            }
        } else {// mem不存在
            if (keys.indexOf(account) != -1) {// storage存在(從SQL匯入)
                HashMap<String, AccountResource> tmp_ars = DB.queryAccount(account);
                ars.put(account, tmp_ars.get(account));
                return 2;
            } else {// storage不存在(此帳號尚未被建立)
                return 3;
            }
        }
    }

    /**
     * 更新 IMTree，更新完成後回傳 rootHash
     * 
     * @param account
     * @param ar
     * @return
     */
    private byte[] updateIMTree(String account, AccountResource ar) {
        if (isIMT) {
            // 將帳本內容取Hash後的值(String) 轉成 byte
            byte[] digestValue = HashUtils.hex2byte(ar.getRowData());
            imt.put(account, digestValue);
            return imt.getRootHash();
        } else {
            return null;
        }
    }

    /**
     * 將 imt 存入物件中
     */
    private void updateIMTToObject(IMTree imt) {
        if (isNoStore) {
            // doNothing
        } else {
            if (isIMT) {
                // 將樹的內容存起來
                ObjectUtils ou_IMTNode = new ObjectUtils("LedgerState_imt");
                ou_IMTNode.writeObjectToFile(imt);
            }
        }

    }

    /**
     * 更新 SMTree，更新完成後回傳 rootHash
     * 
     * @param account
     * @param ar
     * @return
     */
    private byte[] updateSMTree(String account, AccountResource ar) {
        if (!isIMT) {
            // 將帳本內容取Hash後的值(String) 轉成 byte
            byte[] digestValue = HashUtils.hex2byte(ar.getRowData());
            smt.put(account, digestValue);

            return smt.getRootHash();
        } else {
            return null;
        }
    }

    /**
     * 將 smt 存入物件中
     */
    private void updateSMTToObject(SMTree smt) {
        if (isNoStore) {
            // doNothing
        } else {
            if (!isIMT) {
                // 將樹的內容存起來
                ObjectUtils ou_SMTNode = new ObjectUtils("LedgerState_smt");
                ou_SMTNode.writeObjectToFile(smt);
            }
        }

    }

    /**
     * 輸入 account，返回 account 內容
     * 
     * @param account
     * @return
     */
    public String ShowAccountContent(String account) {
        if (accountResourceStates(account) < 3) { // storage存在
            return ars.get(account).getAccountStateBlob();
        } else {// storage存在
            return "此帳本不存在!";
        }
    }

    /**
     * 秀出目前所有已存在的 account (str)
     */
    public void ShowAccountList(boolean isShowlist) {
        if (keys.isEmpty()) {
            System.out.println("尚未有 account 被建立!");
        }
        int count = 0;
        for (String s : keys) {
            if (isShowlist) {
                System.out.println("Account: " + s);
            }
            count++;
        }
        System.out.println("共有 " + count + " 個Account!");
    }

    public List<String> getAccountList() {
        return this.keys;
    }

    /**
     * 創建至SQL-account_states(單筆)
     * 
     * @param account 輸入account
     * @return
     */
    public boolean insertAccountToSql(String account) {
        if (isNoStore) {
            return true;
        } else {
            if(!isNoSQL){
                return DB.insertAccount(account);
            }
            return true;
        }
    }

    /**
     * 創建至SQL-account_states(多筆_測試用)
     * 
     * @param account 輸入account
     * @return
     */
    public boolean insertAccountToSql(String[] accounts) {
        if (isNoStore) {
            return true;
        } else {
            if(!isNoSQL){
                return DB.insertAccount(accounts);
            }
            return true;
        }
    }

    /**
     * 更改至SQL-account_states(單筆)
     * 
     * @param ar 輸入包裝後的account資料
     * @return
     */
    public boolean updataAccountToSql(AccountResource ar) {
        if (isNoStore) {
            return true;
        } else {
            if(!isNoSQL){
                return DB.updateAccount(ar);
            }
            return true;
        }
    }

    /**
     * 儲存目前已存在的 account 至 Object
     * 
     * @param keys
     */
    public void updateKeysToObject(List<String> keys) {
        if (isNoStore) {
            // doNothing
        } else {
            ObjectUtils ou_key = new ObjectUtils("LedgerState_key");
            ou_key.writeObjectToFile(keys);
        }

    }

    /**
     * ========= <br>
     * ==Audit== <br>
     * ========= <br>
     */

    public boolean sliceAudit(String account) {
        // 目前的 rootHash
        byte[] rootHash = imt.getRootHash();
        // 取的 account 的 slice
        String slice = imt.extractSlice(account);
        System.out.println("slice : " + slice);
        byte[] sliceRootHash = imt.evalRootHashFromSlice(slice);
        System.out.println("sliceRootHash : " + HashUtils.byte2hex(sliceRootHash));

        // 比對 rootHash
        return Arrays.equals(rootHash, sliceRootHash);
    }

    /**
     * [IMT]確認該帳戶的狀態在樹上是儲存最新的
     * 
     * @param account
     * @return
     */
    public boolean isNewOnTree(String account) {
        if (accountResourceStates(account) < 3) { // storage存在
            System.out.println();
        } else {// storage不存在
            return false;
        }

        // 確認該帳戶有被包在樹上
        if (!imt.contains(account)) {
            // System.out.println("沒有被包在樹上");
            return false;
        }

        // 取得帳戶狀態的 Hash
        AccountResource ar = ars.get(account);
        byte[] digestValue = HashUtils.hex2byte(ar.getRowData());

        // 取得存在樹上的 帳戶狀態的 Hash
        byte[] treeDigestValue = imt.get(account);

        // System.out.println("digestValue\t" + HashUtils.byte2hex(digestValue));
        // System.out.println("treeDigestValue\t" +
        // HashUtils.byte2hex(treeDigestValue));
        if (!Arrays.equals(digestValue, treeDigestValue)) {
            // System.out.println("Hash值相同!");
            return false;
        }

        // 確認 Slice
        if (!sliceAudit(account)) {
            // System.out.println("Slice正確");
            return false;
        }

        return true;
    }

    /**
     * =========================== <br>
     * ==以下操作會改變LedgerState== <br>
     * =========================== <br>
     */

    /**
     * 
     * 建立一個帳戶(預設)，並回傳結果
     * 
     * @param account 輸入欲建立的帳戶
     * @return roothash
     */
    public byte[] CreateAccount(String account) {
        AccountResource ar;
        byte[] rootHash;
        if (ars.get(account) == null) { // 該account尚未被建立
            ar = new AccountResource(account);
            ars.put(account, ar); // 儲存文本
            keys.add(account); // 儲存 account
            // 存回Object
            updateKeysToObject(keys);
            // 更新LedgerState
            rootHash = updateIMTree(account, ar); // 存入 IMT
            updateSMTree(account, ar); // 存入 SMT

            return rootHash;
        } else { // 該 account 已存在
            System.out.println("該 account 已經被建立!");
            return imt.getRootHash();
        }
    }

    /**
     * 匯入 account (已知內容)
     * 
     * @param balance
     * @param sequence_number
     * @param authentication_key
     * @param sent_events_counts
     * @param received_events_count
     * @return rootHash
     */
    public byte[] CreateAccount(int balance, int sequence_number, String authentication_key, int sent_events_counts,
            int received_events_count) {
        AccountResource ar;
        byte[] rootHash;
        if (ars.get(authentication_key) == null) { // 該account尚未被建立
            ar = new AccountResource(balance, sequence_number, authentication_key, sent_events_counts,
                    received_events_count);
            ars.put(authentication_key, ar); // 儲存文本
            keys.add(authentication_key); // 儲存 account

            System.out.println("account: " + authentication_key + "，建立成功");
            // 更新LedgerState
            rootHash = updateIMTree(authentication_key, ar); // 存入 IMT
            updateSMTree(authentication_key, ar); // 存入 SMT

            return rootHash;
        } else { // 該 account 已存在
            System.out.println("該 account 已經被建立!");
            return imt.getRootHash();
        }
    }

    /**
     * 為該帳戶注入libra
     * 
     * @param account 預注入 libra 的 account
     * @param libra   預注入 libra 數量
     */
    public void MintLibra(String account, int balance) {
        if (accountResourceStates(account) < 3) {
            AccountResource ar = ars.get(account);
            ar.doMintLibra(balance);

            // 更新 LedgerState
            updateIMTree(account, ar);
            updateSMTree(account, ar); // 存入 SMT

            // 更新至 SQL
            updataAccountToSql(ar);

            // System.out.println("成功!");
            // System.out.println("已經為 0x" + account + " 注入 " + balance + " 個 Libra");
            // System.out.println(ar.getAccountStateBlob());
        } else {
            System.out.println("帳號尚未被建立");
            // 不做任何事情
        }
    }

    /**
     * 進行1對1轉帳，會顯示交易結果
     * 
     * @param senderAccount   發送者的 account
     * @param receiverAccount 接收者的 account
     * @param balance         轉帳金額
     */
    public void P2PTransaction(String senderAccount, String receiverAccount, int balance) {
        String resultStr = ""; // 秀出最後的交易結果
        // 判斷帳戶是否存在
        if (accountResourceStates(senderAccount) < 3 && accountResourceStates(receiverAccount) < 3) {// 存在
            AccountResource senderAr = ars.get(senderAccount);
            AccountResource receiverAr = ars.get(receiverAccount);
            boolean isBalanceEnough = (senderAr.getBalance() >= balance);// 確認轉出者餘額是否足夠

            /**
             * 進行轉帳動作 <br>
             * STEP 1. 驗證帳戶狀態 <br>
             * STEP 2. 更改帳戶狀態 <br>
             * STEP 3. 將帳戶狀態存回SQL <br>
             * STEP 4. 更新LedgerState <br>
             * STEP 5. 顯示結果 <br>
             * 
             */

            // STEP 1. 驗證帳戶狀態
            // 檢查 樹上所儲存的 roothash

            // STEP 2. 更改帳戶狀態
            senderAr.doAP2PTransaction(balance, true, isBalanceEnough);
            receiverAr.doAP2PTransaction(balance, false, isBalanceEnough);

            // STEP 3. 將帳戶狀態存回SQL
            updataAccountToSql(senderAr);
            updataAccountToSql(receiverAr);

            // STEP 4. 更新 LedgerState
            // 存入 IMT
            updateIMTree(senderAccount, senderAr);
            updateIMTree(receiverAccount, receiverAr);
            // 存入 SMT
            updateSMTree(senderAccount, senderAr);
            updateSMTree(receiverAccount, receiverAr);

            // STEP 5. 顯示結果
            resultStr += "執行交易: \n";
            resultStr += "{\n account: 0x" + senderAccount + " 轉出 " + balance + " Libra \n";
            resultStr += " account: 0x" + receiverAccount + " 轉入 " + balance + " Libra \n}";
            resultStr += "交易完成...\n";
            if (isBalanceEnough) {
                resultStr += "交易成功!\n";
            } else {
                resultStr += "交易失敗!\n";
                resultStr += "Sender 的餘額不足，無法完成交易。";
            }
            // System.out.println(resultStr);
        } else {
            System.out.println("帳號尚未被建立");
            // 不做任何事情
        }
    }
}