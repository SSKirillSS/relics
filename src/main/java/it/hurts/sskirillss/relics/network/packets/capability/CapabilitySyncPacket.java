package it.hurts.sskirillss.relics.network.packets.capability;

import it.hurts.sskirillss.relics.capability.utils.CapabilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CapabilitySyncPacket {
    private final CompoundTag data;

    public CapabilitySyncPacket(FriendlyByteBuf buf) {
        data = buf.readNbt();
    }

    public CapabilitySyncPacket(CompoundTag data) {
        this.data = data;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(this::doSync);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void doSync() {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return;

        CapabilityUtils.getRelicsCapability(player).deserializeNBT(data);
    }
}