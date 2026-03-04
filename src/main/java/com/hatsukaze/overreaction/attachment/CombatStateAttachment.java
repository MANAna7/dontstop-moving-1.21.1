package com.hatsukaze.overreaction.attachment;

import com.hatsukaze.overreaction.data.AttackDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//NBT(セーブ&ロードできるよ～ってimplementsで宣言、インターフェースを宣言して実装してるよ～ってする)
/**
 *現在の攻撃の状態(state)を管理する。今回の攻撃でもう誰にヒットしたか?いまどこか？今何のコンボか？Timerは？みたいなものを管理する。これを作ることによってクライアントサイドと紐づけ、動作する
 *
 **/
public class CombatStateAttachment implements INBTSerializable<CompoundTag> {

    //戦闘のフェーズ。フェーズにわけてモーションを管理、操作していく
    public enum CombatPhase {
        IDLE,
        ATTACKING,
        HIT_STOP,
        RECOVERY,
        COMBO_WINDOW
    }

    private CombatPhase phase = CombatPhase.IDLE;
    private String currentNodeId = null; // nullはIDLE（コンボ未開始）

    private float attackTimer = 0f;
    private AttackDefinition currentAttackDef = null;
    //攻撃が当たった敵を配列で保存することによって、ヒットボックスが継続してでる攻撃でも一度のみになる。ディノバルドの大回転斬のガードみたいなのを防ぐ
    private final Set<UUID> hitEntities = new HashSet<>();

    private float totalAttackingTime = 0f;

    private boolean animationPlayed = false;

    //この辺はコンボ後に先行入力とか入力待ちを作るための処理
    private boolean bufferedInput = false;
    private float comboWindowTimer = -1f; // -1 = ウィンドウ非アクティブ



    public boolean hasBufferedInput() { return bufferedInput; }
    public void setBufferedInput(boolean b) { this.bufferedInput = b; }
    public float getComboWindowTimer() { return comboWindowTimer; }
    public void setComboWindowTimer(float t) { this.comboWindowTimer = t; }

    // --- phase ---
    public CombatPhase getPhase() { return phase; }
    public void setPhase(CombatPhase phase) { this.phase = phase;}

    // --- currentNodeId ---
    public String getCurrentNodeId() { return currentNodeId; }
    public void setCurrentNodeId(String nodeId) { this.currentNodeId = nodeId; }

    // --- hitFlg ---
    public boolean hasAnimationPlayed() { return animationPlayed; }
    public void setAnimationPlayed(boolean played) { this.animationPlayed = played; }

    // --- attackTimer ---
    public float getAttackTimer() { return attackTimer; }
    public void setAttackTimer(float timer) { this.attackTimer = timer; }

    // --- currentAttackDef ---
    public AttackDefinition getCurrentAttackDef() { return currentAttackDef; }
    public void setCurrentAttackDef(AttackDefinition def) {
        this.currentAttackDef = def;
        this.hitEntities.clear(); // 攻撃切り替わったらヒット済みリセット
    }

    // --- hitEntities ---
    public boolean hasHit(UUID uuid) { return hitEntities.contains(uuid); }
    public void addHitEntity(UUID uuid) { hitEntities.add(uuid); }

    // --- AttackingTimes ---
    public float getTotalAttackingTime() { return totalAttackingTime; }
    public void setTotalAttackingTime(float t) { this.totalAttackingTime = t; }

    // --- シリアライズ（ログアウト時の保存用） ---
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("currentNodeId", currentNodeId != null ? currentNodeId : "");
        tag.putFloat("attackTimer", attackTimer);
        return tag;
    }

    public void load(CompoundTag tag) {
        String nodeId = tag.getString("currentNodeId");
        this.currentNodeId = nodeId.isEmpty() ? null : nodeId;
        this.attackTimer    = tag.getFloat("attackTimer");
        // currentAttackDefはランタイムのみ、保存不要
    }

    //serializableにSupplierだけを渡す形式の場合はシリアライズの方法をAttachment側に持たせる必要がある
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return save();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        load(tag);
    }
}