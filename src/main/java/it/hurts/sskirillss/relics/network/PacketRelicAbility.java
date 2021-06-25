package it.hurts.sskirillss.relics.network;

import it.hurts.sskirillss.relics.items.RelicItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class PacketRelicAbility {
    // Unsecure. Should be replaced with getting slot ID
    private final ItemStack relic;

    public PacketRelicAbility(PacketBuffer buf) {
        relic = buf.readItem();
    }

    public PacketRelicAbility(ItemStack relic) {
        this.relic = relic;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeItem(relic);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player == null || !player.isAlive()) return;
            ItemStack stack = this.relic;
            if (!CuriosApi.getCuriosHelper().findEquippedCurio(stack.getItem(), player).isPresent()
                    || !(stack.getItem() instanceof RelicItem)) return;
            RelicItem relic = (RelicItem) stack.getItem();
            if (!relic.hasAbility()) return;
            relic.castAbility(player, stack);
        });
        return true;
    }
}