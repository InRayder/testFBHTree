package com.Ray;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import com.Ray.Utils.HashUtils;

import org.junit.Test;

/**
 * HashUtilsTest
 */
public class HashUtilsTest {

    @Test
    public void sha256Test_str() throws UnsupportedEncodingException {
        String expected,actual;

        expected = "40510175845988f13f6162ed8526f0b09f73384467fa855e1e79b44a56562a58";
        actual = HashUtils.sha256("1000");
        assertEquals("sha256(1000):",expected, actual);

        expected = "d6164c076f55344295e42299437331683fe00fc8b1b0ff9ff7427acc0d4f4745";
        actual = HashUtils.sha256("師大資工");
        assertEquals("sha256(師大資工):",expected, actual);
    }

    
}