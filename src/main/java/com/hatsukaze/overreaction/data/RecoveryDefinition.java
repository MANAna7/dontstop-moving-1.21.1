package com.hatsukaze.overreaction.data;

import com.google.gson.JsonObject;

/**
後隙を管理するdataクラス
 **/
public class RecoveryDefinition {
    public final String animationId;
    public final float duration;
    public final boolean cancelable;

    private RecoveryDefinition(String animationId, float duration, boolean cancelable) {
        this.animationId = animationId;
        this.duration = duration;
        this.cancelable = cancelable;
    }

    public static RecoveryDefinition fromJson(JsonObject json) {
        return new RecoveryDefinition(
                json.get("animationId").getAsString(),
                json.get("duration").getAsFloat(),
                json.get("cancelable").getAsBoolean()
        );
    }
}