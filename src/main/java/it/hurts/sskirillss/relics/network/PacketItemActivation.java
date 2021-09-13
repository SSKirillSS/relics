package it.hurts.sskirillss.relics.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketItemActivation {
    private final ItemStack stack;

    public PacketItemActivation(PacketBuffer buf) {
        stack = buf.readItem();
    }

    public PacketItemActivation(ItemStack stack) {
        this.stack = stack;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeItem(stack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Minecraft.getInstance().gameRenderer.displayItemActivation(stack));
        return true;
    }
}