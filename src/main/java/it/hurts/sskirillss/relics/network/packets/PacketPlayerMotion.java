package it.hurts.sskirillss.relics.network.packets;

import io.netty.buffer.ByteBuf;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Data
@AllArgsConstructor
public class PacketPlayerMotion implements CustomPacketPayload {
    private final double motionX;
    private final double motionY;
    private final double motionZ;

    public static final CustomPacketPayload.Type<PacketPlayerMotion> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "player_motion"));

    public static final StreamCodec<ByteBuf, PacketPlayerMotion> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, PacketPlayerMotion::getMotionX,
            ByteBufCodecs.DOUBLE, PacketPlayerMotion::getMotionY,
            ByteBufCodecs.DOUBLE, PacketPlayerMotion::getMotionZ,
            PacketPlayerMotion::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Vec3 motion = new Vec3(this.motionX, this.motionY, this.motionZ);

            Minecraft.getInstance().player.setDeltaMovement(motion);
        });
    }
}