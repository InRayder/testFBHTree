package com.Ray.SMTree;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.Ray.Libra.LedgerState.AccountResource;
import com.Ray.Utils.HashUtils;

/**
 * (Libra)SMTree - SparseMerkleTree Libra中用的稀疏摩克樹
 */
public class SMTree implements Serializable {

    // private LinkedHashMap<Integer, Node> nodes = new LinkedHashMap<>();
    public LinkedList<Node> nodes = new LinkedList<>();
    private Node rootNode = new Node(); // 預設 root
    private final Node emptyNode = new Node().defEmptyNode(); // 預設空節點

    public static void main(String[] args) {
        // String[] accounts = { "0100", "1000", "1011" };
        // byte[] bb = HashUtils.hex2byte(accounts[0]);
        // System.out.println(bb);
        // System.out.println(bb.toString());

        String[] accounts = { "4474aa4d1d9428df98d385f92dcb6e09ca41b65a6f491fcc041a211cbe32a2f6",
                "b383b6b5ca621880e1a779a27555eea92b3acf3a1a9425f602dea813621bb57a",
                "6a6fbe0b7ad1a8dc90022a44450f5085fe605ab7e883b5baeb8265acd65411b5",
                "ed6dade59b7e8e92a2b694e809a89567d610a431fea6a856858483851e7b3b1e",
                "e696c499fbc717d3a2e9adac0339a14acef2c673f99abd4d30287a4942602b4d",
                "00161b256da7f6bf3393c028dd855522b3a45abd46e065d976e5d655a29a4550" };
        // String[] accounts = { "0","1","2","3","4"};
        SMTree smt = new SMTree();
        AccountResource ar_0 = new AccountResource(accounts[0]);
        AccountResource ar_1 = new AccountResource(accounts[1]);
        AccountResource ar_2 = new AccountResource(accounts[2]);
        AccountResource ar_3 = new AccountResource(accounts[3]);
        AccountResource ar_4 = new AccountResource(accounts[4]);
        AccountResource ar_5 = new AccountResource(accounts[5]);
        smt.viewAll();
        smt.put(ar_0.getAuthentication_key(), HashUtils.hex2byte(ar_0.getRowData()));
        smt.viewAll();
        // ar_0.doMintLibra(100);
        // smt.put(ar_0.getAuthentication_key(), HashUtils.hex2byte(ar_0.getRowData()));
        // smt.viewAll();
        smt.put(ar_1.getAuthentication_key(), HashUtils.hex2byte(ar_1.getRowData()));
        smt.viewAll();
        smt.findDepth();
        smt.put(ar_2.getAuthentication_key(), HashUtils.hex2byte(ar_2.getRowData()));
        smt.viewAll();
        smt.put(ar_3.getAuthentication_key(), HashUtils.hex2byte(ar_3.getRowData()));
        smt.viewAll();
        smt.put(ar_4.getAuthentication_key(), HashUtils.hex2byte(ar_4.getRowData()));
        smt.viewAll();
        smt.put(ar_5.getAuthentication_key(), HashUtils.hex2byte(ar_5.getRowData()));
        smt.viewAll();
        // System.out.println("rootHash:"+HashUtils.byte2hex(smt.getRootHash()));
        // System.out.println("account:"+accounts[4]);
        // System.out.println("HashValue:"+HashUtils.byte2hex(smt.get(accounts[4])));

        smt.findDepth();
    }

    public SMTree() {
        // 初始化
        emptyNode.setId(0); //預設空節點
        nodes.add(0, emptyNode); 
        nodes.add(1, rootNode);
    }

    // 測試用
    public void viewAll() {
        System.out.println("-----------------------");
        for (Node n : nodes) {
            System.out
                    .println("id:" + n.id + ", nodeType:" + n.nodeType + ", HashValue:" + n.getContentDigestHexString()
                            + ", leftChildId:" + n.leftChildId + ", rightChildID:" + n.rightChildId);
            if (n.nodeType == 1) {
                System.out.println("\t\t∟Account:" + n.account);
            }
        }
        System.out.println("-----------------------");
    }

