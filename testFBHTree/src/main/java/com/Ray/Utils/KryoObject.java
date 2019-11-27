package com.Ray.Utils;

import com.Ray.IMTree.IMTree;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;

import java.io.*;

/**
 * KryoObject <br>
 * 參考 <br>
 * https://github.com/EsotericSoftware/kryo <br>
 * https://github.com/magro/kryo-serializers <br>
 */
public class KryoObject {

    private final String objName;
    public Kryo kryo;

    public KryoObject(Class className, String objName) {
        this.kryo = new Kryo();
        this.objName = objName + ".bin";
        this.kryo.register(className);
        SynchronizedCollectionsSerializer. registerSerializers(kryo);
    }

    /**
     * 將物件寫入kryo物件檔案中
     * 
     * @param obj
     */
    public void writeObjectToFile(Object obj) {
        Output output = null;
        try {
            System.out.println("寫入 " + objName + " 中...");
            output = new Output(new FileOutputStream(objName));
            kryo.writeObject(output, obj);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("寫入 " + objName + " 失敗!");
            e.printStackTrace();
        } finally {
            output.close();
            System.out.println("寫入 " + objName + " 成功!");
        }
    }

    /**
     * 從kryo物件檔案中讀取物件
     * 
     * @return Object
     */
    public Object readObjectFromFile() {
        Input input;
        Object temp = null;
        try {
            System.out.println("讀取 " + objName + " 中...");
            input = new Input(new FileInputStream(objName));
            temp = kryo.readObject(input, IMTree.class);
            input.close();
            System.out.println("讀取 " + objName + " 成功!");
        } catch (FileNotFoundException e) {
            // } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("讀取 " + objName + " 失敗!");
            e.printStackTrace();
        }

        return temp;
    }

    public static void main(String[] args) throws FileNotFoundException {

        String fileName = "IMT21_oneAccount";
        IMTree imt = new IMTree(21);
        KryoObject kObject = new KryoObject(IMTree.class, fileName);
        String account = "";
        byte[] digestValue = null;

        try {
            account = HashUtils.sha256(String.valueOf(System.nanoTime()));
            digestValue = HashUtils.hex2byte(HashUtils.sha256(account));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 差值入樹中
        imt.put(account, digestValue);
        System.out.println("account: " + account + ", hash value: " + HashUtils.byte2hex(digestValue));
        System.out.println("roohHash(row): " + HashUtils.byte2hex(imt.getRootHash()));

        // 將物件打包成object
        kObject.writeObjectToFile(imt);
        // System.out.println(kObject.saveTpmTreeObject(imt));

        // 從object中讀出物件
        IMTree imt_tmp = (IMTree) kObject.readObjectFromFile();
        // IMTree imt_tmp = kObject.readTpmTreeObject(fileName);
        System.out.println("roohHash(read): " + HashUtils.byte2hex(imt_tmp.getRootHash()));

    }
}