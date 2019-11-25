package com.Ray.JMTree;

import java.util.LinkedHashMap;

import com.Ray.Libra.LedgerState.AccountResource;

/**
 * NodeType
 * 
 * @author Inray
 */
public class NodeType {

    private int version;
    private NibblePath nibble_path;

    public NodeType(int version, NibblePath nibble_path) {
        this.version = version;
        this.nibble_path = nibble_path;
    }

    /**
     * 生成由版本和空半字節路徑組成的節點密鑰的快捷方式。
     * 
     * @param version
     * @return
     */
    public NodeType newEmptyPath(int version) {
        return new NodeType(version, new NibblePath(new byte[0], true));
    }

    /**
     * 獲取版本。
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * 獲取半字節路徑。
     */
    public NibblePath getNibblePath() {
        return this.nibble_path;
    }

    /**
     * 根據該NodeKey生成子NodeKey。
     */
    public NodeType genChildNodeKeye(int version, byte n) {
        nibble_path.push(n);
        return new NodeType(version, nibble_path);
    }

    /**
     * 根據該節點密鑰生成相同版本的父節點密鑰。
     */
    public NodeType genParentNodeKey() {
        //if 父節點不是root
        nibble_path.pop();
        return new NodeType(version, nibble_path);
    }

    /**
     * 將版本設置為給定的版本。
     */
    public void setVersion(int version){
        this.version = version;        
    }

    /**
     * 序列化為物理存儲字節，以執行與內存中相同的順序。<br>
     * (存成物件)
     */
    public void encode(){
        
    }

    /**
     * 從物理存儲中的序列化字節中恢復。
     * (讀出物件)
     */
    public NodeType decode(){

        return null;
    }

    public static class Node {

        public Node() {

        }

        /**
         * 創建[`Null`]（Node :: Null）變體。
         */
        public void new_null() {

        }

        /**
         * 創建[`Internal`]（Node :: Internal）變體。
         */
        public void new_internal() {

        }

        /**
         * 創建[`Leaf`]（Node :: Leaf）變體。
         */
        public LeafNode new_leaf(String key, AccountResource ar) {
            return new LeafNode(key, ar);
        }

        /**
         * 如果該節點是葉節點，則返回“ true”。
         */
        public boolean is_leaf() {

            return false;
        }

        /**
         * 序列化為字節以進行物理存儲。
         */
        public void encode() {

        }

        /**
         * 計算節點的哈希值。
         */
        public void hash() {

        }

        /**
         * 從物理存儲中的序列化字節中恢復。
         */
        public void decode() {

        }

        // 內部節點
        public static class InternalNode {

            public LinkedHashMap<byte[], Child> Children = new LinkedHashMap<byte[], Child>();


            public InternalNode(LinkedHashMap<byte[], Child> Children){
                this.Children = Children;
            }

            //獲取第 n 個孩子。
            public Child getChild(byte[] n){
                return Children.get(n);
            }

            // 返回現有孩子的總數。
            public int getNumChildren(){
                return Children.size();
            }

            public static class Child{
                /**
                 * 此子節點的哈希值。
                 */                
                byte[] HashValue;
                /**
                 * 子版本號，即該子節點所屬的[InternalNode`]的['NodeKey`]的`nibble_path`，
                 * 並且子索引構成了[`NodeKey`]，以便從存儲中唯一地標識該子節點。 
                 * 由`[`NodeKey :: gen_child_node_key`]使用。
                 */
                int version;
                /**
                 * 子節點是否為葉節點。
                 */
                boolean is_leaf; 
                public Child(byte[] HashValue, int version, boolean is_leaf){
                    this.HashValue = HashValue;
                    this.version = version;
                    this.is_leaf = is_leaf;
                }
            }

        }

        // 葉節點
        public static class LeafNode {

            
            String account_key; // 帳戶地址
            AccountResource blob; // 帳戶內容

            /**
             * 創建一個新的葉子節點。
             * 
             * @param key
             * @param ar
             */
            public LeafNode(String key, AccountResource ar) {
                this.account_key = key;
                this.blob = ar;
            }

            /**
             * 獲取帳戶鑰，即哈希帳戶地址。
             */
            public String getAccountKey() {
                return this.account_key;
            }

            /**
             * 獲取關聯的Blob的哈希值。
             * 
             * @return
             */
            public String getBlobHash() {
                return blob.getRowData();
            }

            public AccountResource getBlob() {
                return this.blob;
            }

        }

    }
}