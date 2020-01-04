package com.Ray.JMTree;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.Ray.JMTree.Node.Child;
import com.Ray.JMTree.Node.InternalNode;
import com.Ray.JMTree.Node.NodeKeyAndHash;
import com.Ray.Utils.HashUtils;

/**
 * NodeType
 * 
 * @author Inray
 */
public class NodeKey {

    private int version;
    private NibblePath nibble_path;

    public NodeKey(int version, NibblePath nibble_path) {
        this.version = version;
        this.nibble_path = new NibblePath(nibble_path.getBytes(), nibble_path.getIsEven());
    }

   /**
     * 生成由版本和空半字節路徑組成的節點密鑰的快捷方式。
     * 
     * @param version
     * @return
     */
    public NodeKey(int version){
        byte[] b = new byte[2];
        this.version = version;
        this.nibble_path = new NibblePath(new byte[0], true);
    }

    public NodeKey getClone(){
        return new NodeKey(this.version,this.nibble_path);
    }
 
    // public NodeKey newEmptyPath(int version) {
    //     return new NodeKey(version, new NibblePath(new byte[0], true));
    // }

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
    public NodeKey genChildNodeKeye(int version, byte n) {
        nibble_path.push(n);
        return new NodeKey(version, nibble_path);
    }

    /**
     * 根據該節點密鑰生成相同版本的父節點密鑰。
     */
    public NodeKey genParentNodeKey() {
        // if 父節點不是root
        nibble_path.pop();
        return new NodeKey(version, nibble_path);
    }

    /**
     * 將版本設置為給定的版本。
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * 序列化為物理存儲字節，以執行與內存中相同的順序。<br>
     * (存成物件)
     */
    public void encode() {

    }

    /**
     * 從物理存儲中的序列化字節中恢復。 (讀出物件)
     */
    public NodeKey decode() {

        return null;
    }



