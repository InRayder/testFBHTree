package com.Ray.JMTree.Node;

import com.Ray.JMTree.NodeKey;

/**
 * NodeAndNodeKey
 */
public class NodeAndNodeKey {

    Node nowNode;
    NodeKey nowNodeKey;

    public NodeAndNodeKey(Node node,NodeKey nodeKey){
        this.nowNode = node;
        this.nowNodeKey = new NodeKey(nodeKey.getVersion(), nodeKey.getNibblePath());
    }

    public Node getNode(){
        return this.nowNode;
    }

    public NodeKey getNodeKey(){
        return this.nowNodeKey;
    }
    
}