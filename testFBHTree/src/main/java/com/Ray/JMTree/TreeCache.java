package com.Ray.JMTree;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import com.Ray.JMTree.Node.LeafNode;
import com.Ray.JMTree.Node.Node;
import com.Ray.JMTree.Node.NodeAndNodeKey;
import com.Ray.JMTree.Node.NullNode;
import com.Ray.Libra.LedgerState.AccountResource;
import com.Ray.Utils.HashUtils;

/**
 * TreeCache
 */
public class TreeCache {
    // 緩存中跟節點的 "NodeKey"
    public NodeKey root_node_key;
    // 即將到來的 "put" 相關的交易版本
    public int next_version;
    // 以 hash 為key的中間節點
    public HashMap<NodeKey, Node> node_cache;
    // "node_cacge" 的葉子數
    public int num_new_leaves;
    // 部分過時的日誌。 NodeKey用來識別過時的記錄。
    public HashSet<NodeKey> stale_node_index_cache;
    // "stale_node_index_cache" 中的葉子數
    public int num_stale_leaves;
    // 此緩存的不可變部分，它將提交給基礎存儲。
    public FrozenTreeCache frozen_cache;


    public TreeCache(int next_version) {
        this.node_cache = new HashMap<NodeKey, Node>();
        // 如果第一個版本為0，則意味著我們需要從一棵空樹開始，因此我們需要事先插入一個空節點來處理這種極端情況。
        if (next_version == 0) {
            
            this.root_node_key = new NodeKey(0);
            this.node_cache.put(root_node_key, new NullNode());
        } else {
            this.root_node_key = new NodeKey(next_version - 1);
        }
        this.next_version = next_version;
        this.stale_node_index_cache = new HashSet<>();
        this.num_new_leaves = 0;
        this.num_stale_leaves = 0;
    }

    // 獲取具有給定節點密鑰的節點。 如果它在節點緩存中不存在，請從`reader`中讀取。
    public Node getNode(NodeKey node_key) {
        return node_cache.get(node_key);
    }

    // 獲取當前的根節點密鑰。
    public NodeKey getRootNodeKey() {
        // return new NodeKey(this.root_node_key.getVersion(), this.root_node_key.getNibblePath());
        return root_node_key;
    }

    // 設定根 "node_key"
    public void setRootNodeKey(NodeKey root_Node_Key) {
        this.root_node_key = root_Node_Key;
    }

    // 將具有給定哈希值的節點作為鍵放入node_cache。
    public void putNode(NodeKey nodeKey, Node newNode) {

        
        if (newNode.is_leaf()) {
            num_new_leaves += 1;
        }
        node_cache.put(nodeKey, newNode);
    }

    // 刪除具有給定哈希值的節點。
    public void deleteNode(NodeKey oldNodeKey, boolean isLeaf) {
        // 如果節點緩存中沒有該節點，則意味著該節點位於磁盤上樹的先前版本中。
        if (node_cache.remove(oldNodeKey) != null) {
            stale_node_index_cache.add(new NodeKey(oldNodeKey.getVersion(), oldNodeKey.getNibblePath()));
            if (isLeaf) {
                this.num_stale_leaves += 1;
            }
        } else if (isLeaf) {
            this.num_new_leaves -= 1;
        }
    }

    // 將緩存中的所有內容凍結為不可變，並清除“ node_cache”。
    public void freeze(){
        NodeKey T_root_node_key = this.getRootNodeKey();
        Node T_root_node = this.getNode(T_root_node_key);
        byte[] root_hash = T_root_node.getHash();
        System.out.println(HashUtils.byte2HEX(root_hash));
        
        this.frozen_cache.root_hashs.push(root_hash);
        this.frozen_cache.node_cache.putAll(this.node_cache);
        int stale_since_version = this.next_version;
        frozen_cache.stale_node_index_cache.addAll(this.stale_node_index_cache);
        frozen_cache.num_stale_leaves += this.num_stale_leaves;
        this.num_stale_leaves = 0;
        frozen_cache.num_new_leaves += this.num_new_leaves;
        this.num_new_leaves = 0;
        this.next_version += 1;

    }

    /**
     * FrozenTreeCache
     */
    public class FrozenTreeCache {
        
        // 不可變的節點快取
        public HashMap<NodeKey, Node> node_cache = new HashMap<NodeKey, Node>();

        // "node_cache" 中的葉子數
        int num_new_leaves = 0;
        // 不可變的 stale_node_index_cache
        public HashSet<NodeKey> stale_node_index_cache;

        // "stale_node_index_cache" 中的葉子數
        public int num_stale_leaves = 0;
        // 每個早期交易後凍結的rootHash
        Stack<byte[]> root_hashs = new Stack<byte[]>();

        public FrozenTreeCache(){

        }

    }


    public NodeAndNodeKey random_leaf_with_key(int next_version) {
        byte[] address = null;
        try {
            String seed = String.valueOf(System.nanoTime());
            address = HashUtils.hex2byte(HashUtils.sha256(seed));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Node node = new LeafNode(address, new AccountResource(HashUtils.byte2hex(address)));
        NodeKey nodeKey = new NodeKey(next_version, new NibblePath(address, true));

        return new NodeAndNodeKey(node, nodeKey);
    }
    public static void main(String[] args) {
        int next_version = 0;
        TreeCache cache = new TreeCache(next_version);
        // assertEquals(cache.getRootNodeKey().getVersion(), new NodeKey(0).getVersion());
        NodeKey t1 = cache.getRootNodeKey();
        NodeKey t2 = new NodeKey(0);
        // System.out.println("cache.getRootNodeKey():"+cache.getRootNodeKey().getVersion());
        // System.out.println("new NodeKey(0).getVersion():"+new NodeKey(0).getVersion());

        // System.out.println("cache.getRootNodeKey():"+cache.getRootNodeKey().getNibblePath().getBytesStr());
        // System.out.println("new NodeKey(0):"+new NodeKey(0).getNibblePath().getBytesStr());

        NodeAndNodeKey nank1 = cache.random_leaf_with_key(next_version);
        Node node1 = nank1.getNode();
        NodeKey nodeKey1 = nank1.getNodeKey();
        cache.putNode(nodeKey1,node1);

        NodeAndNodeKey nank2 = cache.random_leaf_with_key(next_version);
        Node node2 = nank2.getNode();
        NodeKey nodeKey2 = nank2.getNodeKey();
        cache.putNode(nodeKey2,node2);

        System.out.println("cache.getNode(nodeKey1).getHash():" + HashUtils.byte2hex(cache.getNode(nodeKey1).getHash()));
        System.out.println("node1.getHash():"+HashUtils.byte2hex(node1.getHash()));

        System.out.println("cache.getNode(nodeKey2).getHash():" + HashUtils.byte2hex(cache.getNode(nodeKey2).getHash()));
        System.out.println("node2.getHash():"+HashUtils.byte2hex(node2.getHash()));
        // assertArrayEquals(cache.getNode(nodeKey1).getHash(), node1.getHash());
        // assertArrayEquals(cache.getNode(nodeKey2).getHash(), node2.getHash());
        cache.freeze();
        

    }
}