// ComboDefinition.java
package com.hatsukaze.overreaction.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * コンボ全体を管理するクラス。配列からMap<String, ComboNode>のツリー構造に変更。
 */
public class ComboDefinition {
    public final String id;
    public final String entryNode;  // コンボ開始ノードのID
    private final Map<String, ComboNode> nodes;

    private ComboDefinition(String id, String entryNode, Map<String, ComboNode> nodes) {
        this.id = id;
        this.entryNode = entryNode;
        this.nodes = Collections.unmodifiableMap(nodes);
    }

    /** IDでノードを取得。存在しなければnull */
    public ComboNode getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    /** エントリーノードを取得 */
    public ComboNode getEntryNode() {
        return nodes.get(entryNode);
    }

    public static ComboDefinition fromJson(JsonObject json) {
        String id = json.get("id").getAsString();
        // entryNodeの指定がなければnodesの最初のノードをエントリーにする
        JsonArray nodesJson = json.getAsJsonArray("nodes");

        Map<String, ComboNode> nodes = new LinkedHashMap<>();
        nodesJson.forEach(el -> {
            JsonObject nodeJson = el.getAsJsonObject();
            ComboNode node = ComboNode.fromJson(nodeJson);
            nodes.put(node.attackDef.id, node);
        });

        String entryNode = json.has("entryNode")
                ? json.get("entryNode").getAsString()
                : nodes.keySet().iterator().next(); // 最初のノードをデフォルトに

        return new ComboDefinition(id, entryNode, nodes);
    }
}