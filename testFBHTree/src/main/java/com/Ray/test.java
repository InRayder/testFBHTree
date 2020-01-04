package com.Ray;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * test
 */
public class test {

    public static void main(String[] args){
        // IMTree imt = new IMTree(21);
        // int index = imt.calcLeafIndex("ffffffffffff");
        // System.out.println(index);
        
        // int[] array = new int[] {0,1,2}; 
        // BitSet bs = new BitSet(4);//預設最少就是64bit

        // for (int i : array) {
        //     bs.set(i,true);
        // }

        // System.out.println(bs.size());
        // System.out.println(bs.get(0));
        // System.out.println(bs.get(1));
        // System.out.println(bs.get(2));
        // System.out.println(bs.get(3));
        // System.out.println(bs.get(63));
        // System.out.println(bs.get(64));

        System.out.println((10 & (1<<2)-1));
        System.out.println(count_ones(1));
        System.out.println(count_ones(2));
        System.out.println(count_ones(4));
        System.out.println(count_ones(8));
        System.out.println(count_ones(10));

    }

    private static boolean count_ones(int n) {
        for (int i = 1; i <= n; i *= 2) {
            if (i == n) {
                return true;
            }
        }
        return false;
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

    public static void loading() {
        String[] sy = { "-", "\\", "|", "/" };
        System.out.print("Progress:");
        for (int i = 1; i <= 100; i++) {
            System.out.print(sy[i % 4]);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
}
