package com.Ray;

import static org.junit.Assert.*;

import com.Ray.Libra.LedgerState.AccountResource;

import org.junit.Test;

/**
 * AccountResourceTest
 */
public class AccountResourceTest {

    /**
     * 確認可以初始化 <br>
     * - 新增 <br>
     * - 匯入 <br>
     */
    @Test
    public void canInit() {
        // 預創建帳戶內容
        String account = "9d551151d715267077b3a4a41ac5652b5887a82bd8e5ebdd085386a093cd1a89";
        int balance = 100;
        int sequence_number = 3;
        int sent_events_counts = 2;
        int received_events_count = 1;

        String actualsAccount;
        int actualsBalance, actualsSequenceNumber, actualsSentEventsCounts, actualsReceivedEventsCount;

        // 新增
        AccountResource ar_create = new AccountResource(account);
        // 判斷 account 是否一致
        actualsAccount = ar_create.getAuthentication_key();
        assertEquals("ar_create::account:", account, actualsAccount);
        // 判斷 balance 是否初始化
        actualsBalance = ar_create.getBalance();
        assertEquals("ar_create::balance:", 0, actualsBalance);
        // 判斷 sequence_number 是否一致
        actualsSequenceNumber = ar_create.getSequence_number();
        assertEquals("ar_create::sequence_number:", 0, actualsSequenceNumber);
        // 判斷 sent_events_counts 是否一致
        actualsSentEventsCounts = ar_create.getSent_events_counts();
        assertEquals("ar_create::sent_events_counts:", 0, actualsSentEventsCounts);
        // 判斷 received_events_count 是否一致
        actualsReceivedEventsCount = ar_create.getReceived_events_count();
        assertEquals("ar_create::received_events_count:", 0, actualsReceivedEventsCount);

        // 匯入
        AccountResource ar_import = new AccountResource(balance, sequence_number, account, sent_events_counts,
                received_events_count);
        // 判斷 account 是否一致
        actualsAccount = ar_import.getAuthentication_key();
        assertEquals("ar_import::account:", account, actualsAccount);
        // 判斷 balance 是否一致
        actualsBalance = ar_import.getBalance();
        assertEquals("ar_import::balance:", balance, actualsBalance);
        // 判斷 sequence_number 是否一致
        actualsSequenceNumber = ar_import.getSequence_number();
        assertEquals("ar_import::sequence_number:", sequence_number, actualsSequenceNumber);
        // 判斷 sent_events_counts 是否一致
        actualsSentEventsCounts = ar_import.getSent_events_counts();
        assertEquals("ar_import::sent_events_counts:", sent_events_counts, actualsSentEventsCounts);
        // 判斷 received_events_count 是否一致
        actualsReceivedEventsCount = ar_import.getReceived_events_count();
        assertEquals("ar_import::received_events_count:", received_events_count, actualsReceivedEventsCount);

    }

    /**
     * 確認可以進行MintLibra
     */
    @Test
    public void canDoMintLibra() {
        String account = "9d551151d715267077b3a4a41ac5652b5887a82bd8e5ebdd085386a093cd1a89";
        int balance = 1000;
        AccountResource ar = new AccountResource(account);
        // 記錄原本的 balacne 和 received_events_count
        int old_balance = ar.getBalance();
        int old_received_events_count = ar.getReceived_events_count();
        // 進行 MintLibra
        ar.doMintLibra(balance);
        // 確認是否正確
        assertEquals("Balance:", balance, ar.getBalance() - old_balance);
        assertEquals("received_events_count:", 1, ar.getReceived_events_count() - old_received_events_count);
    }

