package com.Ray.JMTree.Node;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import com.Ray.JMTree.NodeKey;
import com.Ray.Utils.HashUtils;

// 內部節點
public class InternalNode implements Node {

    /**
     * byte[] 內存nibble 當定位
     */
    public HashMap<Byte, Child> Children = new HashMap<Byte, Child>();

    public InternalNode(HashMap<Byte, Child> Children) {
        this.Children = Children;
    }

    // 獲取第 n 個孩子。
    public Child getChild(Byte n) {
        return Children.get(n);
    }

    // 返回現有孩子的總數。
    public int getNumChildren() {
        return Children.size();
    }

    /**
     * 以一對u16的形式生成“ existence_bitmap”和“ leaf_bitmap”： 如果設置了“ existence_bitmap
     * [i]”，則存在索引為“ i”的子級； 如果設置了“ leaf_bitmap [i]”，則索引“ i”的孩子是葉子節點。
     * 
     * @return (u16,u16)
     */
    public int[] genBitMaps() {
        int[] bitMap = new int[2];
        int existence_bitmap, leaf_bitmap;
        existence_bitmap = 0;
        leaf_bitmap = 0;

        for (Map.Entry<Byte, Child> c : Children.entrySet()) {
            Byte i = c.getKey();
            Child child = c.getValue();
            existence_bitmap |= 1 << i;
            leaf_bitmap |= (child.is_leaf ? 1 : 0) << i;
        }
        bitMap[0] = existence_bitmap;
        bitMap[1] = leaf_bitmap;

        return bitMap;
    }

    /**
     * 計算bit值為1的數量 https://www.itread01.com/content/1542563649.html
     * 
     * @param n
     * @return
     */
    private int count_ones(int n) {
        int count = 0;
        while (n != 0) {
            count += n & 1;
            n >>>= 1;
        }
        return count;
        // for (int i = 1; i <= n; i *= 2) {
        // if (i == n) {
        // return true;
        // }
        // }
        // return false;
    }

    /**
     * 計算尾隨0數量
     * 
     * @param n
     * @return
     */
    private int trailing_zeros(int n) {
        int count = 0;
        while (n != 0 && (n & 1) != 1) {
            count++;
            n >>>= 1;
        }
        return count;
    }

    /**
     * 給定一個範圍[start，start + width），返回該範圍的子bitmap <br>
     * A range with `start == 8` and `width == 4` will generate a mask
     * 0b0000111100000000.
     */
    public int[] rangeBitMap(int start, int width, int[] bitmaps) {
        // 下面再判斷
        /**
         * "start < 16" : 最多只能有16個子節點，所以不能超過16 "count_ones(width)" : width只能是2的指數 "start
         * % width == 0" :
         */
        if (start < 16 && count_ones(width) == 1 && (start % width) == 0) {
            int[] bitMap = Arrays.copyOf(bitmaps, bitmaps.length);
            int mask = (width == 16 ? 0xffff : (1 << width) - 1) << start;
            bitMap[0] &= mask;
            bitMap[1] &= mask;
            return bitMap;
        } else {
            return null;
        }
    }

    public byte[] JMT_sha256(byte[] left_child, byte[] right_child) {
        if (left_child == null) {
            return right_child;
        } else if (right_child == null) {
            return left_child;
        } else {
            return HashUtils.sha256(left_child, right_child);
        }
    }

