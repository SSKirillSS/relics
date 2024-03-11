package it.hurts.sskirillss.relics.network.packets.abilities;

import it.hurts.sskirillss.relics.client.hud.abilities.ActiveAbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellCastPacket {
    private final CastType type;
    private final CastStage stage;
    private final String ability;
    private final int slot;

    public SpellCastPacket(FriendlyByteBuf buf) {
        ability = buf.readUtf();
        slot = buf.readInt();
        type = buf.readEnum(CastType.class);
        stage = buf.readEnum(CastStage.class);
    }

    public SpellCastPacket(CastType type, CastStage stage, String ability, int slot) {
        this.ability = ability;
        this.slot = slot;
        this.type = type;
        this.stage = stage;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(ability);
        buf.writeInt(slot);
        buf.writeEnum(type);
        buf.writeEnum(stage);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null)
                return;

            ItemStack stack = ActiveAbilityUtils.getStackInCuriosSlot(player, slot);

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;

            if (!ActiveAbilityUtils.getRelicActiveAbilities(stack).contains(ability)
                    || !relic.canPlayerUseActiveAbility(player, stack, ability)) {
                if (relic.isAbilityTicking(stack, ability)) {
                    relic.setAbilityTicking(stack, ability, false);

                    relic.castActiveAbility(stack, player, ability, type, CastStage.END);
                }

                return;
            }

            switch (type) {
                case CYCLICAL, TOGGLEABLE -> {
                    switch (stage) {
                        case START -> relic.setAbilityTicking(stack, ability, true);
                        case END -> relic.setAbilityTicking(stack, ability, false);
                    }
                }
            }

            relic.castActiveAbility(stack, player, ability, type, stage);
        });

        return true;
    }
}