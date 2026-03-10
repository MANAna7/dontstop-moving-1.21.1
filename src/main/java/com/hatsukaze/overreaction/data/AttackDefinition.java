// AttackDefinition.java
package com.hatsukaze.overreaction.data;

import com.google.gson.JsonObject;

/**
 * 現在何のcomboを持ち、comboの中でも一段ずつの状態を維持するためのデータクラス。一段ごとに管理する。
 **/
public class AttackDefinition {
    public final String id;
    public final String animationId;
    public final float damageMultiplier;
    public final float hitWindowStart;
    public final float hitWindowEnd;
    public final boolean playOnHit;
    public final float attackingTimeBase;
    public final float animationNaturalTime;

    // nullable
    public final HitStopDefinition hitStop;
    public final RecoveryDefinition recovery;
    public final ChargeDefinition charge;



    private AttackDefinition(String id, String animationId, float damageMultiplier,
                             float hitWindowStart, float hitWindowEnd, boolean playOnHit, float attackingTimeBase, float animationNaturalTime,
                             HitStopDefinition hitStop, RecoveryDefinition recovery,
                             ChargeDefinition charge) {
        this.id = id;
        this.animationId = animationId;
        this.damageMultiplier = damageMultiplier;
        this.hitWindowStart = hitWindowStart;
        this.hitWindowEnd = hitWindowEnd;
        this.playOnHit = playOnHit;
        this.attackingTimeBase = attackingTimeBase;
        this.animationNaturalTime = animationNaturalTime;
        this.hitStop = hitStop;
        this.recovery = recovery;
        this.charge = charge;
    }

    /**
     * jsonからデータを取得する
     * getting data in json
     */
    public static AttackDefinition fromJson(JsonObject json) {
        String id           = json.get("id").getAsString();
        String animationId  = json.get("animationId").getAsString();
        float dmgMult       = json.get("damageMultiplier").getAsFloat();
        //もしもなかった場合に備えてデフォルト値1.0f
        float atkTimeBase  = json.has("attackingTimeBase") ? json.get("attackingTimeBase").getAsFloat() : 1.0f;
        float animationNaturalTime = json.has("animationNaturalTime") ? json.get("animationNaturalTime").getAsFloat() : 1.0f;

        // hitWindowがnullの場合
        float hwStart = 0f;
        float hwEnd = 0f;
        if (json.has("hitWindow") && !json.get("hitWindow").isJsonNull()) {
            JsonObject hw = json.getAsJsonObject("hitWindow");
            hwStart = hw.get("start").getAsFloat();
            hwEnd = hw.get("end").getAsFloat();
        }
        boolean playOnHit   = json.get("playOnHit").getAsBoolean();

        HitStopDefinition hitStop = (json.has("hitStop") && !json.get("hitStop").isJsonNull())
                ? HitStopDefinition.fromJson(json.getAsJsonObject("hitStop")) : null;
        RecoveryDefinition recovery = (json.has("recovery") && !json.get("recovery").isJsonNull())
                ? RecoveryDefinition.fromJson(json.getAsJsonObject("recovery")) : null;
        ChargeDefinition charge = (json.has("charge") && !json.get("charge").isJsonNull())
                ? ChargeDefinition.fromJson(json.getAsJsonObject("charge")) : null;

        return new AttackDefinition(id, animationId, dmgMult, hwStart, hwEnd,
                playOnHit, atkTimeBase, animationNaturalTime, hitStop, recovery, charge);
    }
}