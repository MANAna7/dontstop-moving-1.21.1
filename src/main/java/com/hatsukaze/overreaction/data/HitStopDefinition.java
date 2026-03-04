package com.hatsukaze.overreaction.data;

import com.google.gson.JsonObject;

/**
 後隙を管理するdataクラス
 **/
public class HitStopDefinition {
    public final float duration;

    private HitStopDefinition(float duration) {
        this.duration = duration;
    }

    public static HitStopDefinition fromJson(JsonObject json) {
        return new HitStopDefinition(
                json.get("duration").getAsFloat()
        );
    }
}