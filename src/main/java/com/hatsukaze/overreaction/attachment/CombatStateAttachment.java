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
    private String currentComboId = "";
    private int attackIndex = 0;
    private float attackTimer = 0f;
    private AttackDefinition currentAttackDef = null;
    //攻撃が当たった敵を配列で保存することによって、ヒットボックスが継続してでる攻撃でも一度のみになる。ディノバルドの大回転斬のガードみたいなのを防ぐ
    private final Set<UUID> hitEntities = new HashSet<>();

    // --- attackIndex ---
    public int getAttackIndex() { return attackIndex; }
    public void setAttackIndex(int index) { this.attackIndex = index; }

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

    // --- シリアライズ（ログアウト時の保存用） ---
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("comboId", currentComboId);
        tag.putInt("attackIndex", attackIndex);
        tag.putFloat("attackTimer", attackTimer);
        return tag;
    }

    public void load(CompoundTag tag) {
        this.currentComboId = tag.getString("comboId");
        this.attackIndex    = tag.getInt("attackIndex");
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