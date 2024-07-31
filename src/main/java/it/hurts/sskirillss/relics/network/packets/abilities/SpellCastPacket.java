package it.hurts.sskirillss.relics.network.packets.abilities;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.system.casts.abilities.AbilityReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellCastPacket {
    private final CastType type;
    private final CastStage stage;
    private final CompoundTag ability;

    public SpellCastPacket(FriendlyByteBuf buf) {
        type = buf.readEnum(CastType.class);
        stage = buf.readEnum(CastStage.class);
        ability = buf.readNbt();
    }

    public SpellCastPacket(CastType type, CastStage stage, CompoundTag ability) {
        this.type = type;
        this.stage = stage;
        this.ability = ability;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeEnum(stage);
        buf.writeNbt(ability);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            AbilityReference reference = new AbilityReference().deserializeNBT(ability);
            ItemStack stack = reference.getSlot().gatherStack(player);

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;

            if (!relic.canPlayerUseActiveAbility(player, stack, reference.getId())) {
                if (relic.isAbilityTicking(stack, reference.getId())) {
                    relic.setAbilityTicking(stack, reference.getId(), false);

                    relic.castActiveAbility(stack, player, reference.getId(), type, CastStage.END);
                }

                return;
            }

            switch (type) {
                case CYCLICAL, TOGGLEABLE -> {
                    switch (stage) {
                        case START -> relic.setAbilityTicking(stack, reference.getId(), true);
                        case END -> relic.setAbilityTicking(stack, reference.getId(), false);
                    }
                }
            }

            relic.castActiveAbility(stack, player, reference.getId(), type, stage);
        });
        return true;
    }
}