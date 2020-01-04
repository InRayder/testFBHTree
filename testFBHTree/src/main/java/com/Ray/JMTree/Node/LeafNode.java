package com.Ray.JMTree.Node;

import com.Ray.Libra.LedgerState.AccountResource;
import com.Ray.Utils.HashUtils;

// 葉節點
public class LeafNode implements Node {

    String account_key; // 帳戶地址
    byte[] account_key_byte;
    AccountResource blob; // 帳戶內容

    /**
     * 創建一個新的葉子節點。
     * 
     * @param key
     * @param ar
     */
    public LeafNode(String key, AccountResource ar) {
        this.account_key = key;
        this.account_key_byte = HashUtils.hex2byte(key);
        this.blob = ar;
    }

    public LeafNode(byte[] key, AccountResource ar) {
        this.account_key = HashUtils.byte2hex(key);
        this.account_key_byte = key;
        this.blob = ar;
    } 

    /**
     * 獲取帳戶鑰，即哈希帳戶地址。
     */
    public String getAccountKey() {
        return this.account_key;
    }

    /**
     * 獲取關聯的 blob 的哈希值。
     * 
     * @return
     */
    public String getBlobHash() {
        return blob.getRowData();
    }

    /**
     *  獲取關聯的 blob 本身
     * @return
     */
    public AccountResource getBlob() {
        return this.blob;
    }

    /**
     * 返回 blob 的hash值
     */
    @Override
    public byte[] getHash() {
        // TODO Auto-generated method stub        
        return HashUtils.hex2byte(blob.getRowData());
    }

    @Override
    public void encode() {
        // TODO Auto-generated method stub

    }

    @Override
    public void decode() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean is_leaf() {
        // TODO Auto-generated method stub
        return true;
    }


}