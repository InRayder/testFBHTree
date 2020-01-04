package com.Ray.JMTree.Node;

/**
 * Node
 */
public interface Node {

/**
* 如果該節點是葉節點，則返回“ true”。
*/
public boolean is_leaf();

/**
* 計算節點的哈希值。
*/
public abstract byte[] getHash();

/**
* 序列化為字節以進行物理存儲。
*/
public abstract void encode();

/**
* 從物理存儲中的序列化字節中恢復。
*/
public abstract void decode();
}

// public class Node {

//     boolean isLeaf = false;

//     /**
//      * Creates null
//      */
//     public Node() {

//     }

//     /**
//      * Creates Internal
//      * 
//      * @param child
//      */
//     public Node(Child child) {
//         this.isLeaf = false;
//     }

//     /**
//      * Creates Leaf
//      * 
//      * @param key
//      * @param ar
//      */
//     public Node(String key, AccountResource ar) {
//         this.isLeaf = true;
//     }

//     public boolean isLeaf() {
//         return isLeaf;
//     }

//     /**
//      * 計算節點的哈希值。
//      */
//     public void hash() {

//     }

//     /**
//      * 序列化為字節以進行物理存儲。
//      */
//     public void encode() {

//     }

//     /**
//      * 從物理存儲中的序列化字節中恢復。
//      */
//     public void decode() {

//     }

//     // 葉子節點
//     public static class LeafNode {

//     }
//     // 內部節點
//     public static class InternalNode {

//     }

// }
