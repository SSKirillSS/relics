package it.hurts.sskirillss.relics.network.packets.abilities;

import it.hurts.sskirillss.relics.client.hud.abilities.ActiveAbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellCastPacket {
    private final AbilityCastType type;
    private final AbilityCastStage stage;
    private final String ability;
    private final int slot;

    public SpellCastPacket(FriendlyByteBuf buf) {
        ability = buf.readUtf();
        slot = buf.readInt();
        type = buf.readEnum(AbilityCastType.class);
        stage = buf.readEnum(AbilityCastStage.class);
    }

    public SpellCastPacket(AbilityCastType type, AbilityCastStage stage, String ability, int slot) {
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

            if (!(stack.getItem() instanceof RelicItem relic))
                return;

            if (!ActiveAbilityUtils.getRelicActiveAbilities(stack).contains(ability)
                    || !AbilityUtils.canPlayerUseActiveAbility(player, stack, ability)) {
                if (AbilityUtils.isAbilityTicking(stack, ability)) {
                    AbilityUtils.setAbilityTicking(stack, ability, false);

                    relic.castActiveAbility(stack, player, ability, type, AbilityCastStage.END);
                }

                return;
            }

            switch (type) {
                case CYCLICAL -> {
                    switch (stage) {
                        case START -> AbilityUtils.setAbilityTicking(stack, ability, true);
                        case END -> AbilityUtils.setAbilityTicking(stack, ability, false);
                    }
                }
                case TOGGLEABLE -> {
                    switch (stage) {
                        case START -> AbilityUtils.setAbilityTicking(stack, ability, true);
                        case END -> AbilityUtils.setAbilityTicking(stack, ability, false);
                    }
                }
            }

            relic.castActiveAbility(stack, player, ability, type, stage);
        });

        return true;
    }
}