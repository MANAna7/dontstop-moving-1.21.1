// ComboReloadListener.java
package com.hatsukaze.overreaction.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hatsukaze.overreaction.registry.ComboRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import java.util.Map;

/**
*combo用jsonからcombo情報を取得する。さらに、取得できたかどうかDebugLogで出力する
*
 **/
public class ComboReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    //GSON - googleが提供するjsonとjavaオブジェクトを相互変換するためのクラス。ライブラリ
    private static final Gson GSON = new Gson();

    public ComboReloadListener() {
        // "combos"フォルダを監視する
        super(GSON, "combos");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ComboRegistry.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            try {
                ComboDefinition combo = ComboDefinition.fromJson(entry.getValue().getAsJsonObject());
                ComboRegistry.register(combo);
                LOGGER.debug("Loaded combo: {}", combo.id);
            } catch (Exception e) {
                LOGGER.error("Failed to load combo {}: {}", entry.getKey(), e.getMessage());
            }
        }

        LOGGER.info("[DontStopMoving] Loaded {} combo(s)", ComboRegistry.getAll().size());
    }

}