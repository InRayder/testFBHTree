package com.Ray.JMTree;

import java.util.Arrays;

import com.Ray.Utils.HashUtils;

/**
 * NibblePath 以半字節(4 bit)為單位定義 Merkle tree 中的路徑
 * 
 * 
 * @author Inray
 */
public class NibblePath {

    /**
     * 半字節路徑長度 <br>
     * 長度為 bytes.len * 2 - 1 或 bytes.len * 2 <br>
     * 路徑由上而下
     */
    private int num_nibbles;
    /**
     * 儲存路徑的基礎單位，每單位為兩個半字節。<br>
     * 若半字節為奇數，則最後一個單位的後半段必須為0
     */
    private byte[] bytes;

    public boolean isEven;

     /**
      * 創建一個新的半字節路徑
      * @param bytes 輸入路徑
      * @param isEven 該路徑是否為偶數長度
      */
    public NibblePath(byte[] bytes, boolean isEven) {
        this.bytes = bytes;
        // int a = bytes.length;
        // HashUtils hs = new HashUtils();
        // String hex_string = hs.binary(bytes, 16);
        // char t = hex_string.charAt(a * 2 - 1);
        this.isEven = isEven;
        if (isEven) {
            this.num_nibbles = bytes.length * 2;// 若為偶數
        } else {
            this.num_nibbles = bytes.length * 2 - 1;// 若為奇數
            // 需要補0
        }
    }