    /**
     * ==test用==<br>
     * Generate a random node key with 63 nibbles. (生成具有63個半字節的隨機節點密鑰。)
     * 
     * @throws UnsupportedEncodingException
     */
    public static NodeKey random63NibblesNodeKey() {
        byte[] bytes = null;
        try {
            bytes = HashUtils.hex2byte(HashUtils.sha256(String.valueOf(System.nanoTime())));
            bytes[bytes.length - 1] &= 0xf0;
            return new NodeKey(0, new NibblePath(bytes, false));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new NodeKey(0, new NibblePath(bytes, false));

    }

    /**
     * 生成一對葉子節點密鑰和帳戶密鑰，以及一個傳入的63半字節節點密鑰和要附加的最後半字節。
     * 
     * @param version
     * @param nibblePath
     * @param nibble
     */
    public static NodeKey gen_leaf_keys(int version, NibblePath nibblePath, byte nibble) {
        if (nibblePath.getNumNibbles() != 63) {// 長度不對
            return null;
        }
        NibblePath np = new NibblePath(nibblePath.getBytes(), nibblePath.getIsEven());

        np.push(nibble);
        // byte[] account_key =
        return new NodeKey(version, np);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        byte index1 = (byte) 0x00, index2 = (byte) 0x08;
        NodeKey internal_node_key = random63NibblesNodeKey();
        HashMap<Byte, Child> children = new HashMap<Byte, Child>();

        System.out.println("getNibblePath: "+internal_node_key.getNibblePath().getBytesStr());

        NodeKey leaf1_node_key = gen_leaf_keys(0, internal_node_key.getNibblePath(), index1);
        NodeKey leaf2_node_key = gen_leaf_keys(1, internal_node_key.getNibblePath(), index2);
        byte[] hash1 = HashUtils.hex2byte(HashUtils.sha256("1"));
        byte[] hash2 = HashUtils.hex2byte(HashUtils.sha256("2"));

        children.put(index1, new Child(hash1, 0, true));
        children.put(index2, new Child(hash2, 1, true));
        InternalNode internal_node = new InternalNode(children);

        // 內部節點的結構如下
        //
        //      root
        //       / \
        //      /   \
        // leaf1    leaf2
        //
        byte[] root_hash = HashUtils.sha256(hash1, hash2);
        System.out.println("RootHash(實際)："+HashUtils.byte2hex(internal_node.getHash()));
        System.out.println("RootHash(預期)："+HashUtils.byte2hex(root_hash));
        // assertArrayEquals(internal_node.getHash(), root_hash);

        // NodeKeyAndHash nkah1 = internal_node.getChildWithSiblings(internal_node_key, index1);
        // System.out.println("version(實際)："+nkah1.getNodeKey().getVersion());
        // System.out.println("version(預期)："+leaf1_node_key.getVersion());
        // System.out.println("NodeKey.NibblePath(實際)："+nkah1.getNodeKey().getNibblePath().getBytesStr());
        // System.out.println("NodeKey.NibblePath(預期)："+leaf1_node_key.getNibblePath().getBytesStr());
        // System.out.println("Hash(實際)："+HashUtils.byte2hex(nkah1.getHash().pop()));
        // System.out.println("Hash(預期)："+HashUtils.byte2hex(hash2));
        // assertEquals(nkah1.getNodeKey().getVersion(), leaf1_node_key.getVersion());
        // assertArrayEquals(nkah1.getHash().pop(), hash2);

        for (int i = 0; i < 8; i++) {
            System.out.println("i="+i);
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            System.out.println("version(實際)："+nkah.getNodeKey().getVersion());
            System.out.println("version(預期)："+leaf1_node_key.getVersion());
            System.out.println("NodeKey.NibblePath(實際)："+nkah.getNodeKey().getNibblePath().getBytesStr());
            System.out.println("NodeKey.NibblePath(預期)："+leaf1_node_key.getNibblePath().getBytesStr());
            System.out.println("Hash(實際)："+HashUtils.byte2hex(nkah.getHash().pop()));
            System.out.println("Hash(預期)："+HashUtils.byte2hex(hash2));
            // assertEquals(nkah.getNodeKey().getVersion(), leaf1_node_key.getVersion());
            // assertSame(nkah.getNodeKey(), leaf1_node_key);
            // assertArrayEquals(nkah.getHash().pop(), hash2);
        }

        for (int i = 8; i < 16; i++) {
            System.out.println("i="+i);
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            System.out.println("version(實際)："+nkah.getNodeKey().getVersion());
            System.out.println("version(預期)："+leaf2_node_key.getVersion());
            System.out.println("NodeKey.NibblePath(實際)："+nkah.getNodeKey().getNibblePath().getBytesStr());
            System.out.println("NodeKey.NibblePath(預期)："+leaf2_node_key.getNibblePath().getBytesStr());
            System.out.println("Hash(實際)："+HashUtils.byte2hex(nkah.getHash().pop()));
            System.out.println("Hash(預期)："+HashUtils.byte2hex(hash1));
            // assertEquals(nkah.getNodeKey().getVersion(), leaf2_node_key.getVersion());
            // assertSame(nkah.getNodeKey(), leaf2_node_key);
            // assertArrayEquals(nkah.getHash().pop(), hash1);
        }

    }
    


    // /**
    //  * Node 包含內部節點(InternalNode)和葉子節點(LeafNode) <br>
    //  * 其中葉子節點代表一個帳戶
    //  */
    // public static class Node {

    //     public Node() {

    //     }

    //     /**
    //      * 創建[`Null`]（Node :: Null）變體。
    //      */
    //     public void new_null() {

    //     }

    //     /**
    //      * 創建[`Internal`]（Node :: Internal）變體。
    //      */
    //     public void new_internal() {

    //     }

    //     /**
    //      * 創建[`Leaf`]（Node :: Leaf）變體。
    //      */
    //     public LeafNode new_leaf(String key, AccountResource ar) {
    //         return new LeafNode(key, ar);
    //     }

    //     /**
    //      * 如果該節點是葉節點，則返回“ true”。
    //      */
    //     public boolean is_leaf() {

    //         return false;
    //     }

    //     /**
    //      * 序列化為字節以進行物理存儲。
    //      */
    //     public void encode() {

    //     }

    //     /**
    //      * 計算節點的哈希值。
    //      */
    //     public void hash() {

    //     }

    //     /**
    //      * 從物理存儲中的序列化字節中恢復。
    //      */
    //     public void decode() {

    //     }

    //     // 內部節點
    //     public static class InternalNode {

    //         public LinkedHashMap<byte[], Child> Children = new LinkedHashMap<byte[], Child>();

    //         public InternalNode(LinkedHashMap<byte[], Child> Children) {
    //             this.Children = Children;
    //         }

    //         // 獲取第 n 個孩子。
    //         public Child getChild(byte[] n) {
    //             return Children.get(n);
    //         }

    //         // 返回現有孩子的總數。
    //         public int getNumChildren() {
    //             return Children.size();
    //         }

    //         public static class Child {
    //             /**
    //              * 此子節點的哈希值。
    //              */
    //             byte[] HashValue;
    //             /**
    //              * 子版本號，即該子節點所屬的[InternalNode`]的['NodeKey`]的`nibble_path`，
    //              * 並且子索引構成了[`NodeKey`]，以便從存儲中唯一地標識該子節點。 由`[`NodeKey :: gen_child_node_key`]使用。
    //              */
    //             int version;
    //             /**
    //              * 子節點是否為葉節點。
    //              */
    //             boolean is_leaf;

    //             public Child(byte[] HashValue, int version, boolean is_leaf) {
    //                 this.HashValue = HashValue;
    //                 this.version = version;
    //                 this.is_leaf = is_leaf;
    //             }
    //         }

    //     }
    // }
}