package it.hurts.sskirillss.relics.network;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class PacketRelicAbility {
    private final Integer slot;

    public PacketRelicAbility(PacketBuffer buf) {
        slot = buf.readInt();
    }

    public PacketRelicAbility(Integer slot) {
        this.slot = slot;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(slot);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player == null || !player.isAlive()) return;
            CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(handler -> {
                ItemStack stack = handler.getStackInSlot(this.slot);
                if (stack.isEmpty() || !(stack.getItem() instanceof RelicItem)) return;
                RelicItem relic = (RelicItem) stack.getItem();
                if (!relic.getData().hasAbility()) return;
                relic.castAbility(player, stack);
            });
        });
        return true;
    }
}