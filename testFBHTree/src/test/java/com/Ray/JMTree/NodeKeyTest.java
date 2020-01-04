package com.Ray.JMTree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.Ray.JMTree.Node.Child;
import com.Ray.JMTree.Node.InternalNode;
import com.Ray.JMTree.Node.NodeKeyAndHash;
import com.Ray.Utils.HashUtils;

import org.junit.Test;

/**
 * nodeKeyTest
 */
public class NodeKeyTest {



    /**
     * ==test用==<br>
     * Generate a random node key with 63 nibbles. (生成具有63個半字節的隨機節點密鑰。)
     * 
     * @throws UnsupportedEncodingException
     */
    public NodeKey random63NibblesNodeKey() {
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
    public NodeKey gen_leaf_keys(int version, NibblePath nibblePath, byte nibble) {
        if (nibblePath.getNumNibbles() != 63) {// 長度不對
            return null;
        }
        NibblePath np = new NibblePath(nibblePath.getBytes(), nibblePath.getIsEven());
        np.push(nibble);
        // byte[] account_key = 
        return new NodeKey(version, np);
    }

    @Test
    public void test_encode_decode() {
        NodeKey internal_node_key = random63NibblesNodeKey();

        NodeKey leaf1_keys = gen_leaf_keys(0, internal_node_key.getNibblePath(), (byte) 0x1);

        NodeKey leaf2_keys = gen_leaf_keys(0, internal_node_key.getNibblePath(), (byte) 0x2);

    }

    @Test
    public void test_leaf_hash(){
    
    }

    @Test
    public void two_leaves_test1() throws UnsupportedEncodingException {
        byte index1 = (byte) 0x00, index2 = (byte) 0x08;
        NodeKey internal_node_key = random63NibblesNodeKey();
        HashMap<Byte, Child> children = new HashMap<Byte, Child>();

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
        assertArrayEquals(internal_node.getHash(), root_hash);

        // NodeKeyAndHash nkah1 = internal_node.getChildWithSiblings(internal_node_key, index1);
        // assertEquals(nkah1.getNodeKey().getVersion(), leaf1_node_key.getVersion());
        // assertArrayEquals(nkah1.getHash().pop(), hash2);


        for (int i = 0; i < 8; i++) {
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf1_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf1_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf1_node_key);
            assertArrayEquals(nkah.getHash().pop(), hash2);
        }

        for (int i = 8; i < 16; i++) {
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf2_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf2_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf2_node_key);
            assertArrayEquals(nkah.getHash().pop(), hash1);
        }

    }

    @Test
    public void two_leaves_test2() throws UnsupportedEncodingException {
        byte[] SPARSE_MERKLE_PLACEHOLDER_HASH = HashUtils.hex2byte(HashUtils.sha256("0"));

        byte index1 = (byte) 0x04, index2 = (byte) 0x06;
        NodeKey internal_node_key = random63NibblesNodeKey();
        HashMap<Byte, Child> children = new HashMap<Byte, Child>();

        NodeKey leaf1_node_key = gen_leaf_keys(0, internal_node_key.getNibblePath(), index1);
        NodeKey leaf2_node_key = gen_leaf_keys(1, internal_node_key.getNibblePath(), index2);

        byte[] hash1 = HashUtils.hex2byte(HashUtils.sha256("1"));
        byte[] hash2 = HashUtils.hex2byte(HashUtils.sha256("2"));

        children.put(index1, new Child(hash1, 0, true));
        children.put(index2, new Child(hash2, 1, true));
        InternalNode internal_node = new InternalNode(children);

        // 內部節點的結構如下
        //
        //              root
        //              /
        //             /
        //            x2
        //             \
        //              \
        //               x1
        //              / \
        //             /   \
        //        leaf1     leaf2
        byte[] hash_x1 = HashUtils.sha256(hash1, hash2);;
        byte[] hash_x2 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash_x1);;

        byte[] root_hash = HashUtils.sha256(hash_x2,SPARSE_MERKLE_PLACEHOLDER_HASH);
        assertArrayEquals(internal_node.getHash(), root_hash);

        for(int i=0;i<4;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertNull(nkah.getNodeKey());
            /**
             * 這部分因為用stack，所以要反過來
             */
            assertArrayEquals(nkah.getHash().pop(), hash_x1);
            assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);         
        }

        for(int i=4;i<6;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf1_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf1_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf1_node_key);

            assertArrayEquals(nkah.getHash().pop(), hash2);
            assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
        }

        for(int i=6;i<8;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf2_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf2_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf1_node_key);

            assertArrayEquals(nkah.getHash().pop(), hash1);
            assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
        }

        for(int i=8;i<16;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertNull(nkah.getNodeKey());
            /**
             * 這部分因為用stack，所以要反過來
             */
            assertArrayEquals(nkah.getHash().pop(), hash_x2);            
        }
    }

    @Test
    public void three_leaves_test1() throws UnsupportedEncodingException {
        byte index1 = (byte) 0x00, index2 = (byte) 0x04, index3 = (byte) 0x08;
        NodeKey internal_node_key = random63NibblesNodeKey();
        HashMap<Byte, Child> children = new HashMap<Byte, Child>();

        NodeKey leaf1_node_key = gen_leaf_keys(0, internal_node_key.getNibblePath(), index1);
        NodeKey leaf2_node_key = gen_leaf_keys(1, internal_node_key.getNibblePath(), index2);
        NodeKey leaf3_node_key = gen_leaf_keys(2, internal_node_key.getNibblePath(), index3);
        
        byte[] hash1 = HashUtils.hex2byte(HashUtils.sha256("1"));
        byte[] hash2 = HashUtils.hex2byte(HashUtils.sha256("2"));
        byte[] hash3 = HashUtils.hex2byte(HashUtils.sha256("3"));

        children.put(index1, new Child(hash1, 0, true));
        children.put(index2, new Child(hash2, 1, true));
        children.put(index3, new Child(hash3, 2, true));
        InternalNode internal_node = new InternalNode(children);

        // 內部節點的結構如下
        //
        //               root
        //               / \
        //              /   \
        //             x     leaf3
        //            / \
        //           /   \
        //      leaf1     leaf2
        byte[] hash_x = HashUtils.sha256(hash1, hash2);

        byte[] root_hash = HashUtils.sha256(hash_x,hash3);
        assertArrayEquals(internal_node.getHash(), root_hash);

        for(int i=0;i<4;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf1_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf1_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf1_node_key);

            assertArrayEquals(nkah.getHash().pop(), hash2);
            assertArrayEquals(nkah.getHash().pop(), hash3);
        }

        for(int i=4;i<8;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf2_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf2_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf1_node_key);

            assertArrayEquals(nkah.getHash().pop(), hash1);
            assertArrayEquals(nkah.getHash().pop(), hash3);
        }

        for(int i=8;i<16;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf3_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf3_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf1_node_key);

            assertArrayEquals(nkah.getHash().pop(), hash_x);
        }
    }

    @Test
    public void mixed_nodes_test() throws UnsupportedEncodingException {
        byte[] SPARSE_MERKLE_PLACEHOLDER_HASH = HashUtils.hex2byte(HashUtils.sha256("0"));

        byte index1 = (byte) 0x00, index2 = (byte) 0x08;
        NodeKey internal_node_key = random63NibblesNodeKey();
        HashMap<Byte, Child> children = new HashMap<Byte, Child>();

        NodeKey leaf1_node_key = gen_leaf_keys(0, internal_node_key.getNibblePath(), index1);
        NodeKey internal2_node_key = gen_leaf_keys(1, internal_node_key.getNibblePath(), (byte)2);
        NodeKey internal3_node_key = gen_leaf_keys(2, internal_node_key.getNibblePath(), (byte)7);
        NodeKey leaf4_node_key = gen_leaf_keys(3, internal_node_key.getNibblePath(), index2);

        byte[] hash1 = HashUtils.hex2byte(HashUtils.sha256("1"));
        byte[] hash2 = HashUtils.hex2byte(HashUtils.sha256("2"));
        byte[] hash3 = HashUtils.hex2byte(HashUtils.sha256("3"));
        byte[] hash4 = HashUtils.hex2byte(HashUtils.sha256("4"));

        children.put(index1, new Child(hash1, 0, true));
        children.put((byte)2, new Child(hash2, 1, false));
        children.put((byte)7, new Child(hash3, 2, false));
        children.put(index2, new Child(hash4, 3, true));
        InternalNode internal_node = new InternalNode(children);

        // 內部節點（B）將具有以下結構
        //
        //                   B (root hash)
        //                  / \
        //                 /   \
        //                x5    leaf4
        //               / \
        //              /   \
        //             x2    x4
        //            / \     \
        //           /   \     \
        //      leaf1    x1     x3
        //               /       \
        //              /         \
        //          internal2      internal3
        //
        byte[] hash_x1 = HashUtils.sha256(hash2, SPARSE_MERKLE_PLACEHOLDER_HASH);
        byte[] hash_x2 = HashUtils.sha256(hash1, hash_x1);
        byte[] hash_x3 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash3);
        byte[] hash_x4 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash_x3);
        byte[] hash_x5 = HashUtils.sha256(hash_x2, hash_x4);

        byte[] root_hash = HashUtils.sha256(hash_x5,hash4);
        assertArrayEquals(internal_node.getHash(), root_hash);

        // 0~1 (leaf1)
        for(int i=0;i<2;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf1_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf1_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf1_node_key);

            assertArrayEquals(nkah.getHash().pop(), hash_x1);
            assertArrayEquals(nkah.getHash().pop(), hash_x4);
            assertArrayEquals(nkah.getHash().pop(), hash4);
        }

        // 2 (internal2)
        NodeKeyAndHash nkah2 = internal_node.getChildWithSiblings(internal_node_key, (byte) 2);
        assertEquals(nkah2.getNodeKey().getVersion(), internal2_node_key.getVersion());
        assertTrue(nkah2.getNodeKey().getNibblePath().getBytesStr().equals(internal2_node_key.getNibblePath().getBytesStr()));
        
        assertArrayEquals(nkah2.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
        assertArrayEquals(nkah2.getHash().pop(), hash1);
        assertArrayEquals(nkah2.getHash().pop(), hash_x4);
        assertArrayEquals(nkah2.getHash().pop(), hash4);


        // 3
        NodeKeyAndHash nkah3 = internal_node.getChildWithSiblings(internal_node_key, (byte) 3);
        assertNull(nkah3.getNodeKey());

        assertArrayEquals(nkah3.getHash().pop(), hash2);
        assertArrayEquals(nkah3.getHash().pop(), hash1);
        assertArrayEquals(nkah3.getHash().pop(), hash_x4);
        assertArrayEquals(nkah3.getHash().pop(), hash4);

        // 4~5
        for(int i=4;i<6;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertNull(nkah.getNodeKey());

            assertArrayEquals(nkah.getHash().pop(), hash_x3);
            assertArrayEquals(nkah.getHash().pop(), hash_x2);
            assertArrayEquals(nkah.getHash().pop(), hash4);
        }

        // 6
        NodeKeyAndHash nkah4 = internal_node.getChildWithSiblings(internal_node_key, (byte) 6);
        assertNull(nkah4.getNodeKey());

        assertArrayEquals(nkah4.getHash().pop(), hash3);
        assertArrayEquals(nkah4.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
        assertArrayEquals(nkah4.getHash().pop(), hash_x2);
        assertArrayEquals(nkah4.getHash().pop(), hash4);

        // 7 (internal3)
        NodeKeyAndHash nkah5 = internal_node.getChildWithSiblings(internal_node_key, (byte) 7);
        assertEquals(nkah5.getNodeKey().getVersion(), internal3_node_key.getVersion());
        assertTrue(nkah5.getNodeKey().getNibblePath().getBytesStr().equals(internal3_node_key.getNibblePath().getBytesStr()));
        
        assertArrayEquals(nkah5.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
        assertArrayEquals(nkah5.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
        assertArrayEquals(nkah5.getHash().pop(), hash_x2);
        assertArrayEquals(nkah5.getHash().pop(), hash4);

        // 8~15 (leaf4)
        for(int i=8;i<16;i++){
            NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
            assertEquals(nkah.getNodeKey().getVersion(), leaf4_node_key.getVersion());
            assertTrue(nkah.getNodeKey().getNibblePath().getBytesStr().equals(leaf4_node_key.getNibblePath().getBytesStr()));
            // assertSame(nkah.getNodeKey(), leaf1_node_key);

            assertArrayEquals(nkah.getHash().pop(), hash_x5);
        }



    }

    @Test
    public void test_internal_hash_and_proof() throws UnsupportedEncodingException {
        byte[] SPARSE_MERKLE_PLACEHOLDER_HASH = HashUtils.hex2byte(HashUtils.sha256("0"));

        // non-leaf case1
        {
            NodeKey internal_node_key = random63NibblesNodeKey();
            HashMap<Byte, Child> children = new HashMap<Byte, Child>();

            byte index1 = (byte) 04;
            byte index2 = (byte) 15;

            byte[] hash1 = HashUtils.hex2byte(HashUtils.sha256("1"));
            byte[] hash2 = HashUtils.hex2byte(HashUtils.sha256("2"));

            NodeKey child1_node_key = gen_leaf_keys(0, internal_node_key.getNibblePath(), index1);
            NodeKey child2_node_key = gen_leaf_keys(1, internal_node_key.getNibblePath(), index2);

            children.put(index1, new Child(hash1, 0, false));
            children.put(index2, new Child(hash2, 1, false));
            InternalNode internal_node = new InternalNode(children);
    
            // 內部節點（B）將具有以下結構
            //
            //              root
            //              / \
            //             /   \
            //            x3    x6
            //             \     \
            //              \     \
            //              x2     x5
            //              /       \
            //             /         \
            //            x1          x4
            //           /             \
            //          /               \
            // non-leaf1             non-leaf2
            //
            byte[] hash_x1 = HashUtils.sha256(hash1, SPARSE_MERKLE_PLACEHOLDER_HASH);
            byte[] hash_x2 = HashUtils.sha256(hash_x1, SPARSE_MERKLE_PLACEHOLDER_HASH);
            byte[] hash_x3 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash_x2);
            byte[] hash_x4 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash2);
            byte[] hash_x5 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash_x4);
            byte[] hash_x6 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash_x5);
    
            byte[] root_hash = HashUtils.sha256(hash_x3,hash_x6);
            assertArrayEquals(internal_node.getHash(), root_hash);

            // 0~3 
            for(int i=0;i<4;i++){
                NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
                assertNull(nkah.getNodeKey());

                assertArrayEquals(nkah.getHash().pop(), hash_x2);
                assertArrayEquals(nkah.getHash().pop(), hash_x6);
            }

            // 4 (child1)
            NodeKeyAndHash nkah1 = internal_node.getChildWithSiblings(internal_node_key, index1);
            assertEquals(nkah1.getNodeKey().getVersion(), child1_node_key.getVersion());
            assertTrue(nkah1.getNodeKey().getNibblePath().getBytesStr().equals(child1_node_key.getNibblePath().getBytesStr()));

            assertArrayEquals(nkah1.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah1.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah1.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah1.getHash().pop(), hash_x6);

            // 5
            NodeKeyAndHash nkah2 = internal_node.getChildWithSiblings(internal_node_key, (byte) 5);
            assertNull(nkah2.getNodeKey());

            assertArrayEquals(nkah2.getHash().pop(), hash1);
            assertArrayEquals(nkah2.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah2.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah2.getHash().pop(), hash_x6);

            // 6~7
            for(int i=6;i<8;i++){
                NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
                assertNull(nkah.getNodeKey());

                assertArrayEquals(nkah.getHash().pop(), hash_x1);
                assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
                assertArrayEquals(nkah.getHash().pop(), hash_x6);
            }

            // 8~11
            for(int i=8;i<12;i++){
                NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
                assertNull(nkah.getNodeKey());

                assertArrayEquals(nkah.getHash().pop(), hash_x5);                
                assertArrayEquals(nkah.getHash().pop(), hash_x3);
            }

            // 12~13
            for(int i=12;i<13;i++){
                NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
                assertNull(nkah.getNodeKey());

                assertArrayEquals(nkah.getHash().pop(), hash_x4);
                assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);               
                assertArrayEquals(nkah.getHash().pop(), hash_x3);
            }

            // 14
            NodeKeyAndHash nkah3 = internal_node.getChildWithSiblings(internal_node_key, (byte) 14);
            assertNull(nkah3.getNodeKey());

            assertArrayEquals(nkah3.getHash().pop(), hash2);
            assertArrayEquals(nkah3.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah3.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah3.getHash().pop(), hash_x3);

            // 15 (child2)
            NodeKeyAndHash nkah4 = internal_node.getChildWithSiblings(internal_node_key, index2);
            assertEquals(nkah4.getNodeKey().getVersion(), child2_node_key.getVersion());
            assertTrue(nkah4.getNodeKey().getNibblePath().getBytesStr().equals(child2_node_key.getNibblePath().getBytesStr()));

            assertArrayEquals(nkah4.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah4.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah4.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah4.getHash().pop(), hash_x3);


        }

        // non-leaf case2
        {
            NodeKey internal_node_key = random63NibblesNodeKey();
            HashMap<Byte, Child> children = new HashMap<Byte, Child>();

            byte index1 = (byte) 0;
            byte index2 = (byte) 7;

            byte[] hash1 = HashUtils.hex2byte(HashUtils.sha256("1"));
            byte[] hash2 = HashUtils.hex2byte(HashUtils.sha256("2"));

            NodeKey child1_node_key = gen_leaf_keys(0, internal_node_key.getNibblePath(), index1);
            NodeKey child2_node_key = gen_leaf_keys(1, internal_node_key.getNibblePath(), index2);

            children.put(index1, new Child(hash1, 0, false));
            children.put(index2, new Child(hash2, 1, false));
            InternalNode internal_node = new InternalNode(children);

            // 內部節點的結構如下
            //
            //                     root
            //                     /
            //                    /
            //                   x5
            //                  / \
            //                 /   \
            //               x2     x4
            //               /       \
            //              /         \
            //            x1           x3
            //            /             \
            //           /               \
            //  non-leaf1                 non-leaf2
            byte[] hash_x1 = HashUtils.sha256(hash1, SPARSE_MERKLE_PLACEHOLDER_HASH);
            byte[] hash_x2 = HashUtils.sha256(hash_x1, SPARSE_MERKLE_PLACEHOLDER_HASH);
            byte[] hash_x3 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash2);
            byte[] hash_x4 = HashUtils.sha256(SPARSE_MERKLE_PLACEHOLDER_HASH, hash_x3);
            byte[] hash_x5 = HashUtils.sha256(hash_x2, hash_x4);
    
            byte[] root_hash = HashUtils.sha256(hash_x5,SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(internal_node.getHash(), root_hash);

            // 0 (child1)
            NodeKeyAndHash nkah1 = internal_node.getChildWithSiblings(internal_node_key, (byte) 0);
            assertEquals(nkah1.getNodeKey().getVersion(), child1_node_key.getVersion());
            assertTrue(nkah1.getNodeKey().getNibblePath().getBytesStr().equals(child1_node_key.getNibblePath().getBytesStr()));

            assertArrayEquals(nkah1.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah1.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah1.getHash().pop(), hash_x4);
            assertArrayEquals(nkah1.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);

            // 1
            NodeKeyAndHash nkah2 = internal_node.getChildWithSiblings(internal_node_key, (byte) 1);
            assertNull(nkah2.getNodeKey());

            assertArrayEquals(nkah2.getHash().pop(), hash1);
            assertArrayEquals(nkah2.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah2.getHash().pop(), hash_x4);
            assertArrayEquals(nkah2.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);

            // 2~3
            for(int i=2;i<4;i++){
                NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
                assertNull(nkah.getNodeKey());

                assertArrayEquals(nkah.getHash().pop(), hash_x1);                
                assertArrayEquals(nkah.getHash().pop(), hash_x4);
                assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            }

            // 4~5
            for(int i=4;i<6;i++){
                NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
                assertNull(nkah.getNodeKey());

                assertArrayEquals(nkah.getHash().pop(), hash_x3);                
                assertArrayEquals(nkah.getHash().pop(), hash_x2);
                assertArrayEquals(nkah.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            }

            // 6
            NodeKeyAndHash nkah3 = internal_node.getChildWithSiblings(internal_node_key, (byte) 6);
            assertNull(nkah3.getNodeKey());

            assertArrayEquals(nkah3.getHash().pop(), hash2);
            assertArrayEquals(nkah3.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah3.getHash().pop(), hash_x2);
            assertArrayEquals(nkah3.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);

            // 7 (child2)
            NodeKeyAndHash nkah4 = internal_node.getChildWithSiblings(internal_node_key, index2);
            assertEquals(nkah4.getNodeKey().getVersion(), child2_node_key.getVersion());
            assertTrue(nkah4.getNodeKey().getNibblePath().getBytesStr().equals(child2_node_key.getNibblePath().getBytesStr()));

            assertArrayEquals(nkah4.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah4.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);
            assertArrayEquals(nkah4.getHash().pop(), hash_x2);
            assertArrayEquals(nkah4.getHash().pop(), SPARSE_MERKLE_PLACEHOLDER_HASH);

            // 8~15
            for(int i=8;i<16;i++){
                NodeKeyAndHash nkah = internal_node.getChildWithSiblings(internal_node_key, (byte) i);
                assertNull(nkah.getNodeKey());

                assertArrayEquals(nkah.getHash().pop(), hash_x5);
            }            
        }
    }

}