    /**
     * 確認P2P轉帳是否正確，可能有以下四種結果[isSender,isSuccess] <br>
     * Sender <br>
     * ├── 1. [T, T] Sender (success) <br>
     * └── 2. [T, F] Sender (fail) <br>
     * Receiver <br>
     * ├── 3. [F, T] Receiver (success) <br>
     * └── 4. [F, F] Receiver (fail) <br>
     */
    @Test
    public void canDoAP2PTransaction() {
        String account_sender = "9d551151d715267077b3a4a41ac5652b5887a82bd8e5ebdd085386a093cd1a89";
        String account_receiver = "b383b6b5ca621880e1a779a27555eea92b3acf3a1a9425f602dea813621bb57a";
        int balance = 100;
        AccountResource ar_s = new AccountResource(account_sender);
        AccountResource ar_r = new AccountResource(account_receiver);

        // 預先為 Sender 存入 1000
        ar_s.doMintLibra(1000);

        // 記錄 Sender 原本的狀態
        int old_s_balance = ar_s.getBalance();
        int old_s_sequence_number = ar_s.getSequence_number();
        int old_s_sent_events_counts = ar_s.getSent_events_counts();
        int old_s_received_events_count = ar_s.getReceived_events_count();

        // 記錄 receiver 原本的狀態
        int old_r_balance = ar_r.getBalance();
        int old_r_sequence_number = ar_r.getSequence_number();
        int old_r_sent_events_counts = ar_r.getSent_events_counts();
        int old_r_received_events_count = ar_r.getReceived_events_count();

        // ==1. [T, T] Sender (success)==
        // 進行轉帳，假設Sender餘額充足
        ar_s.doAP2PTransaction(balance, true, true);
        // 確認 Sender 轉帳結果是否正確
        assertEquals("1. [T, T] Sender (success)::balance: ", -(balance), ar_s.getBalance() - old_s_balance);
        assertEquals("1. [T, T] Sender (success)::sequence_number: ", 1,
                ar_s.getSequence_number() - old_s_sequence_number);
        assertEquals("1. [T, T] Sender (success)::sent_events_counts: ", 1,
                ar_s.getSent_events_counts() - old_s_sent_events_counts);
        assertEquals("1. [T, T] Sender (success)::received_events_count: ", 0,
                ar_s.getReceived_events_count() - old_s_received_events_count);

        // 記錄 Sender 現在的狀態
        old_s_balance = ar_s.getBalance();
        old_s_sequence_number = ar_s.getSequence_number();
        old_s_sent_events_counts = ar_s.getSent_events_counts();
        old_s_received_events_count = ar_s.getReceived_events_count();

        // ==2. [T, F] Sender (fail)==
        // 進行轉帳，假設Sender餘額不足
        ar_s.doAP2PTransaction(balance, true, false);
        // 確認 Sender 轉帳結果是否正確
        assertEquals("2. [T, F] Sender (fail)::balance: ", 0, ar_s.getBalance() - old_s_balance);
        assertEquals("2. [T, F] Sender (fail)::sequence_number: ", 1,
                ar_s.getSequence_number() - old_s_sequence_number);
        assertEquals("2. [T, F] Sender (fail)::sent_events_counts: ", 0,
                ar_s.getSent_events_counts() - old_s_sent_events_counts);
        assertEquals("2. [T, F] Sender (fail)::received_events_count: ", 0,
                ar_s.getReceived_events_count() - old_s_received_events_count);

        // ==3. [F, T] Receiver (success)==
        // 進行轉帳，假設Sender餘額充足
        ar_r.doAP2PTransaction(balance, false, true);
        // 確認 Receiver 轉帳結果是否正確
        assertEquals("3. [F, T] Receiver (success)::balance: ", balance, ar_r.getBalance() - old_r_balance);
        assertEquals("3. [F, T] Receiver (success)::sequence_number: ", 0,
                ar_r.getSequence_number() - old_r_sequence_number);
        assertEquals("3. [F, T] Receiver (success)::sent_events_counts: ", 0,
                ar_r.getSent_events_counts() - old_r_sent_events_counts);
        assertEquals("3. [F, T] Receiver (success)::received_events_count: ", 1,
                ar_r.getReceived_events_count() - old_r_received_events_count);

        // 記錄 receiver 現在的狀態
        old_r_balance = ar_r.getBalance();
        old_r_sequence_number = ar_r.getSequence_number();
        old_r_sent_events_counts = ar_r.getSent_events_counts();
        old_r_received_events_count = ar_r.getReceived_events_count();

        // ==4. [F, F] Receiver (fail)==
        // 進行轉帳，假設Sender餘額充足
        ar_r.doAP2PTransaction(balance, false, false);
        // 確認 Receiver 轉帳結果是否正確
        assertEquals("4. [F, F] Receiver (fail)::balance: ", 0, ar_r.getBalance() - old_r_balance);
        assertEquals("4. [F, F] Receiver (fail)::sequence_number: ", 0,
                ar_r.getSequence_number() - old_r_sequence_number);
        assertEquals("4. [F, F] Receiver (fail)::sent_events_counts: ", 0,
                ar_r.getSent_events_counts() - old_r_sent_events_counts);
        assertEquals("4. [F, F] Receiver (fail)::received_events_count: ", 0,
                ar_r.getReceived_events_count() - old_r_received_events_count);

    }

}