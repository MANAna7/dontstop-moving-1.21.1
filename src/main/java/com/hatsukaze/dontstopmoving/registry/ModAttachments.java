package com.hatsukaze.dontstopmoving.registry;

import com.hatsukaze.dontstopmoving.attachment.CombatStateAttachment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

import static com.hatsukaze.dontstopmoving.ExampleMod.MODID;

/**
 *unityのFixedUpdateみたいな感じで、定期的に走る部分に作ったものを登録する処理
 *
 **/
public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
//Supplier　必要な時に生成して登録する。常時ってよりかは登録するときに一度だけ…みたいな
    public static final Supplier<AttachmentType<CombatStateAttachment>> COMBAT_STATE =
            ATTACHMENT_TYPES.register("combat_state", () ->
                    AttachmentType.serializable(() -> new CombatStateAttachment())
                                    .build()
                    // メモ：CompoundTagって形式でCombatStateAttachmentっていうアイテムを出し入れするよ～(これがジェネリクス記法)
                    // new AttachmentType.IAttachmentSerializer<net.minecraft.nbt.CompoundTag, CombatStateAttachment>() {
                    //ただ旧式の書き方なので実際は使わない
            );
}