    public byte[] MerkleHash(int start, int width, int[] bitMaps) {
        // System.out.println("s:" + start + ", w:" + width);

        // 0:range_existence_bitmap
        // 1:range_leaf_bitmap
        int[] rangeBitMaps = rangeBitMap(start, width, bitMaps);
        if (rangeBitMaps[0] == 0) {
            // 該子樹下沒有孩子
            // 回傳預設空值
            
            byte[] SPARSE_MERKLE_PLACEHOLDER_HASH=null;
            try {
                SPARSE_MERKLE_PLACEHOLDER_HASH = HashUtils.hex2byte(HashUtils.sha256("0"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return SPARSE_MERKLE_PLACEHOLDER_HASH;
        } else if (count_ones(rangeBitMaps[0]) == 1 && (rangeBitMaps[1] != 0 || width == 1)) {
            // 此子樹下只有一個葉子子級或達到最低級別
            byte only_child_index;
            only_child_index = (byte) trailing_zeros(rangeBitMaps[0]);
            return this.getChild(only_child_index).getHash();
        } else {
            byte[] left_child = this.MerkleHash(start, width / 2, bitMaps);
            byte[] right_child = this.MerkleHash(start + width / 2, width / 2, bitMaps);
            if (left_child != null & right_child != null) {
                // System.out.println("left_child:" + HashUtils.byte2hex(left_child));
                // System.out.println("right_child:" + HashUtils.byte2hex(right_child));
            }

            return JMT_sha256(left_child, right_child);
        }

    }

    /**
     * 得到兄弟的子節點 獲取生成第n個孩子的證明所必需的孩子及其對應的同級兄弟。 如果是存在證明，則返回的孩子必須是第n個孩子；
     * 否則，返回的孩子可能是另一個孩子。
     */
    public NodeKeyAndHash getChildWithSiblings(NodeKey t_nodeKey, byte nibble) {
        NodeKey nodeKey = new NodeKey(t_nodeKey.getVersion(), t_nodeKey.getNibblePath());
        Stack<byte[]> siblings = new Stack<byte[]>();
        /**
         * bitMaps[0]:existence_bitmap, bitMaps[1]:leaf_bitmap
         */
        int[] bitMaps = this.genBitMaps();

        // 半字節高度從 3 到 0
        for (int h = 3; h >= 0; h--) {
            // 獲取此高度的每個子樹覆蓋的內部節點的子節點數。
            int width = 1 << h;
            /**
             * halfStart[0]:child_half_start, halfStart[1]:sibling_half_start
             */
            int[] halfStart = getChildAndSliblingHalfStart(nibble, h);
            // 計算以`r`的兄弟為根的子樹的根哈希。
            siblings.push(this.MerkleHash(halfStart[1], width, bitMaps));
            /**
             * bitMaps[0]:range_existence_bitmap, bitMaps[1]:rnage_leaf_bitmap
             */
            int[] range_bitMap = this.rangeBitMap(halfStart[0], width, bitMaps);

            if (range_bitMap[0] == 0) {
                // 沒有子樹在這個range
                // return new NullNode(), siblings;
                return new NodeKeyAndHash(null, siblings);
            } else if (count_ones(range_bitMap[0]) == 1 && ((count_ones(range_bitMap[1]) == 1) || width == 1)) {
                /**
                 * 返回此子樹下的唯一1個葉子子級或達到最低級別。 即使此葉子子級不是第n個子級，也應返回它而不是`None'，因為它的存在間接證明第n個子級不存在 。
                 */
                byte only_child_index = (byte) trailing_zeros(range_bitMap[0]);
                int only_child_version = getChild(only_child_index).getVersion();

                return new NodeKeyAndHash(nodeKey.genChildNodeKeye(only_child_version, only_child_index), siblings);
            }
        }
        return null;
    }

    /**
     * 得到子節點和兄弟節點的一半開始 輕而易舉地計算其“ child_half_start”和“ sibling_half_start”在“
     * height”級別的開始位置。
     */
    public int[] getChildAndSliblingHalfStart(byte nibble, int height) {
        // 0:child_half_start
        // 1:sibling_half_start
        int[] halfStart = new int[2];

        // 獲取屬於同一子樹的第一個子節點的索引，該子樹的根為根，假設“ r”位於第n個子節點所屬的“ height”處。
        // 注意：在高度0處，child_half_start始終等於n。
        halfStart[0] = (0xff << height) & nibble;

        // 獲取屬於子樹的第一個孩子的索引，該子樹的根是`h`的`r`的兄弟。
        halfStart[1] = halfStart[0] ^ (1 << height);

        return halfStart;
    }

    /**
     * 返回該internalNode的Hash值
     */
    @Override
    public byte[] getHash() {
        // TODO Auto-generated method stub

        return this.MerkleHash(0, 16, this.genBitMaps());
    }

    @Override
    public void encode() {
        // TODO Auto-generated method stub

    }

    @Override
    public void decode() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean is_leaf() {
        // TODO Auto-generated method stub
        return false;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        // test
        byte[] a1, a2, a3, a4;
        Child c1, c2, c3, c4;

        a1 = HashUtils.hex2byte(HashUtils.sha256("a1"));
        c1 = new Child(a1, 0, true);
        // System.out.println(HashUtils.byte2hex(a1));

        a2 = HashUtils.hex2byte(HashUtils.sha256("a2"));
        c2 = new Child(a2, 1, true);
        // System.out.println(HashUtils.byte2hex(a2));

        a3 = HashUtils.hex2byte(HashUtils.sha256("a3"));
        c3 = new Child(a3, 0, false);
        // System.out.println(HashUtils.byte2hex(a3));

        a4 = HashUtils.hex2byte(HashUtils.sha256("a4"));
        c4 = new Child(a4, 0, false);
        // System.out.println(HashUtils.byte2hex(a4));

        HashMap<Byte, Child> child = new HashMap<Byte, Child>();
        child.put((byte) 0x00, c1);
        child.put((byte) 0x01, c2);
        // child.put((byte) 0x02, c3);
        // child.put((byte) 0x03, c4);
        // child.put((byte) 0x04, c1);
        // child.put((byte) 0x05, c2);
        // child.put((byte) 0x06, c3);
        // child.put((byte) 0x07, c4);

        // child.put((byte) 0x08, c1);
        // child.put((byte) 0x09, c2);
        // child.put((byte) 0x0a, c3);
        // child.put((byte) 0x0b, c4);
        // child.put((byte) 0x0c, c1);
        // child.put((byte) 0x0d, c2);
        // child.put((byte) 0x0e, c3);
        // child.put((byte) 0x0f, c4);

        // 測試建構子
        InternalNode IN = new InternalNode(child);
        System.out.println("Internal node 數量:" + IN.getNumChildren());
        // 測試getChild 0x00
        Child c1_t = IN.getChild((byte) 0x00);
        System.out.println("第一個node 的 HASH:" + HashUtils.byte2hex(c1_t.getHash()));
        System.out.println("第一個node 的 版本:" + c1_t.getVersion());
        System.out.println("第一個node 是否為葉節點:" + c1_t.getIsLeaf());

        // 測試getChild 0x08
        Child c2_t = IN.getChild((byte) 0x01);
        System.out.println("第二個node 的 HASH:" + HashUtils.byte2hex(c2_t.getHash()));
        System.out.println("第二個node 的 版本:" + c2_t.getVersion());
        System.out.println("第二個node 是否為葉節點:" + c2_t.getIsLeaf());

        // 測試bitMpas
        int[] bitMap = IN.genBitMaps();
        System.out.println("existence_bitmap:" + bitMap[0]);
        System.out.println("leaf_bitmap:" + bitMap[1]);
        int[] rang_bitMap = IN.rangeBitMap(0, 4, bitMap);
        System.out.println("rang_existence_bitmap:" + rang_bitMap[0]);
        System.out.println("rang_leaf_bitmap:" + rang_bitMap[1]);
        System.out.println("rang_leaf_bitmap 是否只有一個:" + IN.count_ones(rang_bitMap[1]));

        // byte[] ha = IN.MerkleHash(0, 2, bitMap);
        // System.out.println("該個inter node 的merkle root:" + HashUtils.byte2hex(ha));

        // 測試root hash
        byte[] RH = IN.getHash();
        byte[] RH_ = HashUtils.sha256(a1, a2);
        System.out.println("node的RootHash:\t" + HashUtils.byte2hex(RH));
        System.out.println("計算的RootHash:\t" + HashUtils.byte2hex(RH_));


        byte index1=(byte)0x00, index2=(byte)0x01;
        HashMap<Byte, Child> children = new HashMap<Byte, Child>();

        byte[] hash1 = HashUtils.hex2byte(HashUtils.sha256("1"));
        byte[] hash2 = HashUtils.hex2byte(HashUtils.sha256("2"));
        
        children.put(index1, new Child(hash1, 0, true));
        children.put(index2, new Child(hash2, 1, true));
        InternalNode internal_node = new InternalNode(children);

        byte[] root_hash = HashUtils.sha256(hash1,hash2);
        byte[] root_hash_ = internal_node.getHash();
        System.out.println(HashUtils.byte2hex(root_hash_));
        System.out.println(HashUtils.byte2hex(root_hash));
        System.out.println(root_hash_);
        System.out.println(root_hash);
    }

}
