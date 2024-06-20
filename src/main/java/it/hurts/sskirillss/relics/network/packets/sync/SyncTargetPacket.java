package it.hurts.sskirillss.relics.network.packets.sync;

import it.hurts.sskirillss.relics.entities.misc.ITargetableEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncTargetPacket {
    private final int targeterId;
    private final int targetId;

    public SyncTargetPacket(FriendlyByteBuf buf) {
        targeterId = buf.readInt();
        targetId = buf.readInt();
    }

    public SyncTargetPacket(int targeterId, int targetId) {
        this.targeterId = targeterId;
        this.targetId = targetId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(targeterId);
        buf.writeInt(targetId);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = Minecraft.getInstance().player.level();

            if (level.getEntity(targeterId) instanceof ITargetableEntity targeter && level.getEntity(targetId) instanceof LivingEntity target)
                targeter.setTarget(target);
        });

        return true;
    }
}