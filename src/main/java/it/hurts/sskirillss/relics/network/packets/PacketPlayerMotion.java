package it.hurts.sskirillss.relics.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPlayerMotion {
    private final double motionX;
    private final double motionY;
    private final double motionZ;

    public PacketPlayerMotion(FriendlyByteBuf buf) {
        motionX = buf.readDouble();
        motionY = buf.readDouble();
        motionZ = buf.readDouble();
    }

    public PacketPlayerMotion(double motionX, double motionY, double motionZ) {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(motionX);
        buf.writeDouble(motionY);
        buf.writeDouble(motionZ);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Vec3 motion = new Vec3(this.motionX, this.motionY, this.motionZ);

            Minecraft.getInstance().player.setDeltaMovement(motion);
        });
        return true;
    }
}