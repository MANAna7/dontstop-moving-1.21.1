package com.hatsukaze.overreaction.data;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ChargeDefinition {
    public final String chargingAnimationId;
    public final String releaseAnimationId;
    public final List<ChargeStage> chargeStages;

    private ChargeDefinition(String chargingAnimationId, String releaseAnimationId, List<ChargeStage> chargeStages) {
        this.chargingAnimationId = chargingAnimationId;
        this.releaseAnimationId = releaseAnimationId;
        this.chargeStages = chargeStages;
    }

    public static ChargeDefinition fromJson(JsonObject json) {
        List<ChargeStage> stages = new ArrayList<>();
        json.getAsJsonArray("chargeStages")
                .forEach(e -> stages.add(ChargeStage.fromJson(e.getAsJsonObject())));
        return new ChargeDefinition(
                json.get("chargingAnimationId").getAsString(),
                json.get("releaseAnimationId").getAsString(),
                stages
        );
    }
}