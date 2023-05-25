package it.hurts.sskirillss.relics.network.packets.abilities;

import it.hurts.sskirillss.relics.client.hud.abilities.ActiveAbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
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

            ItemStack stack = ActiveAbilityUtils.getStackInCuriosSlot(player, slot);

            if (!(stack.getItem() instanceof RelicItem relic)
                    || !ActiveAbilityUtils.getRelicActiveAbilities(stack).contains(ability)
                    || !AbilityUtils.canPlayerUseActiveAbility(player, stack, ability))
                return;

            relic.endCastActiveAbility(stack, player, ability);
        });

        return true;
    }
}