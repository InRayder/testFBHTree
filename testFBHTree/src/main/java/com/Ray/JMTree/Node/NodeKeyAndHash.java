package com.Ray.JMTree.Node;

import java.util.Stack;

import com.Ray.JMTree.NodeKey;

/**
 * NodeKeyAndHash
 */
public class NodeKeyAndHash {

    NodeKey nodeKey;
    Stack<byte[]> hash = new Stack<byte[]>();


    public NodeKeyAndHash(NodeKey nodeKey,Stack<byte[]> hash) {
        this.nodeKey = nodeKey;
        this.hash = (Stack<byte[]>)hash.clone();
        
    }

    public NodeKey getNodeKey(){
        return this.nodeKey;
    }
    
    public Stack<byte[]> getHash(){
        return this.hash;
    }
}