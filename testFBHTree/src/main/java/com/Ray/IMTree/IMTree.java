package com.Ray.IMTree;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

import com.Ray.Utils.HashUtils;

/**
 * IMTree - version. 2019
 * 
 * @author InRay
 */
public class IMTree implements Serializable {

    private static final char SLICE_DELIMITER = '.';
    private static final int ESTIMATED_SLICE_LENGTH = 8192;

    private final int height;
    private final Node[] nodes;

    private final int shiftIndex;// 最左側葉子index

    /**
     * 建構具有初始高度的 IMTree.
     * 
     * @param treeHeight the initial tree height
     * @throws IllegalArgumentException if the specified initial tree height is
     *                                  smaller than 1
     */
    public IMTree(int treeHeight) {
        long time = System.nanoTime();
        if (treeHeight <= 0) {
            throw new IllegalArgumentException("The minimum value for tree height is 1.");
        }

        this.height = treeHeight;
        this.nodes = new Node[1 << height];
        this.shiftIndex = (1 << height) / 2; // 最左側葉子index
        for (int i = nodes.length - 1; i > 0; i--) { // 從葉子開始往上建
            if (i >= (1 << (height - 1))) { // leaf node
                nodes[i] = new Node(i, null, null);
            } else { // internal node
                nodes[i] = new Node(i, nodes[i * 2], nodes[i * 2 + 1]);
            }
        }

        System.out.println("建立樹時間:" + (double) (System.nanoTime() - time) / 1000000 + " ms");
    }

    /**
     * 定位函數，根據定位函數決定要到哪個葉子節點。 (Libra直接使用 Address 前 20 bits 當定位) (因為只使用前 20 個
     * bits，所以只抓取Address的前 5 個word) (5 word = 20 bits)
     * 
     * @param key
     * @return 對應的葉子節點
     */
    public int calcLeafIndex(String key) {

        String subKey = key.substring(0, 5); // 只需取得最左側5位
        int index = HashUtils.hex2dec(subKey) + shiftIndex;
        // System.out.println("address: "+key+"\nindex: "+index);
        return index;
    }

    // public int calcLeafIndex(String key) {
    // int index = 0;
    // String subKey = HashUtils.hex2bin(key, true);
    // subKey = subKey.substring(0,height-1);
    // String[] s = subKey.split("");
    // System.out.println(subKey);

    // for (int i = 0; i < subKey.length(); i++) {
    // index += Integer.valueOf(s[i]) ;
    // }
    // System.out.println(index);
    // return (1 << (height - 1)) + index;
    // }

    // public int calcLeafIndex(String key) {
    // byte[] digest = HashUtils.sha256(key.getBytes());
    // int index = 0;

    // if (digest.length >= 4) {
    // for (int i = 0; i < 4; i++) {
    // index += digest[i] << (i * 8);
    // }
    // }
    // return (1 << (height - 1)) + Math.abs(index) % (1 << (height - 1));
    // }

    /**
     * 將指定的 digestValue 放入Tree中的指定的 key。 如果指定的 key 中已經存在，則直接替換舊的 digestValue。
     * 
     * @param key
     * @param digestValue
     */
    public void put(String key, byte[] digestValue) {
        int index = calcLeafIndex(key);
        // System.out.println("put:" + key);
        // System.out.println("index:" + index);
        nodes[index].put(key, digestValue);
        for (int i = index; i > 0; i >>= 1) { // Slice 上的值都需要更新

            nodes[i].setDirty(true);
        }
    }

    /**
     * Returns <tt>true</tt> if this FBHTree contains a mapping for the specified
     * key. 確認指定的 account 是否存在在 IMTree 中
     */
    public boolean contains(String key) {
        return nodes[calcLeafIndex(key)].contains(key);
    }

    // get the hash of row (獲取這key的哈希值)
    public byte[] get(String key) {
        int index = calcLeafIndex(key);

        for (int i = index; i > 0; i /= 2) {
            nodes[i].setDirty(true);
        }

        return nodes[index].get(key);
    }

    /**
     * 返回RootHash
     * 
     * @return
     */
    public byte[] getRootHash() {
        return nodes[1].getContentDigest();
    }

    public Collection<byte[]> getContentList(String key) {
        int index = calcLeafIndex(key);
        return nodes[index].getContents();
    }

