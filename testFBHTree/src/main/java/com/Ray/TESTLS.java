package com.Ray;

import com.Ray.Utils.HashUtils;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TESTLS {

    long audit_time;
    List<BigInteger> row_sig = new ArrayList<>();
    List<byte[]> row_hash = new ArrayList<>();
    List<String> Index_PK = new ArrayList<>();
    List<String> Index_id = new ArrayList<>();
    List<String> Index_Name = new ArrayList<>();
    List<String> Slice_set = new ArrayList<>();
    List<String> PK = new ArrayList<>();
    List<String> id = new ArrayList<>();
    List<String> Name = new ArrayList<>();
    List<String> Pic = new ArrayList<>();
    List<String> Sig = new ArrayList<>();
    List<String> trash = new ArrayList<>();

    List<String> Index_id2 = new ArrayList<>();
    List<byte[]> row_hash2 = new ArrayList<>();

    // FBHTree fbh = new FBHTree(20);
    // byte[] roothash = newFBH(fbh);


    public static void main(String[] args) {
        TESTLS a = new TESTLS();
        // a.getSlice();
        // a.Slice_Audit();
        // a.Slice_Update();
        // a.Slice_Insert();
        // a.Slice_Delete();
        // a.testSize();
        a.Chain_Audit();
        a.Slice_Insert();
        a.getSlice();
        a.Bottom_up_Audit();
    }

    public void testSize() {
        FBHTree fbh = new FBHTree(20);
        newFBH(fbh);
        try {
            FileOutputStream fs = new FileOutputStream("D:/test/123");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(fbh);
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getSlice() {
        System.out.println("Testing getSlice...");
        FBHTree fbh = new FBHTree(20);
        newFBH(fbh);
        String slice = "";
        for (int i = 1; i <= 500; i++) {
            slice = fbh.extractSlice("id=" + i);
        }
        audit_time = System.nanoTime();
        for (int i = 501; i <= 1000; i++) {
            slice = fbh.extractSlice("id=" + i);
        }
        audit_time = (System.nanoTime() - audit_time);
        System.out.println(slice);
        showTime("Get Slice", audit_time);
        // System.out.println("Get Slice =" + (double) (audit_time) / 1000000 / 500);
    }

    public void Slice_Audit() {
        FBHTree fbh = new FBHTree(20);
        byte[] roothash = newFBH(fbh);
        for (int i = 1; i <= 500; i++) {
            String slice = fbh.extractSlice("id=" + i);
            // Slice_set.add(slice);

            byte[] slice_root = fbh.evalRootHashFromSlice(slice);
            Arrays.equals(slice_root, roothash);
        }
        audit_time = System.nanoTime();
        for (int i = 501; i <= 1000; i++) {
            String slice = fbh.extractSlice("id=" + i);
            // Slice_set.add(slice);

            byte[] slice_root = fbh.evalRootHashFromSlice(slice);
            Arrays.equals(slice_root, roothash);
        }
        audit_time = (System.nanoTime() - audit_time);
        System.out.println("audit row=" + (double) (audit_time) / 1000000 / 500);
    }

    public void Slice_Update() {
        System.out.println("Testing Slice_Update...");
        FBHTree fbh = new FBHTree(20);
        byte[] roothash = newFBH(fbh);
        for (int i = 1; i <= 500; i++) {
            // int j = (int) (Math.random() * 999998 + 1);
            byte[] slice_root = fbh.evalRootHashFromSlice(fbh.extractSlice("id=" + i));
            Arrays.equals(slice_root, roothash);
            fbh.remove("id=" + i);
            fbh.put("id=" + i, row_hash.get(i), row_hash.get(i - 1), row_hash.get(i + 1));
            fbh.getRootHash();// R
        }
        audit_time = System.nanoTime();
        for (int i = 501; i <= 1000; i++) {
            // int j = (int) (Math.random() * 999998 + 1);
            byte[] slice_root = fbh.evalRootHashFromSlice(fbh.extractSlice("id=" + i));
            Arrays.equals(slice_root, roothash);
            fbh.remove("id=" + i);
            fbh.put("id=" + i, row_hash.get(i), row_hash.get(i - 1), row_hash.get(i + 1));
            fbh.getRootHash();// R
        }

        audit_time = System.nanoTime() - audit_time;
        System.out.println(String.valueOf(roothash));
        showTime("update row", audit_time);
        // System.out.println("update row =" + (double) (audit_time) / 1000000 / 500);
    }

    public void Slice_Insert() {
        System.out.println("Testing Slice_Insert...");
        FBHTree fbh = new FBHTree(20);
        byte[] roothash = newFBH(fbh);
        for (int i = 1; i <= 500; i++) {
            // int j = (int) (Math.random() * 999998 + 1);
            byte[] slice_root = fbh.evalRootHashFromSlice(fbh.extractSlice("id=" + i));
            Arrays.equals(slice_root, roothash);
            fbh.put("id=" + i, row_hash.get(i), row_hash.get(i - 1), row_hash.get(i + 1));
            fbh.getRootHash();// R
        }
        audit_time = System.nanoTime();
        for (int i = 501; i <= 1000; i++) {
            // int j = (int) (Math.random() * 999998 + 1);
            byte[] slice_root = fbh.evalRootHashFromSlice(fbh.extractSlice("id=" + i));
            Arrays.equals(slice_root, roothash);
            fbh.put("id=" + i, row_hash.get(i), row_hash.get(i - 1), row_hash.get(i + 1));
            fbh.getRootHash();// R
        }
        audit_time = System.nanoTime() - audit_time;
        System.out.println(String.valueOf(roothash));
        showTime("insert row", audit_time);
        // System.out.println("insert row =" + (double) (audit_time) / 1000000 / 500);
    }

    public void Slice_Delete() {
        FBHTree fbh = new FBHTree(20);
        byte[] roothash = newFBH(fbh);
        for (int i = 1; i <= 500; i++) {

            byte[] slice_root = fbh.evalRootHashFromSlice(fbh.extractSlice("id=" + i));
            Arrays.equals(slice_root, roothash);
            fbh.remove("id=" + i);
            fbh.getRootHash();// R
        }
        audit_time = System.nanoTime();
        for (int i = 501; i <= 1000; i++) {

            byte[] slice_root = fbh.evalRootHashFromSlice(fbh.extractSlice("id=" + i));
            Arrays.equals(slice_root, roothash);
            fbh.remove("id=" + i);
            fbh.getRootHash();// R
        }
        audit_time = System.nanoTime() - audit_time;
        System.out.println("delete row =" + (double) (audit_time) / 1000000 / 500);
    }

    public void Chain_Audit() { // chain
        System.out.println("Testing Chain_Audit...");
        FBHTree fbh = new FBHTree(20);
        byte[] roothash = newFBH(fbh);
        for (int i = 1; i <= 100; i++) {
            fbh.get("id=" + i).equals(fbh.getPre("id=" + i + 1));
        }
        audit_time = System.nanoTime();
        for (int i = 1; i <= 50; i++) {
            fbh.get("id=" + i).equals(fbh.getPre("id=" + i + 1));
        }
        audit_time = System.nanoTime() - audit_time;
        System.out.println(String.valueOf(roothash));
        showTime("range select", audit_time);
        // System.out.println("range select =" + (double) (audit_time) / 1000000);
    }

    public void Bottom_up_Audit() {
        System.out.println("Testing Bottom_up_Audit...");
        FBHTree fbh = new FBHTree(20);
        byte[] roothash = newFBH(fbh);
        ArrayList<String> sample = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            int j = (int) (Math.random() * 999999 + 1);
            sample.add("id=" + j);
        }
        audit_time = System.nanoTime();
        fbh.Leaf_Turn(sample);
        audit_time = System.nanoTime() - audit_time;
        System.out.println(String.valueOf(roothash));
        showTime("audit_total_time in level audit", audit_time);
        // System.out.println("audit_total_time in level audit=" + (double) (audit_time)
        // / 1000000);
    }

    public void showTime(String testItem, long time) {
        System.out.println(testItem + " = " +  ((double)time / 1000000) + " ms");
        System.out.println();
    }

    public byte[] newFBH(FBHTree fbh) {
        System.out.println("Create a new FBH Tree...");
        audit_time = System.nanoTime();
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
        System.out.println("Finish a new FBH Tree...");
        audit_time = System.nanoTime() - audit_time;
        showTime("Total time", audit_time);
        return fbh.getRootHash();
    }

    public static void save(Object file) {
        try {
            FileOutputStream fs = new FileOutputStream("D:/test/123");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(file);
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