    /**
     * @return 返回以儲存的半字節總數
     */
    public int getNumNibbles() {
        return this.num_nibbles;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public boolean getIsEven(){
        return this.isEven;
    }

    /**
     * @return 返回以儲存的基礎字節(Str)
     */
    public String getBytesStr() {
        HashUtils hs = new HashUtils();
        String hex_string = hs.binary(this.bytes, 16);

        return (String) hex_string.subSequence(0, num_nibbles);
    }

    /**
     * 在半字節尾端添加一個半字節
     */
    public void push(byte nibble) {
        // 先判斷是否超過最大深度
        // if(num_nibbles < max)

        /**
         * 目前偶數狀況 [2] > [3] <br>
         * 0x12 >> 0x12 0x30 <br>
         * 目前奇數狀況 [3] > [4] <br>
         * 0x12 0x30 >> 0x12 0x34 <br>
         * *
         */
        if (num_nibbles % 2 == 0) {// 目前長度是偶數
            byte[] t_byte = Arrays.copyOf(bytes, bytes.length);// 備份陣列
            bytes = new byte[bytes.length + 1];
            System.arraycopy(t_byte, 0, bytes, 0, t_byte.length);// 複製陣列
            bytes[bytes.length - 1] = (byte) (nibble << 4);
        } else {// 目前長度是奇數
            bytes[num_nibbles / 2] = (byte) (bytes[num_nibbles / 2] | nibble);
        }

        this.num_nibbles++;
        this.isEven = !isEven;
    }

    /**
     * 從半字節路徑尾端彈出一個半字節
     */
    public byte pop() {
        // 判斷是否為空
        // if(num_nibbles <=0){

        // }
        /**
         * 偶數 <br>
         * 0x12, 0x34 => 0x12, 0x30 (直接覆蓋最後一個)<br>
         * 奇數 <br>
         * 0x12, 0x30 => 0x12 (長度會減1))<br>
         */
        byte popByte = bytes[bytes.length - 1];
        if (num_nibbles % 2 == 0) { // 目前路徑長度是偶數
            popByte = (byte) (popByte & 0x0f);
            bytes[bytes.length - 1] = (byte) (bytes[bytes.length - 1] & 0xf0);//(直接覆蓋最後一個)
        } else { // 目前路徑長度是奇數
            popByte = (byte) (popByte >>> 4);
            byte[] t_byte = Arrays.copyOf(bytes, bytes.length-1);// 備份陣列
            bytes = new byte[bytes.length - 1];
            System.arraycopy(t_byte, 0, bytes, 0, t_byte.length);// 複製陣列
            
        }
        

        this.num_nibbles--;
        this.isEven = !isEven;
        return popByte;
    }

    /**
     * 返回最後半字節
     */
    public void getLast() {

    }

    /**
     * 或取第i位bit
     * 
     * @param i
     */
    public boolean getBit(int i) {
        if (i / 4 >= this.num_nibbles) {// 不應超過半位元長度
            return false;
        }
        int p = i / 8;
        int b = 7 - i % 8;

        return ((bytes[p] >>> b) & 0x01) != 0;
    }

    /**
     * 獲取第i個半字節
     * 
     * @param i
     */
    public byte[] getNibble(int i) {
        if (i >= num_nibbles) {
            return new byte[0];
        }
        int index = i / 2;
        byte[] t = new byte[] { bytes[index] };
        if (i % 2 == 0) {// 前半位
            t[0] = (byte) (t[0] >>> 4);
        }
        t[0] = (byte) (t[0] & 0x0f);
        return t;
    }

    /**
     * 返回一個迭代器，遍歷整個半字節路徑
     */
    public BitIterator bits() {
        return new BitIterator(this, num_nibbles);
    }

    /**
     * 返回一個半字節迭代器，對整個半字節進行迭代
     */
    public NibbleIterator nibbles() {
        return new NibbleIterator(this, 0, num_nibbles);
    }

    public static class BitIterator {

        private NibblePath nibble_path;
        private Range pos;

        public BitIterator(NibblePath nibble_path, int pos) {
            this.nibble_path = nibble_path;
            this.pos = new Range(0, pos * 4);
        }

        /**
         * 窺視(Peek)下一個值而不進入迭代
         */
        public boolean Peekable() {
            return this.nibble_path.getBit(pos.start());
        }

        public boolean IteratorNext() {
            return this.nibble_path.getBit(pos.next());
        }

        /**
         * 反向迭代器
         * 
         * @return
         */
        public boolean DoubleEndedIterator() {
            return this.nibble_path.getBit(pos.next_back());
        }

    }

    public static class NibbleIterator {

        private NibblePath nibble_path;
        private Range pos;
        private int start;

        public NibbleIterator(NibblePath nibble_path, int pos) {
            this.nibble_path = nibble_path;
            this.pos = new Range(0, pos);
            this.start = 0;
        }

        public NibbleIterator(NibblePath nibble_path, int start, int end) {
            this.nibble_path = nibble_path;
            this.pos = new Range(start, end);
            this.start = start;
        }

        /**
         * 窺視(Peek)下一個值而不進入迭代
         */
        public byte[] Peekable() {
            return this.nibble_path.getNibble(pos.start());
        }

        /**
         * 返回下一個字節(byte)，但須介於0,16之間
         * 
         * @return
         */
        public byte[] IteratorNext() {
            return this.nibble_path.getNibble(pos.next());
        }

    }

    /**
     * test
     * 
     * @param args
     */
    public static void main(String[] args) {
        byte[] bytes1 = { (byte) 0xab, (byte) 0x34, (byte) 0x56 };// 偶數
        NibblePath np1 = new NibblePath(bytes1, true);
        byte[] bytes2 = { (byte) 0xcd, (byte) 0x34, (byte) 0x50 };// 奇數
        NibblePath np2 = new NibblePath(bytes2, false);

        /**
         * test_nibble_path_fmt <br>
         * 預期結果： <br>
         * ab3456 <br>
         * cd345 <br>
         */
        System.out.println("\ntest_nibble_path_fmt");
        System.out.println(np1.getBytesStr());
        System.out.println(np2.getBytesStr());

        /**
         * test_create_nibble_path_succes 預期結果： <br>
         * 6 <br>
         * 5 <br>
         */
        System.out.println("\ntest_create_nibble_path_succes");
        System.out.println(np1.getNumNibbles());
        System.out.println(np2.getNumNibbles());

        /**
         * test_get_nibble <br>
         * 預期結果： <br>
         * 10, a <br>
         * 11, b <br>
         * 3, 3 <br>
         * 4, 4 <br>
         */
        System.out.println("\ntest_get_nibble");
        HashUtils hs = new HashUtils();
        System.out.println(np1.getNibble(0)[0] + ", " + hs.binary(np1.getNibble(0), 16));
        System.out.println(np1.getNibble(1)[0] + ", " + hs.binary(np1.getNibble(1), 16));
        System.out.println(np1.getNibble(2)[0] + ", " + hs.binary(np1.getNibble(2), 16));
        System.out.println(np1.getNibble(3)[0] + ", " + hs.binary(np1.getNibble(3), 16));

        /**
         * test_nibble_iterator <br>
         * a, a, b, 3, 4, 5, 6, null(X)
         */
        System.out.println("\ntest_nibble_iterator");
        NibbleIterator iter = np1.nibbles();
        System.out.println(hs.binary(iter.Peekable(), 16));
        System.out.println(hs.binary(iter.IteratorNext(), 16));
        System.out.println(hs.binary(iter.IteratorNext(), 16));
        System.out.println(hs.binary(iter.IteratorNext(), 16));
        System.out.println(hs.binary(iter.IteratorNext(), 16));
        System.out.println(hs.binary(iter.IteratorNext(), 16));
        System.out.println(hs.binary(iter.IteratorNext(), 16));
        System.out.println(hs.binary(iter.IteratorNext(), 16));

        /**
         * test_get_bit <br>
         * 0000 0001 0000 0010 <br>
         * f f t f t <br>
         * 
         */
        System.out.println("\ntest_get_bit");
        byte[] bytes_get_bit = { (byte) 0x01, (byte) 0x02 };
        NibblePath np_get_bit = new NibblePath(bytes_get_bit, true);
        System.out.println(np_get_bit.getBytesStr());// 0x0102
        System.out.println(np_get_bit.getBit(0));// fales
        System.out.println(np_get_bit.getBit(3));// fales
        System.out.println(np_get_bit.getBit(7));// true
        System.out.println(np_get_bit.getBit(8));// fales
        System.out.println(np_get_bit.getBit(14));// true

        /**
         * test_bit_iter <br>
         * 
         */
        System.out.println("\ntest_bit_iter");
        byte[] bytes_bit_iter = { (byte) 0xc3, (byte) 0xa0 };
        NibblePath np_bit_iter = new NibblePath(bytes_bit_iter, false);
        BitIterator bit_iter = np_bit_iter.bits();
        System.out.println(np_bit_iter.getBytesStr());// 0xc3a
        System.out.println(np_bit_iter.num_nibbles);
        // c: 0b1100
        System.out.println(bit_iter.IteratorNext());// true
        System.out.println(bit_iter.IteratorNext());// true
        System.out.println(bit_iter.IteratorNext());// false
        System.out.println(bit_iter.IteratorNext());// false
        System.out.println();
        // 3: 0b0011
        System.out.println(bit_iter.IteratorNext());// false
        System.out.println(bit_iter.IteratorNext());// false
        System.out.println(bit_iter.IteratorNext());// true
        System.out.println(bit_iter.IteratorNext());// true
        System.out.println();
        // a: 0b1010
        System.out.println(bit_iter.DoubleEndedIterator());// true
        System.out.println(bit_iter.DoubleEndedIterator());// false
        System.out.println(bit_iter.DoubleEndedIterator());// true
        System.out.println(bit_iter.DoubleEndedIterator());// false

        /**
         * test_push <br>
         * 
         * 目前偶數狀況 [2] > [3] <br>
         * 0x12 >> 0x12 0x30 <br>
         * 目前奇數狀況 [3] > [4] <br>
         * 0x12 0x30 >> 0x12 0x34 <br>
         */
        System.out.println("\ntest_push");

        System.out.println("==even==");
        byte[] bytes_push_even = { (byte) 0x12 };
        byte byte_add_even = (byte) 0x03;
        NibblePath np_push_even = new NibblePath(bytes_push_even, true);
        System.out.println(np_push_even.getBytesStr()); // 12
        np_push_even.push(byte_add_even);
        System.out.println(np_push_even.getBytesStr()); // 123

        System.out.println("==odd==");
        byte[] bytes_push_odd = { (byte) 0x12, (byte) 0x30 };
        byte byte_add_odd = (byte) 0x04;
        NibblePath np_push_odd = new NibblePath(bytes_push_odd, false);
        System.out.println(np_push_odd.getBytesStr()); // 123
        np_push_odd.push(byte_add_odd);
        System.out.println(np_push_odd.getBytesStr()); // 1234

        /**
         * test_pop <br>
         */
        System.out.println("\ntest_pop");

        System.out.println("==even==");
        byte[] bytes_pop_even = { (byte) 0x12, (byte) 0x34 };
        NibblePath np_pop_even = new NibblePath(bytes_pop_even, true);
        System.out.println(np_pop_even.getBytesStr()); // 1234
        System.out.println(np_pop_even.pop()); // 4
        System.out.println(np_pop_even.getBytesStr()); // 123

        System.out.println("==odd==");
        byte[] bytes_pop_odd = { (byte) 0x12, (byte) 0x30 };
        NibblePath np_pop_odd = new NibblePath(bytes_pop_odd, false);
        System.out.println(np_pop_odd.getBytesStr()); // 123
        System.out.println(np_pop_odd.pop()); // 3
        System.out.println(np_pop_odd.getBytesStr()); // 12

    }

}