package it.hurts.sskirillss.relics.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketItemActivation {
    private final ItemStack stack;

    public PacketItemActivation(FriendlyByteBuf buf) {
        stack = buf.readItem();
    }

    public PacketItemActivation(ItemStack stack) {
        this.stack = stack;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Minecraft.getInstance().gameRenderer.displayItemActivation(stack));
        return true;
    }
}