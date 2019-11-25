package com.Ray.Libra.LedgerState;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.Ray.Utils.HashUtils;

/**
 * @author InRay
 */
public class AccountResource implements Serializable {

    private final String authentication_key;
    private int balance;
    private int sequence_number;
    private int sent_events_counts;
    private int received_events_count;

    /**
     * 初始化創建
     * 
     * @param authentication_key
     */
    public AccountResource(String authentication_key) {
        this.authentication_key = authentication_key;
        this.balance = 10000000; // 測試用 預設存10000000
        this.sequence_number = 0;
        this.sent_events_counts = 0;
        this.received_events_count = 0;
    }

    /**
     * 匯入
     * 
     * @param balance
     * @param sequence_number
     * @param authentication_key
     * @param sent_events_counts
     * @param received_events_count
     */
    public AccountResource(int balance, int sequence_number, String authentication_key, int sent_events_counts, int received_events_count) {
        this.authentication_key = authentication_key;
        this.balance = balance;
        this.sequence_number = sequence_number;
        this.sent_events_counts = sent_events_counts;
        this.received_events_count = received_events_count;
    }

    /**
     * 秀出完整資訊，包含 rowData，和明文
     * @return
     */
    public String getAccountStateBlob() {
        String blob = "AccountStateBlob { \n" + "\tRaw: " + this.getRowData() + "\n" + "\tDecoded: "
                + this.getDecodedData() + "\n" + "}";
        // System.out.println("\tRaw: " + this.getRowData());
        // System.out.println("\tDecoded: " + this.getDecodedData());
        return blob;
    }

    /**
     * 這邊目前是顯示的內容
     * @return String
     */
    public String getRowData() {
        String row = "" + this.authentication_key + this.balance + this.sequence_number + this.sent_events_counts
                + this.received_events_count;
        try {
            return HashUtils.sha256(row);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDecodedData() {
        String DecodedData = "AccountResource { \n" + "\t\tbalance: " + getBalance() + " ,\n" + "\t\tsequence_number: "
                + getSequence_number() + " ,\n" + "\t\tauthentication_key: 0x" + getAuthentication_key() + " ,\n"
                + "\t\tsent_events_counts: " + getSent_events_counts() + " ,\n" + "\t\treceived_events_count: "
                + getReceived_events_count() + " ,\n" + "\t}";
        return DecodedData;
    }

    /**
     * 為該帳戶注入 Libra
     * 此為測試用，不增加 SN
     * 但會改變 REC 和 Blance
     * 所以會更新 LedgerStateTree
     * 
     * @param balance
     */
    public void doMintLibra(int balance) {
        this.balance += balance;
        this.received_events_count++;
    }

    /**
     * 進行1對1轉帳
     * 
     * @param balance   轉帳金額
     * @param isSender  是否為轉出者
     * @param isSuccess 交易是否執行成功
     */
    public void doAP2PTransaction(int balance, boolean isSender, boolean isSuccess) {
        if (isSender) { // (sender)把錢轉出去
            this.sequence_number++; // 不管交易成功與否，SN都必須加1
            if (isSuccess) { // 扣除餘額以及增加SEC
                this.balance = this.balance - balance;
                this.sent_events_counts++;
            }
        } else { // (received)把錢轉進來
            if (isSuccess) { // 增加餘額以及增加REC
                this.balance = this.balance + balance;
                this.received_events_count++;
            }
            // 交易不成功的狀態下，接收者不會有任何的改變
        }
    }

    public String getAuthentication_key() {
        return this.authentication_key;
    }

    public int getBalance() {
        return this.balance;
    }

    public int getSequence_number() {
        return this.sequence_number;
    }

    public int getSent_events_counts() {
        return this.sent_events_counts;
    }

    public int getReceived_events_count() {
        return this.received_events_count;
    }

    /**
     * 測試用
     * 
     * @param args
     */
    public static void main(String[] args) {
        String authentication_key = "0x77498";
        AccountResource ar = new AccountResource(authentication_key);
        System.out.println(ar.getAccountStateBlob());

    }
}