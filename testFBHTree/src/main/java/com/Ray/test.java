package com.Ray;

import java.math.BigInteger;

import com.Ray.IMTree.IMTree;

/**
 * test
 */
public class test {

    public static void main(String[] args) throws InterruptedException {
        // IMTree imt = new IMTree(21);
        // int index = imt.calcLeafIndex("ffffffffffff");
        // System.out.println(index);
        String[] sy = {"-","\\","|","/"};
        System.out.print("Progress:");
        for (int i = 1; i <= 100; i++) {
            System.out.print(sy[i%4]);
            Thread.sleep(100);
            System.out.print("\b");

            // System.out.print(i + "%");

            // Thread.sleep(100);


            // for (int j = 0; j <= String.valueOf(i).length(); j++) {
            //     System.out.print("\b");
            // }
        }
        System.out.print("\b");
        System.out.println("完成");
    }

    /**
     * 輸入葉子節點的 index 可以輸出一直到 rootHash 之間經過的 node
     * 
     * @param x
     */
    public static void findSlice(int x) {
        for (int i = 10; i > 0; i >>= 1) {
            System.out.println(i);
        }
    }

    /**
     * 輸入 (String) hex ，輸出2進位字串
     * 
     * @param s
     */
    public static void hex2bin(String s) {
        int i = new BigInteger(s, 16).intValue();
        int Output_Length = s.length() * 4;
        String output = String.format("%" + Output_Length + "s", Integer.toBinaryString(i)).replace(' ', '0');

        System.out.println(output);
    }
}
