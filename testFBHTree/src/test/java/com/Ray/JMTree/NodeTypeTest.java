package com.Ray.JMTree;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import com.Ray.JMTree.NibblePath;
import com.Ray.JMTree.NodeType;
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
    public NodeType random63NibblesNodeKey() throws UnsupportedEncodingException {
        byte[] bytes;
        bytes = HashUtils.hex2byte(HashUtils.sha256(String.valueOf(System.nanoTime())));
        bytes[bytes.length - 1] &= 0xf0;
        return new NodeType(0, new NibblePath(bytes, false));
    }

    @Test
    public void test_encode_decode(){
        assertTrue(true);
    }
}