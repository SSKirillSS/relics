package it.hurts.sskirillss.relics.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncEntityEffects {
    private final CompoundTag data;
    private final Action action;
    private final int entity;

    public PacketSyncEntityEffects(FriendlyByteBuf buf) {
        entity = buf.readInt();
        data = buf.readNbt();
        action = buf.readEnum(Action.class);
    }

    public PacketSyncEntityEffects(int entity, CompoundTag data, Action action) {
        this.entity = entity;
        this.data = data;
        this.action = action;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entity);
        buf.writeNbt(data);
        buf.writeEnum(action);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft MC = Minecraft.getInstance();
            ClientLevel level = MC.level;

            if (level == null || !(level.getEntity(this.entity) instanceof LivingEntity entity))
                return;

            MobEffectInstance effect = MobEffectInstance.load(data);

            if (effect != null) {
                if (action == Action.ADD)
                    entity.addEffect(effect);
                else if (action == Action.REMOVE)
                    entity.removeEffect(effect.getEffect());
            }
        });

        return true;
    }

    public enum Action {
        ADD,
        REMOVE
    }
}