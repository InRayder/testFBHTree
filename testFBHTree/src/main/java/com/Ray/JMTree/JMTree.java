package com.Ray.JMTree;

/**
 * @author InRay
 */
public class JMTree {

    public JMTree(){


    }

    /**
     * 應用`blob_set`後，批量返回新的節點和帳戶狀態Blob。 例如，如果在事務“ T_i”之後持久化存儲中樹的提交狀態看起來像以下結構：
     * 
     * ```text
     *               S_i            <br>
     *              /   \           <br>
     *             .     .          <br>
     *            .       .         <br>
     *           /         \        <br>
     *          o           x       <br>
     *         / \                  <br>
     *        A   B                 <br>
     *         storage (disk)       <br>
     * ```
     * 其中，“ A”和“ B”表示兩個相鄰帳戶的狀態，“ x”是樹中從根到A和B的路徑的同級子樹。 <br>
     * 然後由下一個事務“ T_ {i + 1}”產生的“ blob_set”修改了在“ x”下子樹中存在的其他帳戶“ C”和“ D”， <br>
     * 將在內存中構造一個新的局部樹，該結構將 是： <br>
     * 
     *  ```text
     *                  S_i      |      S_{i+1}
     *                 /   \     |     /       \
     *                .     .    |    .         .
     *               .       .   |   .           .
     *              /         \  |  /             \
     *             /           x | /               x'
     *            o<-------------+-               / \
     *           / \             |               C   D
     *          A   B            |
     *            storage (disk) |    cache (memory)
     *  ```
     * 
     * 通過這種設計，我們能夠查詢持久性存儲中的全局狀態，並基於特定的根哈希和“ blob_set”生成建議的樹增量。 <br>
     * 例如，如果我們要執行另一個事務“ T_ {i + 1}”，則可以在存儲中使用樹“ S_i”，並應用事務“ T_ {i + 1}”的“ blob_set”。 <br>
     * 然後，如果存儲提交了返回的批處理，則可以通過調用[`get_with_proof`]（struct.JellyfishMerkleTree.html＃method.get_with_proof <br>
     * 從樹中讀取狀態“ S_ {i + 1}”。 批處理中的任何內容在提交之前都無法通過公共接口訪問。 <br>
     * 
     * 
     * return hash, TreeUpdateBatch
     */
    public void putBlobSet(){


    }
    

} 