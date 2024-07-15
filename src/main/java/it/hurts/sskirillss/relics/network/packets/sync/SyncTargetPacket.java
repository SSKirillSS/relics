package it.hurts.sskirillss.relics.network.packets.sync;

import io.netty.buffer.ByteBuf;
import it.hurts.sskirillss.relics.entities.misc.ITargetableEntity;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Data
@AllArgsConstructor
public class SyncTargetPacket implements CustomPacketPayload {
    private final int targeterId;
    private final int targetId;

    public static final CustomPacketPayload.Type<SyncTargetPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "target_path"));

    public static final StreamCodec<ByteBuf, SyncTargetPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncTargetPacket::getTargetId,
            ByteBufCodecs.INT, SyncTargetPacket::getTargeterId,
            SyncTargetPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = Minecraft.getInstance().player.level();
            if (level.getEntity(targetId) instanceof ITargetableEntity targeter && level.getEntity(targeterId) instanceof LivingEntity target) {
                targeter.setTarget(target);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}