    /**
     * 提取指定的 key 中的slice
     * 
     * @param key
     * @return
     */
    public String extractSlice(String key) {
        if (!contains(key)) { // 確認該 key是否有被包在 IMTree中
            System.out.println(key);
            throw new NoSuchElementException("The specified key does not exist in this FBHTree");
        }

        int index = calcLeafIndex(key);
        String leftHexStr, rightHexStr;
        StringBuilder sliceBuilder = new StringBuilder(ESTIMATED_SLICE_LENGTH);

        sliceBuilder.append(index).append(SLICE_DELIMITER);

        // internal nodes
        for (; index > 1; index /= 2) {
            leftHexStr = rightHexStr = nodes[index].getContentDigestHexString();

            if (index % 2 == 0) {
                rightHexStr = nodes[index + 1].getContentDigestHexString();

            } else {
                leftHexStr = nodes[index - 1].getContentDigestHexString();
            }

            sliceBuilder.append(leftHexStr).append(SLICE_DELIMITER).append(rightHexStr).append(SLICE_DELIMITER);
        }

        sliceBuilder.append(nodes[1].getContentDigestHexString());

        return sliceBuilder.toString();
    }

    /**
     * 以遞歸方式解析和評估給定切片的根哈希值 。
     * 
     * @param slice 給定切片的根哈希的字節數組
     * @return
     */
    public byte[] evalRootHashFromSlice(String slice) {
        String[] tokens = slice.split(String.valueOf("\\" + SLICE_DELIMITER));
        int index = Integer.parseInt(tokens[0]);

        int parentIndex;
        byte[] parentDigest = null;

        for (int i = 1; index > 1; i += 2, index /= 2) {
            parentIndex = i + 2 + (index / 2 == 1 ? 0 : index / 2) % 2;
            parentDigest = HashUtils.sha256(HashUtils.hex2byte(tokens[i]), HashUtils.hex2byte(tokens[i + 1]));
            if (!HashUtils.byte2hex(parentDigest).equals(tokens[parentIndex])) {
                throw new VerifyError("Hashes of slice do not match.");
            }
        }

        return parentDigest;
    }

    /**
     * Basic node for IMTree.
     */
    private static class Node implements Serializable {

        private final int id;
        private final boolean isLeaf;
        private boolean dirty;
        private byte[] contentDigest;
        private String contentDigestHexStr;

        private final Node leftChild;
        private final Node rightChild;

        private LinkedHashMap<String, byte[]> contents;
        // private ArrayList<String> contentKeys;
        // private ArrayList<byte[]> contentValues;

        public Node(int id, Node leftChild, Node rightChild) {
            this.id = id;
            this.leftChild = leftChild;
            this.rightChild = rightChild;

            if (leftChild == null || rightChild == null) { // leaf node
                this.isLeaf = true;
                this.contentDigest = new byte[32]; // 儲存 HashValue (256 bit)

            } else { // internal node
                this.isLeaf = false;
                this.contentDigest = HashUtils.sha256(leftChild.getContentDigest(), rightChild.getContentDigest());
            }
            this.contentDigestHexStr = HashUtils.byte2hex(contentDigest);

            this.contents = null;
        }

        public void put(String key, byte[] bytes) {
            if (contents == null) {
                contents = new LinkedHashMap<>();
            }
            contents.put(key, bytes);
            setDirty(true);
        }

        public boolean contains(String key) {
            // if (contents != null) {
            if (contents != null) {
                // return indexOf(key) >= 0;
                return contents.containsKey(key);
            } else {
                return false;
            }
        }

        public byte[] get(String key) {
            return contents.get(key);
        }

        // private void updateContentDigest(){
        // if (isLeaf) { // leaf node

        // }else{ // internal node

        // }
        // }

        /**
         * (byte-計算用的)要求 node的 hashValue.
         * 
         * @return 返回該 node 的 hashValue
         */
        public byte[] getContentDigest() {
            updateContentDigest();
            return contentDigest;
        }

        /**
         * (String-給人看的)要求 node的 hashValue.
         * 
         * @return 返回該 node 的 hashValue
         */
        public String getContentDigestHexString() {
            updateContentDigest();
            return contentDigestHexStr;
        }

        private void updateContentDigest() {
            if (isDirty()) {
                if (isLeaf) { // 假設是葉子節點
                    contentDigest = HashUtils.sha256(contents.values());
                } else {
                    contentDigest = HashUtils.sha256(leftChild.getContentDigest(), rightChild.getContentDigest());
                }
                contentDigestHexStr = HashUtils.byte2hex(contentDigest);
                setDirty(false);
            }
        }

        public Collection<byte[]> getContents() {
            if (isLeaf) {
                if (contents == null) {
                    contents = new LinkedHashMap<>();
                }
                return contents.values();
            } else {
                throw new IllegalStateException("Internal node does not have contents.");
            }
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;

            if (this.dirty) {
                updateContentDigest();
            }

        }

        public boolean isDirty() {
            return dirty;
        }
    }

}