package it.hurts.sskirillss.relics.network;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class PacketRelicAbility {
    private final Integer slot;

    public PacketRelicAbility(FriendlyByteBuf buf) {
        slot = buf.readInt();
    }

    public PacketRelicAbility(Integer slot) {
        this.slot = slot;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            if (player == null || !player.isAlive())
                return;

            CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(handler -> {
                ItemStack stack = handler.getStackInSlot(this.slot);

                if (stack.isEmpty() || !(stack.getItem() instanceof RelicItem<?> relic)
                        || !relic.getData().hasAbility())
                    return;

                relic.castAbility(player, stack);
            });
        });
        return true;
    }
}