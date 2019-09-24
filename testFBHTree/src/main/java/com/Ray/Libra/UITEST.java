package com.Ray.Libra;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.Ray.Libra.LedgerState.LedgerState;

/**
 * UITEST
 */
public class UITEST {
    /**
     * ============== <br>
     * ==以下為測試區== <br>
     * ============== <br>
     */
    public static String showText = "";

    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        String slectFun;
        String now_tree = "imt";
        /**
         * 初始化
         */
        LedgerState ls = new LedgerState(now_tree);

        /**
         * UI
         */
        // ===========================================
        showText += "\n======Libra UI======\n";
        showText += "=====帳戶相關(a)=====\n";
        showText += "a c <num>\t| 創造 <num> 個虛擬帳戶。\n";
        showText += "a la\t| 列出所有的帳戶。\n";
        showText += "a rm\t| 刪除所有的帳戶。\n";
        showText += "\n";
        showText += "=====查詢相關(q)=====\n";
        showText += "q as <address>\t| 查詢 <address> 帳戶的內容。\n";
        showText += "\n";
        showText += "=====轉帳相關(t)=====\n";
        showText += "t m <address> <balance>\t| 加入 <balance> 個 Libra 到 <address>\n";
        showText += "t p <address1> <address2> <balance>\t| <address1> 轉 <balance> 個 Libra 給 <address2>\n";
        showText += "t test [address1] [address2] <times>\t| 將 [address1](可選) 和 [address2](可選) 或 隨機兩個帳戶互相做 <times> 次交易\n";
        showText += "\n";
        showText += "=====稽核相關(p)=====\n";
        showText += "p t <account>\t| 暫時測試用\n";
        showText += "p t al\t| 暫時測試用\n";
        showText += "p rh\t| 輸出目前 LedgerState 的 rootHash\n";
        if (now_tree.equals("smt")) {
            showText += "p d\t| 列出SMT最大樹深與平均樹深\n";
        }
        showText += "\n";
        showText += " 輸入 q! 結束\n";
        showText += "====================\n";
        // ===========================================

        System.out.println(showText);
        while (isRunning) {
            System.out.print("Libra% ");
            slectFun = scanner.nextLine();
            String[] tokens = slectFun.split(" ");

            // 測試用，確認輸入正確
            // System.out.println(tokens.length);
            // for (String s : tokens) {
            // System.out.println(s);
            // }

            switch (tokens[0]) {
            case "a": // 帳戶相關
                switch (tokens[1]) {
                case "c": // 創建帳戶
                    long time = System.nanoTime();
                    int num = 1; // 未輸入創建數量，預設為1
                    if (tokens.length == 3) { // 第三個參數有被輸入
                        num = Integer.parseInt(tokens[2]);
                    }

                    ls.CreatePseudoAccount(num);

                    // Get the Java runtime
                    Runtime runtime = Runtime.getRuntime();
                    // Run the garbage collector
                    runtime.gc();
                    // Calculate the used memory
                    long memory = runtime.totalMemory() - runtime.freeMemory();
                    System.out.println("Used memory is bytes: " + memory +" Byte");
                    System.out.println("Used memory is megabytes: " + bytesToMegabytes(memory)+" MB");

                    time = System.nanoTime() - time;
                    System.out.println("總執行時間： " + (double) time / 1000000 + " ms");
                    break;
                case "la": // 列出所有帳戶
                    boolean isShowlist = tokens.length == 3 ? true : false;
                    ls.ShowAccountList(isShowlist);
                    break;
                case "rm":
                    ls.rmAllAccount();
                    break;
                }
                break;
            case "q": // 查詢相關
                switch (tokens[1]) {
                case "as": // account_state 秀出帳戶內容
                    String account = tokens[2];
                    System.out.println(ls.ShowAccountContent(account));
                    break;
                }
                break;
            case "t": // 轉帳相關
                switch (tokens[1]) {
                case "m": // 為帳號注入Libra
                    String account = tokens[2];
                    int blance = Integer.parseInt(tokens[3]);
                    ls.MintLibra(account, blance);
                    break;
                case "p": // 進行 P2P 交易
                    String senderAccount = tokens[2];
                    String receiverAccount = tokens[3];
                    int balance = Integer.parseInt(tokens[4]);
                    ls.P2PTransaction(senderAccount, receiverAccount, balance);
                    break;
                case "test": // 進行 P2P 交易 time 次
                    String Account1, Account2;
                    int time;
                    long t = System.nanoTime();
                    if (tokens.length == 5) { // 指定兩帳號
                        Account1 = tokens[2];
                        Account2 = tokens[3];
                        time = Integer.valueOf(tokens[4]) / 2;
                        ls.MintLibra(Account1, 1000);
                        for (int i = 0; i < time; i++) {
                            ls.P2PTransaction(Account1, Account2, 10);
                            ls.P2PTransaction(Account2, Account1, 10);
                        }
                    } else {// 隨機兩帳號
                        List<String> keys = ls.keys;

                        time = Integer.valueOf(tokens[2]) / 2;
                        System.out.print("round:");
                        for (int i = 0; i < time; i++) {
                            System.out.print(i);
                            int s = (int) System.currentTimeMillis();
                            int k = s % keys.size();
                            Account1 = keys.get(k);
                            Account2 = keys.get((k + 1) % keys.size());
                            ls.MintLibra(Account1, 1000);
                            ls.P2PTransaction(Account1, Account2, 10);
                            ls.P2PTransaction(Account2, Account1, 10);

                            for (int j = 0; j < String.valueOf(i).length(); j++) {
                                System.out.print("\b");
                            }
                        }
                    }

                    t = System.nanoTime() - t;
                    System.out.println("run time:" + (double) t / 1000000 + "ms");

                    break;
                }
                break;
            case "p": // 稽核相關
                switch (tokens[1]) {
                case "t":
                    if (tokens[2].equals("al")) { // 稽核所有檔案
                        List<String> keys = new ArrayList<>();
                        keys = ls.getAccountList();

                        long t = System.nanoTime();
                        for (String s : keys) {
                            // System.out.println(s);
                            ls.isNewOnTree(s);
                        }
                        t = System.nanoTime() - t;
                        System.out.println("run time:" + (double) t / 1000000 + "ms");

                    } else { // 稽核特定 account
                        String account = tokens[2];
                        System.out.println(ls.isNewOnTree(account) ? "true" : "false");

                    }

                    break;
                case "rh": // 秀出目前的 rootHash
                    System.out.println("roothash: " + ls.getRootHashStr(now_tree));
                    break;
                case "d": // SMT樹深
                    ls.SMT_findDepth();
                    break;
                }
                break;
            case "q!": // 結束迴圈
                isRunning = false;
                break;
            default:
                errorMsg();
                break;
            }
        }
    }

    public static void errorMsg() {
        System.out.println("不合法字元!!");
        System.out.println(showText);
    }

}