package com.hatsukaze.overreaction.data;

import com.google.gson.JsonObject;

public class ChargeStage {
    public final float duration;
    public final float damageMultiplier;
    public final String animationId;

    private ChargeStage(float duration, float damageMultiplier, String animationId) {
        this.duration = duration;
        this.damageMultiplier = damageMultiplier;
        this.animationId = animationId;
    }

    public static ChargeStage fromJson(JsonObject json) {
        return new ChargeStage(
                json.get("duration").getAsFloat(),
                json.get("damageMultiplier").getAsFloat(),
                json.get("animationId").getAsString()
        );
    }
}