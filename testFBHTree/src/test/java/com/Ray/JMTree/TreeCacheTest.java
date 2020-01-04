package com.Ray.JMTree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import com.Ray.JMTree.Node.LeafNode;
import com.Ray.JMTree.Node.Node;
import com.Ray.JMTree.Node.NodeAndNodeKey;
import com.Ray.Libra.LedgerState.AccountResource;
import com.Ray.Utils.HashUtils;

import org.junit.Test;

/**
 * TreeCacheTest
 */
public class TreeCacheTest {

    public NodeAndNodeKey random_leaf_with_key(int next_version) {
        byte[] address = null;
        try {
            String seed = String.valueOf(System.nanoTime());
            address = HashUtils.hex2byte(HashUtils.sha256(seed));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Node node= new LeafNode(address, new AccountResource(HashUtils.byte2hex(address)));
        NodeKey nodeKey = new NodeKey(next_version, new NibblePath(address, true));

        return new NodeAndNodeKey(node, nodeKey);
    }

    @Test
    public void test_get_node(){
        int next_version = 0;
        TreeCache cache = new TreeCache(next_version);
        NodeAndNodeKey nank = random_leaf_with_key(next_version);
        Node node = nank.getNode();
        NodeKey nodeKey = nank.getNodeKey();
        cache.putNode(nodeKey, node);
        assertArrayEquals(cache.getNode(nodeKey).getHash(), node.getHash());
    }

    @Test
    public void test_root_node(){
        int next_version = 0;
        TreeCache cache = new TreeCache(next_version);
        assertEquals(cache.getRootNodeKey().getVersion(), new NodeKey(0).getVersion());

        NodeAndNodeKey nank = random_leaf_with_key(next_version);
        Node node = nank.getNode();
        NodeKey nodeKey = nank.getNodeKey();
        cache.putNode(nodeKey, node);
        cache.setRootNodeKey(nodeKey);
        assertEquals(cache.getRootNodeKey().getVersion(), nodeKey.getVersion());
    }

    @Test
    public void test_freeze_with_delete(){
        int next_version = 0;
        TreeCache cache = new TreeCache(next_version);
        assertEquals(cache.getRootNodeKey().getVersion(), new NodeKey(0).getVersion());
        assertEquals(cache.getRootNodeKey().getNibblePath().getBytesStr(), new NodeKey(0).getNibblePath().getBytesStr());

        NodeAndNodeKey nank1 = random_leaf_with_key(next_version);
        Node node1 = nank1.getNode();
        NodeKey nodeKey1 = nank1.getNodeKey();
        cache.putNode(nodeKey1,node1);

        NodeAndNodeKey nank2 = random_leaf_with_key(next_version);
        Node node2 = nank2.getNode();
        NodeKey nodeKey2 = nank2.getNodeKey();
        cache.putNode(nodeKey2,node2);

        assertArrayEquals(cache.getNode(nodeKey1).getHash(), node1.getHash());
        assertArrayEquals(cache.getNode(nodeKey2).getHash(), node2.getHash());

    }
    
}