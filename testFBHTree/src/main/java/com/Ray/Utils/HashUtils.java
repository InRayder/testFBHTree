package com.Ray.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Scott
 */
public class HashUtils {

    public static final Logger LOG;
    public static final char[] HEX_CHARS;

    static {
        LOG = Logger.getLogger(HashUtils.class.getName());
        HEX_CHARS = "0123456789abcdef".toCharArray();
    }

    public static String byte2hex(byte[] bytes) {
        return byte2HEX(bytes).toLowerCase();
    }

    public static String byte2HEX(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    public static byte[] hex2byte(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    public static String hex2bin(String s, boolean Padding) {
        String output;
        if (Padding) {
            // int i = new BigInteger(s, 16).intValue();
            int Output_Length = s.length() * 4;
            // output = String.format("%" + Output_Length + "s",Integer.toBinaryString(i)).replace(' ', '0');
            output = new BigInteger(s, 16).toString(2);
            while(output.length() < Output_Length) {
                output = "0"+output;
            }           
        } else {
            output = new BigInteger(s, 16).toString(2);
            
        }

        return output;
    }

    public static int hex2dec(String s) {
        return Integer.parseInt(s, 16);
    }

    public static byte[] sha256(byte[]... bytesArr) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            for (byte[] bytes : bytesArr) {
                md.update(bytes);
            }

            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, null, ex);

            return null;
        }
    }

    public static byte[] sha256(Collection<byte[]> bytesCollection) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            for (byte[] bytes : bytesCollection) {
                md.update(bytes);
            }

            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, null, ex);

            return null;
        }
    }

    /**
     * 
     * @param data 輸入UTF-8的字串
     * @return 回傳SHA256的值
     * @throws UnsupportedEncodingException
     */
    public static String sha256(String data) throws UnsupportedEncodingException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, null, ex);

            return null;
        }

        return byte2hex(md.digest());
    }

    // public static String sha256(String data) {
    // return byte2hex(data.getBytes());
    // }

    public static String sha256(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[8192];

            try (DigestInputStream dis = new DigestInputStream(is, md)) {
                while (dis.read(buffer) != -1)
                    ;
            }

            return byte2hex(md.digest());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);

            return null;
        }
    }

    /**
     * 測試用
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String str = "1000";
        // byte[] sha1000_b = HashUtils.sha256(HashUtils.hex2byte(str));
        // String sha1000_s = HashUtils.byte2hex(sha1000_b);

        // System.out.println("1000.getbyte = " + Arrays.toString(str.getBytes()));

        /**
         * SHA-256(1000) =
         * 40510175845988f13f6162ed8526f0b09f73384467fa855e1e79b44a56562a58
         * SHA-256(師大資工) =
         * d6164c076f55344295e42299437331683fe00fc8b1b0ff9ff7427acc0d4f4745
         */
        // System.out.println("sha256("+str+") = " + HashUtils.sha256(str));
        // System.out.println("sha256(師大資工) = " + HashUtils.sha256("師大資工"));

        String a = "4474aa4d1d9428df";
        System.out.println(HashUtils.hex2bin(a, true));

    }
}
