package com.Ray.JMTree;

import java.io.UnsupportedEncodingException;

import com.Ray.Utils.HashUtils;

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
        return new NodeType(version, new NibblePath(new byte[0],true));
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
    public void genChildNodeKeye(int version, byte n) {
        nibble_path.push(n);
        // new NodeType(version, nibble_path);
    }

    public void genParentNodeKey() {
        nibble_path.pop();
        // new NodeType(version, nibble_path);
    }



    public static void main(String[] args) throws UnsupportedEncodingException {
        /**
         * 生成具有63個半字節的隨機節點密鑰。
         */
        byte[] bytes;
        bytes = HashUtils.hex2byte(HashUtils.sha256(String.valueOf(System.nanoTime())));
        bytes[bytes.length-1] &= 0xf0; 
        NodeType internal_node_key = new NodeType(0,new NibblePath(bytes,false));

        /**
         * test_encode_decode
         */
        System.out.println("\ntest_encode_decode");
    }

    
}