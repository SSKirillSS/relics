package it.hurts.sskirillss.relics.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMouseScroll {
    private final double delta;
    private final ItemStack stack;

    public PacketMouseScroll(PacketBuffer buf) {
        delta = buf.readDouble();
        stack = buf.readItemStack();
    }

    public PacketMouseScroll(double delta, ItemStack stack) {
        this.delta = delta;
        this.stack = stack;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeDouble(delta);
        buf.writeItemStack(stack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                double delta = this.delta;
                ItemStack stack = this.stack;
                PlayerEntity player = ctx.get().getSender();

                player.getHeldItemMainhand().grow((int) Math.round(delta));
            }
        });
        return true;
    }
}