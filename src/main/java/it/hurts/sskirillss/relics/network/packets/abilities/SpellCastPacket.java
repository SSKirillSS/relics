package it.hurts.sskirillss.relics.network.packets.abilities;

import it.hurts.sskirillss.relics.client.hud.abilities.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellCastPacket {
    private final String ability;
    private final int slot;

    public SpellCastPacket(FriendlyByteBuf buf) {
        ability = buf.readUtf();
        slot = buf.readInt();
    }

    public SpellCastPacket(String ability, int slot) {
        this.ability = ability;
        this.slot = slot;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(ability);
        buf.writeInt(slot);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null)
                return;

            ItemStack stack = AbilityUtils.getStackInCuriosSlot(player, slot);

            if (!(stack.getItem() instanceof RelicItem relic)
                    || !AbilityUtils.getRelicActiveAbilities(stack).contains(ability)
                    || !RelicItem.canUseAbility(stack, ability))
                return;

            relic.castActiveAbility(stack, player, ability);
        });

        return true;
    }
}