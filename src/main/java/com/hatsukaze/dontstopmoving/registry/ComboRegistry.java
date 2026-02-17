// ComboRegistry.java
package com.hatsukaze.dontstopmoving.registry;

import com.hatsukaze.dontstopmoving.data.ComboDefinition;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *CombosにComboDefinitionで作成していったクラスを登録していく.<br>
 * <Param>登録:register(ComboDefinition)<Param/><br>
 * <Param>取得:get(String id)<Param/><br>
 * <Param>リセット:clear()<Param/><br>
 *
 **/
public class ComboRegistry {
    private static final Map<String, ComboDefinition> COMBOS = new HashMap<>();

    public static void register(ComboDefinition combo) {
        COMBOS.put(combo.id, combo);
    }

    public static ComboDefinition get(String id) {
        return COMBOS.get(id);
    }

    public static void clear() {
        COMBOS.clear();
    }

    // デバッグ用
    public static Map<String, ComboDefinition> getAll() {
        return Collections.unmodifiableMap(COMBOS);
    }
}