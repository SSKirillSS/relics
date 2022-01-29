package it.hurts.sskirillss.relics.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMouseScroll {
    private final double delta;
    private final ItemStack stack;

    public PacketMouseScroll(FriendlyByteBuf buf) {
        delta = buf.readDouble();
        stack = buf.readItem();
    }

    public PacketMouseScroll(double delta, ItemStack stack) {
        this.delta = delta;
        this.stack = stack;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(delta);
        buf.writeItem(stack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                double delta = this.delta;
                ItemStack stack = this.stack;
                Player player = ctx.get().getSender();
            }
        });
        return true;
    }
}