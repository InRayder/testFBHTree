package com.Ray.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * ObjectUtils 參考網址:
 * https://codertw.com/%E7%A8%8B%E5%BC%8F%E8%AA%9E%E8%A8%80/313896/
 */
public class ObjectUtils {

    private final String objName;

    public ObjectUtils(String objName) {
        this.objName = objName + ".dat";
    }

    /**
     * 將物件寫入暫存檔案
     * 
     * @param obj
     */
    public void writeObjectToFile(Object obj) {
        File file = new File(objName);
        // System.out.println(file.toPath().toString());
        FileOutputStream out;
        try {
            System.out.println("寫入 "+objName+" 中...");
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            System.out.println("寫入 "+objName+" 成功!");
            // System.out.println("write " + objName + " success!");
        } catch (IOException e) {
            System.out.println("寫入 "+objName+" 失敗!");
            // System.out.println("write " + objName + " failed");
            e.printStackTrace();
        }
    }

    /**
     * 從檔案讀取物件
     * 
     * @return Object
     */
    public Object readObjectFromFile() {
        Object temp = null;
        File file = new File(objName);
        FileInputStream in;
        try {
            System.out.println("讀取 "+objName+" 中...");

            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
            System.out.println("讀取 "+objName+" 成功!");
            // System.out.println("read " + objName + " success!");
        } catch (IOException e) {
            System.out.println("讀取 "+objName+" 失敗!");
            // System.out.println("read " + objName + " failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }
}