    // 算出目前SMT的平均深度和最深深度
    public void findDepth() {
        int maxDepth = 0;
        int totalDepth = 0;
        int totalLeaf = 0;
        int depth = 0;
        Node nowNode = rootNode;
        Stack<Node> pathNode = new Stack<>();
        LinkedHashMap<Integer, Integer> pathDepth = new LinkedHashMap<>();

        pathNode.push(nowNode);
        pathDepth.put(nowNode.id, depth);
        while (!pathNode.isEmpty()) {
            nowNode = pathNode.pop();
            if (nowNode.id != 0) {
                // System.out.println("目前節點:" + nowNode.id);
                depth = pathDepth.get(nowNode.id);
                // System.out.println("目前深度:" + depth);

                if (nowNode.leftChildId != -1) { // 有子節點
                    // System.out.println(nowNode.id + "有子節點");
                    depth++;
                    pathNode.push(nowNode.leftChild);
                    pathNode.push(nowNode.rightChild);
                    pathDepth.put(nowNode.leftChildId, depth);
                    pathDepth.put(nowNode.rightChildId, depth);

                } else {// 沒有子節點
                    totalLeaf++;
                    // System.out.println(nowNode.id + "沒有子節點");
                    totalDepth += depth;
                    if (maxDepth < depth) {
                        maxDepth = depth;
                    }
                }
            }

            // System.out.println();
        }
        System.out.println("總結點數 : " +nodes.size());
        System.out.println("最深深度為 : " + maxDepth);
        System.out.println("平均深度為 : " + (double) totalDepth / totalLeaf);
    }

    /**
     * 獲取這key的哈希值
     * 
     * @param key 輸入key
     * @return 回傳key的哈希值
     */
    public byte[] get(String key) {
        return nodes.get(getNodeId(key)).getContentDigest();
    }

    /**
     * 
     * @param key
     * @return 回傳key在這SMT中對應的node ID
     */
    public int getNodeId(String key) {
        List<Stack<Integer>> path = findPath(key);
        Stack<Integer> pathNode = path.get(0); // 路徑節點
        return pathNode.pop();
    }

    /**
     * 確認指定的 account 有被包含在樹中
     * 
     * @param key
     * @return
     */
    public boolean contains(String key) {
        List<Stack<Integer>> path = findPath(key);
        Stack<Integer> pathNode = path.get(0); // 路徑節點
        return nodes.get(pathNode.pop()).account.equals(key);
    }

    /**
     * 返回RootHash
     * 
     * @return
     */
    public byte[] getRootHash() {
        return nodes.get(1).getContentDigest();
    }

    /**
     * 透過 account 紀錄路徑上的節點與兄弟節點
     * 
     * @param key
     * @return
     */
    public List<Stack<Integer>> findPath(String key) {
        String key_bin = HashUtils.hex2bin(key, true);

        Stack<Integer> pathNode = new Stack<>(); // 路徑節點
        Stack<Integer> siblingNode = new Stack<>(); // 兄弟節點
        Stack<Integer> isRight = new Stack<>(); // pathNode在左子樹(0)，在右子樹(1)
        Stack<Integer> isExisting = new Stack<>(); // 若該節點已經存在(1)，則直接覆蓋
        List<Stack<Integer>> path = new ArrayList<>();

        Node now_node = rootNode;
        pathNode.push(1);
        int i = 0;
        // System.out.println("(findPath)now_node.id:" + now_node.id);
        while (now_node.leftChildId != -1) {// 由上至下搜尋
            if (key_bin.charAt(i++) == '0') { // 目標在左子樹(L)，所以抓他的兄弟節點，在右子樹
                pathNode.push(now_node.leftChildId);
                siblingNode.push(now_node.rightChildId);
                isRight.push(0);
                now_node = now_node.leftChild;
            } else { // 右子樹(R)
                pathNode.push(now_node.rightChildId);
                siblingNode.push(now_node.leftChildId);
                isRight.push(1);
                now_node = now_node.rightChild;
            }
            // System.out.println("(findPath)now_node.id:" + now_node.id);
        }
        String n_account = now_node.account != null ? now_node.account : "";

        if (n_account.equals(key)) {// 之前已經放過了，這次作更新
            // System.out.println("已存在");
            isExisting.push(1);
        } else {
            // System.out.println("不存在");
            isExisting.push(0);
        }
        path.add(pathNode);
        path.add(siblingNode);
        path.add(isRight);
        path.add(isExisting);
        return path;
    }

