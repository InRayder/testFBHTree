package com.Ray.JMTree;

import com.Ray.JMTree.NibblePath.NibbleIterator;
import com.Ray.JMTree.Node.Node;
import com.Ray.Libra.LedgerState.AccountResource;

/**
 * JMTree_tmp
 */
public class JMTree_tmp {


   /**
    * 必要項目
   * 建構值
   * put
   */

   public JMTree_tmp(){


   }

   public void put(byte[] key,AccountResource blob, byte[] digestValue, TreeCache treeCache){
      NibblePath np = new NibblePath(key, true);

      // 獲取根節點。 如果這是第一個操作，它將從基礎數據庫獲取根節點。 否則，它很可能來自“ cache”。
      NodeKey rootNodeKey = treeCache.getRootNodeKey();
      NibbleIterator nibble_iter = np.nibbles();

      // 從根節點開始插入。




   }
   // 用於從當前[`NodeKey`]（node_type / struct.NodeKey.html）開始遞歸插入子樹的Helper函數。
   // 返回新插入的節點。 在這裡使用遞歸是安全的，因為最大深度受密鑰長度的限制，對於該樹，密鑰長度是帳戶地址的哈希值的長度。
   public void insertAt(NodeKey nodeKey,int version,NibbleIterator nibbleIterator,AccountResource blob,TreeCache treeCache){
      Node node = treeCache.getNode(nodeKey);
      

   }
    
}