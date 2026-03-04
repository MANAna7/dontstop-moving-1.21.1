package com.hatsukaze.overreaction.data;

import com.google.gson.JsonObject;

/**
 * コンボツリーの1ノード。AttackDefinitionと左右クリックの遷移先を持つ。
 */
public class ComboNode {
    public final AttackDefinition attackDef;
    public final String leftClickNext;   // nullで終端（先頭に戻るならslash_1等を指定）
    public final String rightClickNext;  // nullで分岐なし


    private ComboNode(AttackDefinition attackDef, String leftClickNext, String rightClickNext) {
        this.attackDef = attackDef;
        this.leftClickNext = leftClickNext;
        this.rightClickNext = rightClickNext;
    }

    //Jsonから取得してComboNodeクラスに変換
    public static ComboNode fromJson(JsonObject json) {
        AttackDefinition attackDef = AttackDefinition.fromJson(json);

        String leftClickNext = json.has("leftClickNext") && !json.get("leftClickNext").isJsonNull()
                ? json.get("leftClickNext").getAsString() : null;

        String rightClickNext = json.has("rightClickNext") && !json.get("rightClickNext").isJsonNull()
                ? json.get("rightClickNext").getAsString() : null;

        return new ComboNode(attackDef, leftClickNext, rightClickNext);
    }
}