package com.Ray.JMTree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * RangeTest
 */
public class RangeTest {

    @Test
    public void test_start() {
        Range r = new Range(0, 10);
        assertEquals(r.start(), 0);
        assertEquals(r.start(), 0);  
    }

    @Test
    public void test_end() {
        Range r = new Range(0, 10);
        assertEquals(r.end(), 9);
        assertEquals(r.end(), 9);
    }
    @Test
    public void test_next() {
        Range r = new Range(0, 10);
        assertEquals(r.next(), 0);
        assertEquals(r.next(), 1);
    }
    @Test
    public void test_next_back() {
        Range r = new Range(0, 10);
        assertEquals(r.next_back(), 9);
        assertEquals(r.next_back(), 8);
    }
}