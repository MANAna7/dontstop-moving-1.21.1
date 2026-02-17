// ComboDefinition.java
package com.hatsukaze.dontstopmoving.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *combo自体を管理するクラス。これをベースに、jsonからとってきたものをクラスに当てはめてcomboを作成する。
 **/
public class ComboDefinition {
    public final String id;
    private final List<AttackDefinition> attacks;

    private ComboDefinition(String id, List<AttackDefinition> attacks) {
        this.id = id;
        this.attacks = Collections.unmodifiableList(attacks);
    }

    public AttackDefinition getAttack(int index) {
        return attacks.get(index % attacks.size());
    }

    public int size() {
        return attacks.size();
    }

    public static ComboDefinition fromJson(JsonObject json) {
        String id = json.get("id").getAsString();
        JsonArray attacksJson = json.getAsJsonArray("attacks");

        List<AttackDefinition> attacks = new ArrayList<>();
        attacksJson.forEach(el -> attacks.add(
                AttackDefinition.fromJson(el.getAsJsonObject())
        ));

        return new ComboDefinition(id, attacks);
    }
}