    /**
     * 將 AccountResource 放入節點，並更新樹
     * 
     * @param ar
     * @return
     */
    public Node put(String key, byte[] digestValue) {
        Node new_root = new Node();
        Node updataNode = null;

        List<Stack<Integer>> path = findPath(key);
        Stack<Integer> pathNode = path.get(0);
        Stack<Integer> siblingNode = path.get(1);
        Stack<Integer> isRight = path.get(2); // pathNode在左子樹(0)，在右子樹(1)
        Stack<Integer> isExisting = path.get(3); // 若該節點已經存在(1)，則直接覆蓋

        int depth = pathNode.size() - 1;

        Node newNode = null;
        if (isExisting.pop() == 1) { // 已經存在，不需更新路徑
            // updataNode = nodes.get(getNodeId(key));
            updataNode = nodes.get(pathNode.pop());
            updataNode.put(digestValue);
            updateNodes(updataNode);
        } else { // 尚未建立在SMT上，需要建立Node
            while (!pathNode.empty()) {
                int nowNodeId = pathNode.pop();
                Node nowNode = nodes.get(nowNodeId);

                // System.out.println("now_account:" + now_node.account);
                // System.out.println("now_account.id:" + now_nodeId);

                if (nowNode.nodeType == 3 || nowNode.nodeType == 1) { // 為底(外)層的外部節點
                    // 新增一個 node
                    newNode = new Node().defLeafNode(key);
                    newNode.put(digestValue);
                    updateNodes(newNode);
                    if (nowNode.nodeType == 1) { // 原位置已存在node，無法直接覆蓋
                        // 計算extensionSubTree
                        newNode = extensionSubTree(depth, nowNode, newNode);
                    }
                    updateNodes(newNode);
                } else {// 為內部節點
                    Node leftChild;
                    Node rightChild;

                    if (isRight.pop() == 0) { // path在左子樹
                        leftChild = newNode;
                        rightChild = nodes.get(siblingNode.pop());
                    } else {
                        leftChild = nodes.get(siblingNode.pop());
                        rightChild = newNode;
                    }
                    if (nowNodeId == 1) {// 處理root
                        // System.out.println("更新root層");
                        rootNode.setleftChild(leftChild);
                        rootNode.setrightChild(rightChild);
                        updateNodes(rootNode);
                    } else {
                        // System.out.println("更新內部節點");
                        newNode = nowNode;
                        newNode.setleftChild(leftChild);// 更新節點
                        newNode.setrightChild(rightChild);// 更新節點

                        // newNode = new Node("", leftChild, rightChild, 0);//新增節點
                        updateNodes(newNode);
                    }

                }
                depth--;
            }
        }

        return null;
    }

    
    private void updateNodes(Node node) {
        if (node.id == -1) {// 未設定id(做新增的動作)
            node.setId(nodes.size());
            nodes.add(node);
        } else { // 做更新的動作
            int id = node.id;
            nodes.remove(id);
            nodes.add(id, node);
            // System.out.println("new id:"+id);
        }
    }

    private Node extensionSubTree(int depth, Node existingNode, Node newNode) {
        String existingNode_bin = HashUtils.hex2bin(existingNode.account, true);
        String newNode_bin = HashUtils.hex2bin(newNode.account, true);
        int cpl = commonPrefixLen(existingNode_bin, newNode_bin);
        int extension_len = cpl - depth;

        // System.out.println("cpl:" + cpl);
        // System.out.println("depth:" + depth);
        // System.out.println("extension_len:" + extension_len);

        String extensionCommonPrefix = (String) (existingNode_bin).subSequence(depth, cpl + 1);

        Node leftChild;
        Node rightChild;

        Node internalNode = null;

        // System.out.println("extensionCommonPrefix:" + extensionCommonPrefix);

        for (int i = extensionCommonPrefix.length() - 1; i >= 0; i--) {
            // System.out.println("i:" + i);
            // System.out.println("charAt(i):"+existingNode_bin.charAt(i));
            if (extensionCommonPrefix.charAt(i) == '0') {// 原本已存在的leaf是在左子樹
                if (i == extensionCommonPrefix.length() - 1) {// 計算最下層時
                    // System.out.println("do1");
                    leftChild = existingNode;
                    rightChild = newNode;
                } else {
                    // System.out.println("do2");
                    leftChild = internalNode;
                    rightChild = emptyNode;
                }

            } else {// 原本已存在的leaf是在右子樹
                if (i == extensionCommonPrefix.length() - 1) {// 計算最下層時
                    // System.out.println("do3");
                    leftChild = newNode;
                    rightChild = existingNode;
                } else {
                    // System.out.println("do4");
                    leftChild = emptyNode;
                    rightChild = internalNode;
                }

            }

            internalNode = new Node("", leftChild, rightChild, 0);
            updateNodes(internalNode);
        }

        return internalNode;
    }

    /**
     * 計算共同前綴長度，需轉乘2進制
     * 
     * @param account1
     * @param account2
     * @return
     */
    private int commonPrefixLen(String account1_bin, String account2_bin) {
        int count = 0;
        if (account1_bin.length() == account2_bin.length()) {
            for (int i = 0; i < account1_bin.length(); i++) {
                if (account1_bin.charAt(i) == account2_bin.charAt(i)) {
                    count++;
                } else {
                    break;
                }
            }
        } else {
            // 錯誤
        }

        return count;
    }

