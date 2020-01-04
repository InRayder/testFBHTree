package com.Ray.JMTree.Node;

public class Child {

    Node node;

    byte[] hash;
    /**
     * 子版本號，即該子節點所屬的[InternalNode`]的['NodeKey`]的`nibble_path`，
     * 並且子索引構成了[`NodeKey`]，以便從存儲中唯一地標識該子節點。 由`[`NodeKey :: gen_child_node_key`]使用。
     */
    int version;
    /**
     * 此子節點是否為葉節點。
     */
    boolean is_leaf;

    public Child(byte[] hash, int version, boolean is_leaf) {
        this.hash = hash;
        this.version = version;
        this.is_leaf = is_leaf;
    }

    /**
     * 取得此子節點的哈希值。
     */
    public byte[] getHash() {
        return this.hash;
    }

    /**
     * 取得此解點的版本
     */
    public int getVersion(){
        return this.version;
    }

    /**
     * 返回是否為葉子節點
     */
    public boolean getIsLeaf(){
        return this.is_leaf;
    }

}