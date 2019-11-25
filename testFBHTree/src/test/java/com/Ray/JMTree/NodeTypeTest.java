package com.Ray.JMTree;

import java.io.UnsupportedEncodingException;

import com.Ray.Utils.HashUtils;

import org.junit.Test;

/**
 * nodeTypeTest
 */
public class NodeTypeTest {

    /**
     * ==test用==<br>
     * Generate a random node key with 63 nibbles. (生成具有63個半字節的隨機節點密鑰。)
     * 
     * @throws UnsupportedEncodingException
     */
    public NodeType random63NibblesNodeKey() {
        byte[] bytes;
        try {
            bytes = HashUtils.hex2byte(HashUtils.sha256(String.valueOf(System.nanoTime())));
            bytes[bytes.length - 1] &= 0xf0;
            return new NodeType(0, new NibblePath(bytes, false));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 生成一對葉子節點密鑰和帳戶密鑰，以及一個傳入的63半字節節點密鑰和要附加的最後半字節。
     * 
     * @param version
     * @param nibblePath
     * @param nibble
     */
    public NodeType gen_leaf_keys(int version, NibblePath nibblePath, byte nibble) {
        if (nibblePath.getNumNibbles() != 63) {// 長度不對

        }
        nibblePath.push(nibble);
        return new NodeType(version, nibblePath);
    }

    

    @Test
    public void test_encode_decode() {
        NodeType internal_node_key = random63NibblesNodeKey();

        NodeType leaf1_keys = gen_leaf_keys(0, internal_node_key.getNibblePath(), (byte)0x1);
        
        NodeType leaf2_keys = gen_leaf_keys(0, internal_node_key.getNibblePath(), (byte)0x2);

    }
}