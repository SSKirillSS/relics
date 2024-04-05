package it.hurts.sskirillss.relics.network.packets.abilities;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.system.casts.abilities.AbilityReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellCastPacket {
    private final CastType type;
    private final CastStage stage;
    private final AbilityReference ability;

    public SpellCastPacket(FriendlyByteBuf buf) {
        type = buf.readEnum(CastType.class);
        stage = buf.readEnum(CastStage.class);
        ability = new AbilityReference().deserializeNBT(buf.readNbt());
    }

    public SpellCastPacket(CastType type, CastStage stage, AbilityReference ability) {
        this.type = type;
        this.stage = stage;
        this.ability = ability;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeEnum(stage);
        buf.writeNbt(ability.serializeNBT());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null)
                return;

            ItemStack stack = ability.getSlot().gatherStack(player);

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;

            if (!relic.canPlayerUseActiveAbility(player, stack, ability.getId())) {
                if (relic.isAbilityTicking(stack, ability.getId())) {
                    relic.setAbilityTicking(stack, ability.getId(), false);

                    relic.castActiveAbility(stack, player, ability.getId(), type, CastStage.END);
                }

                return;
            }

            switch (type) {
                case CYCLICAL, TOGGLEABLE -> {
                    switch (stage) {
                        case START -> relic.setAbilityTicking(stack, ability.getId(), true);
                        case END -> relic.setAbilityTicking(stack, ability.getId(), false);
                    }
                }
            }

            relic.castActiveAbility(stack, player, ability.getId(), type, stage);
        });

        return true;
    }
}