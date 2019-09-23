package com.Ray;

import java.util.ArrayList;
import java.util.List;
import com.Ray.Utils.HashUtils;

/**
 * Hello world!
 *
 */
public class App {

    long audit_time;
    List<String> Index_id = new ArrayList<>();
    List<byte[]> row_hash = new ArrayList<>();

    public App() {
        System.out.println("Test start...");
        // System.out.println("Hello World!");
        // FBHTree fbh = new FBHTree(10);
        // newFBH(fbh,1000000);

        // byte[] roothash = fbh.getRootHash();
        // for (byte var : roothash) {
        //     System.out.print(var+" ");
        // }
        // System.out.println();
        // System.out.println(roothash);
    }

    public void Chain_Audit() { // chain
        System.out.println("Chain_Audit() running...");

        FBHTree fbh = new FBHTree(20);
        newFBH(fbh);
        // for (int i = 1; i <= 100; i++) {
        //     fbh.get("id=" + i).equals(fbh.getPre("id=" + i + 1));
        // }
        audit_time = System.nanoTime();
        for (int i = 1; i <= 1000; i++) {
            fbh.get("id=" + i).equals(fbh.getPre("id=" + i + 1));
        }
        audit_time = System.nanoTime() - audit_time;
        showTime(audit_time);
    }
    public void showTime(long time){
        System.out.println("range select = " + (double) (time) / 1000000 +" s");
    }

    public byte[] newFBH(FBHTree fbh) {
        String row;
        for (int i = 0; i < 1000000; i++) {
            row = (i + "").concat(",").concat((Math.random() * 99999 + 1) + "");
            row_hash.add(HashUtils.sha256(row.getBytes()));
            Index_id.add("id=" + i);
        }
        for (int i = 1; i < row_hash.size() - 1; i++) {
            fbh.put(Index_id.get(i), row_hash.get(i), row_hash.get(i - 1), row_hash.get(i + 1));
        }
        fbh.put(Index_id.get(0), row_hash.get(0), row_hash.get(0), row_hash.get(0));
        fbh.put(Index_id.get(row_hash.size() - 1), row_hash.get(row_hash.size() - 1), row_hash.get(row_hash.size() - 1),
                row_hash.get(row_hash.size() - 1));
        return fbh.getRootHash();
    }

    public static void main(String[] args) {
        App Ap = new App();
        Ap.Chain_Audit();

    }
}
