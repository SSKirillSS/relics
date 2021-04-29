package it.hurts.sskirillss.relics.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPlayerMotion {
    private final double motionX;
    private final double motionY;
    private final double motionZ;

    public PacketPlayerMotion(PacketBuffer buf) {
        motionX = buf.readDouble();
        motionY = buf.readDouble();
        motionZ = buf.readDouble();
    }

    public PacketPlayerMotion(double motionX, double motionY, double motionZ) {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeDouble(motionX);
        buf.writeDouble(motionY);
        buf.writeDouble(motionZ);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Vector3d motion = new Vector3d(this.motionX, this.motionY, this.motionZ);
            Minecraft.getInstance().player.setDeltaMovement(motion);
        });
        return true;
    }
}