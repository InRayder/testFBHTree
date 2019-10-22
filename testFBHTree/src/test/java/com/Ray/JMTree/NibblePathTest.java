package com.Ray.JMTree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.Ray.JMTree.NibblePath.BitIterator;
import com.Ray.JMTree.NibblePath.NibbleIterator;

import org.junit.Test;

/**
 * NibblePathTest
 */
public class NibblePathTest {

    @Test
    public void test_nibble_path_fmt() {
        byte[] bytes1 = { (byte) 0xab, (byte) 0x34, (byte) 0x56 };// 偶數
        NibblePath np1 = new NibblePath(bytes1, true);
        byte[] bytes2 = { (byte) 0xcd, (byte) 0x34, (byte) 0x50 };// 奇數
        NibblePath np2 = new NibblePath(bytes2, false);

        assertEquals("ab3456", np1.getBytesStr());
        assertEquals("cd345", np2.getBytesStr());
    }

    @Test
    public void test_create_nibble_path_succes() {
        byte[] bytes1 = { (byte) 0xab, (byte) 0x34, (byte) 0x56 };// 偶數
        NibblePath np1 = new NibblePath(bytes1, true);
        byte[] bytes2 = { (byte) 0xcd, (byte) 0x34, (byte) 0x50 };// 奇數
        NibblePath np2 = new NibblePath(bytes2, false);

        assertEquals(6, np1.getNumNibbles());
        assertEquals(5, np2.getNumNibbles());
    }

    @Test
    public void test_get_nibble() {
        byte[] bytes1 = { (byte) 0xab, (byte) 0x34, (byte) 0x56 };// 偶數
        NibblePath np1 = new NibblePath(bytes1, true);

        assertEquals((byte) 0xa, np1.getNibble(0)[0]);
        assertEquals((byte) 0xb, np1.getNibble(1)[0]);
        assertEquals((byte) 0x3, np1.getNibble(2)[0]);
        assertEquals((byte) 0x4, np1.getNibble(3)[0]);
    }

    @Test
    public void test_nibble_iterator() {
        byte[] bytes1 = { (byte) 0xab, (byte) 0x34, (byte) 0x56 };// 偶數
        NibblePath np1 = new NibblePath(bytes1, true);
        NibbleIterator iter = np1.nibbles();

        assertEquals((byte) 0xa, iter.Peekable()[0]);
        assertEquals((byte) 0xa, iter.IteratorNext()[0]);
        assertEquals((byte) 0xb, iter.IteratorNext()[0]);
        assertEquals((byte) 0x3, iter.IteratorNext()[0]);
        assertEquals((byte) 0x4, iter.IteratorNext()[0]);
        assertEquals((byte) 0x5, iter.IteratorNext()[0]);
        assertEquals((byte) 0x6, iter.IteratorNext()[0]);
    }

    @Test
    public void test_get_bit() {
        byte[] bytes_get_bit = { (byte) 0x01, (byte) 0x02 };
        NibblePath np_get_bit = new NibblePath(bytes_get_bit, true);

        assertEquals(false, np_get_bit.getBit(0));
        assertEquals(false, np_get_bit.getBit(3));
        assertEquals(false, np_get_bit.getBit(0));
        assertEquals(true, np_get_bit.getBit(7));
        assertEquals(false, np_get_bit.getBit(8));
        assertEquals(true, np_get_bit.getBit(14));
    }

    @Test
    public void test_bit_iter() {
        byte[] bytes_bit_iter = { (byte) 0xc3, (byte) 0xa0 };
        NibblePath np_bit_iter = new NibblePath(bytes_bit_iter, false);
        BitIterator bit_iter = np_bit_iter.bits();

        assertEquals(3, np_bit_iter.getNumNibbles());
        // c: 0b1100
        assertEquals(true, bit_iter.IteratorNext());
        assertEquals(true, bit_iter.IteratorNext());
        assertEquals(false, bit_iter.IteratorNext());
        assertEquals(false, bit_iter.IteratorNext());
        // 3: 0b0011
        assertEquals(false, bit_iter.IteratorNext());
        assertEquals(false, bit_iter.IteratorNext());
        assertEquals(true, bit_iter.IteratorNext());
        assertEquals(true, bit_iter.IteratorNext());
        // a: 0b1010
        assertEquals(true, bit_iter.IteratorNext());
        assertEquals(false, bit_iter.IteratorNext());
        assertEquals(true, bit_iter.IteratorNext());
        assertEquals(false, bit_iter.IteratorNext());
    }

    @Test
    public void test_push() {
        // even
        byte[] bytes_push_even = { (byte) 0x12 };
        byte byte_add_even = (byte) 0x03;
        NibblePath np_push_even = new NibblePath(bytes_push_even, true);
        assertArrayEquals(new byte[]{(byte) 0x12}, np_push_even.getBytes());
        np_push_even.push(byte_add_even);
        assertArrayEquals(new byte[]{(byte) 0x12,(byte) 0x30}, np_push_even.getBytes());

        //odd
        byte[] bytes_push_odd = { (byte) 0x12, (byte) 0x30 };
        byte byte_add_odd = (byte) 0x04;
        NibblePath np_push_odd = new NibblePath(bytes_push_odd,false);
        assertArrayEquals(new byte[]{(byte) 0x12,(byte) 0x30}, np_push_odd.getBytes());
        np_push_odd.push(byte_add_odd);
        assertArrayEquals(new byte[]{(byte) 0x12,(byte) 0x34}, np_push_odd.getBytes());
    }

    @Test
    public void test_pop() {
        //even
        byte[] bytes_pop_even = { (byte) 0x12, (byte) 0x34 };
        NibblePath np_pop_even = new NibblePath(bytes_pop_even,true);
        assertArrayEquals(new byte[]{(byte) 0x12,(byte) 0x34}, np_pop_even.getBytes());
        assertEquals((byte) 0x4, np_pop_even.pop());
        assertArrayEquals(new byte[]{(byte) 0x12,(byte) 0x30}, np_pop_even.getBytes());
        
        //odd
        byte[] bytes_pop_odd = { (byte) 0x12, (byte) 0x30 };
        NibblePath np_pop_odd = new NibblePath(bytes_pop_odd,false);
        assertArrayEquals(new byte[]{(byte) 0x12,(byte) 0x30}, np_pop_odd.getBytes());
        assertEquals((byte) 0x3, np_pop_odd.pop());
        assertArrayEquals(new byte[]{(byte) 0x12}, np_pop_odd.getBytes());
    }

}