package com.Ray.JMTree;

import java.util.ArrayList;

import com.Ray.JMTree.Node.Child;
import com.Ray.JMTree.Node.LeafNode;

/**
 * JerryfishMerkleRestore
 */
public class JerryfishMerkleRestore {

    // 預還原的版本號碼
    public int version;


    /**
     * ChildInfo
     */
    public class ChildInfo {

        public boolean isLeaf;

        // 這個子節點是一個內部節點。 
        // 內部節點的哈希（如果已知）存儲在此處，否則為“無”。 
        // 在還原樹的過程中，只有在看到共享相同前綴的所有鍵之後，我們才知道內部節點的哈希。
        public byte[] hash;

        // 這個子節點是一個葉子節點
        public LeafNode node;

        // 是葉子節點
        public ChildInfo(LeafNode node){
            this.node = new LeafNode(node.getAccountKey(), node.getBlob());
            this.isLeaf = true;
        }
        // 內部節點
        public ChildInfo(byte[] hash){            
            this.hash = hash;
            this.isLeaf = false;
        }
        

        public Child into_child(int version) {
            if(isLeaf){
                // 是葉子節點
                return new Child(hash, version, isLeaf);
            }else{
                // 是內部節點
                return new Child(node.getHash(), version, isLeaf);
            }
        }
        
    }

    /**
     * InternalInfo
     */
    public class InternalInfo {

        // 此為內部節點的節點key
        NodeKey node_key;
        // 現有的子節點
        ArrayList<ChildInfo> children = new ArrayList<>();

        public void set_child(int index,ChildInfo child_info) {
            children.set(index, child_info);
        }

        // 假設自己的所有子節點都是已知的且已完全初始化，則將“ self”轉換為內部節點。
        public void into_internal_node(int version){
            
        }

        
    }
    
}