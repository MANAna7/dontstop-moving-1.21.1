// AttackDefinition.java
package com.hatsukaze.dontstopmoving.data;

import com.google.gson.JsonObject;

/**
 * 現在何のcomboを持ち、comboの中でも一段ずつの状態を維持するためのデータクラス。一段ごとに管理する。
 **/
public class AttackDefinition {
    public final String id;
    public final String animationId;
    public final float damageMultiplier;
    public final float cooldown;
    public final float hitWindowStart;
    public final float hitWindowEnd;
    // hitboxは今は省略、後で追加

    private AttackDefinition(String id, String animationId,
                             float damageMultiplier, float cooldown,
                             float hitWindowStart, float hitWindowEnd) {
        this.id = id;
        this.animationId = animationId;
        this.damageMultiplier = damageMultiplier;
        this.cooldown = cooldown;
        this.hitWindowStart = hitWindowStart;
        this.hitWindowEnd = hitWindowEnd;
    }

    /**
     * jsonからデータを取得する
     * getting data in json
     */
    public static AttackDefinition fromJson(JsonObject json) {
        String id            = json.get("id").getAsString();
        String animation     = json.get("animation").getAsString();
        float dmgMult        = json.get("damage_multiplier").getAsFloat();
        float cooldown       = json.get("cooldown").getAsFloat();
        JsonObject hw        = json.getAsJsonObject("hit_window");
        float hwStart        = hw.get("start").getAsFloat();
        float hwEnd          = hw.get("end").getAsFloat();

        return new AttackDefinition(id, animation, dmgMult, cooldown, hwStart, hwEnd);
    }
}