    /**
     * 四種節點：
     * <ul>
     * <li><b>“InternalNode”</b>是一個有兩個子節點的節點。 它與標準Merkle樹中的內部節點相同。</li>
     * <li><b>“LeafNode”</b>代表一個帳戶。
     * 與存儲中的內容類似，葉節點具有Key，該Key是帳戶地址的散列以及作為相應帳戶blob的散列的值散列。
     * 不同之處在於，在將葉子作為非包含證明的一部分加載到內存中的情況下，<b>`LeafNode`<\b>並不總是具有該值。</li>
     * <li><b>“SubtreeNode”</b>表示具有一個或多個葉子的子樹。
     * 當我們從帶有證據的存儲中獲取帳戶時，會生成<b>`SubtreeNode`</b>。 它存儲此子樹的根哈希。</li>
     * <li><b>“EmptyNode”</b>表示零葉子的空子樹。</li> </ui>
     * 
     */
    private static class Node implements Serializable {

        private int id;

        private byte[] contentDigest;
        private String contentDigestHexStr;

        private String account = null;

        /**
         * Id 若為 -1 表示為 null Id 若為 0 表示為 root node 其他情況則為一般節點
         */
        int leftChildId;
        int rightChildId;
        private Node leftChild;
        private Node rightChild;

        /**
         * 0.“InternalNode” <br>
         * 1.“LeafNode” (存有 account blob的節點) <br>
         * 2.“SubtreeNode” <br>
         * 3.“EmptyNode” (空的葉子節點) <br>
         */
        private final int nodeType;
        // public enum nodeType {
        // InternalNode, LeafNode, SubtreeNode, EmptyNode
        // }

        // 預設root節點(root)
        public Node() {
            this.id = 1;
            this.leftChild = defEmptyNode();
            this.rightChild = defEmptyNode();
            this.rightChildId = 0; // id = 0,表示空節點
            this.leftChildId = 0;
            this.nodeType = 0;
            this.contentDigest = HashUtils.sha256(leftChild.getContentDigest(), rightChild.getContentDigest());
        }

        // 預設內部節點
        public Node(String account, Node leftChild, Node rightChild, int nodeType) {
            this.id = -1;
            this.account = account;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.nodeType = nodeType;

            if (this.nodeType == 0) {// 為內部節點
                this.contentDigest = HashUtils.sha256(leftChild.getContentDigest(), rightChild.getContentDigest());
                this.leftChildId = leftChild.id;
                this.rightChildId = rightChild.id;
            } else { // 為外部節點(葉子節點或空節點)
                this.contentDigest = new byte[32];
                this.leftChildId = -1;
                this.rightChildId = -1;
            }
        }

        // 預設葉子節點
        public Node defLeafNode(String account) {
            return new Node(account, null, null, 1);
        }

        // 預設空節點
        public Node defEmptyNode() {
            Node n = new Node(null, null, null, 3);
            try {
                n.put(HashUtils.hex2byte(HashUtils.sha256("0")));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return n;
        }

        // 設定node id
        public void setId(int id) {
            this.id = id;
        }

        // 取得node id
        public int getId() {
            if (leftChild == null || rightChild == null) {
                return -1;
            } else {
                return this.id;
            }
        }

        public void setleftChild(Node leftChild) {
            this.leftChild = leftChild;
            this.leftChildId = leftChild.id;
        }

        public void setrightChild(Node rightChild) {
            this.rightChild = rightChild;
            this.rightChildId = rightChild.id;
        }

        public void put(byte[] bytes) {
            this.contentDigest = bytes;
        }

        /**
         * (byte-計算用的)要求 node的 hashValue.
         * 
         * @return 返回該 node 的 hashValue
         */
        public byte[] getContentDigest() {
            updateContentDigest();
            return contentDigest;
        }

        /**
         * (String-給人看的)要求 node的 hashValue.
         * 
         * @return 返回該 node 的 hashValue
         */
        public String getContentDigestHexString() {
            updateContentDigest();
            return contentDigestHexStr;
        }

        private void updateContentDigest() {

            if (nodeType == 1 || nodeType == 3) { // 葉子節點

            } else { // 內部節點
                contentDigest = HashUtils.sha256(leftChild.getContentDigest(), rightChild.getContentDigest());
            }
            contentDigestHexStr = HashUtils.byte2hex(contentDigest);
        }
    }

}