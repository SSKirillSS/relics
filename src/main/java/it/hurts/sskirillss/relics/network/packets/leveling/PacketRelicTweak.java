package it.hurts.sskirillss.relics.network.packets.leveling;

import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.IntFunction;
import java.util.function.Supplier;

@AllArgsConstructor
public class PacketRelicTweak {
    private final int container;
    private final int slot;
    private final String ability;
    private final Operation operation;

    public PacketRelicTweak(FriendlyByteBuf buf) {
        container = buf.readInt();
        slot = buf.readInt();
        ability = buf.readUtf();
        operation = buf.readEnum(Operation.class);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(container);
        buf.writeInt(slot);
        buf.writeUtf(ability);
        buf.writeEnum(operation);
    }

    private static void causeError(Player player) {
        player.displayClientMessage(Component.translatable("info.relics.researching.wrong_container").withStyle(ChatFormatting.RED), false);

        player.closeContainer();
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            if (player == null) {
                causeError(player);
                return;
            }

            if (player.containerMenu.containerId != container) {
                causeError(player);

                return;
            }

            ItemStack stack = DescriptionUtils.gatherRelicStack(player, slot);

            if (!(stack.getItem() instanceof IRelicItem relic)) {
                causeError(player);

                return;
            }

            AbilityData entry = relic.getAbilityData(ability);

            if (entry == null)
                return;

            switch (operation) {
                case UPGRADE -> {
                    if (relic.mayPlayerUpgrade(player, stack, ability)) {
                        player.giveExperiencePoints(-relic.getUpgradeRequiredExperience(stack, ability));

                        relic.setAbilityPoints(stack, ability, relic.getAbilityPoints(stack, ability) + 1);
                        relic.addPoints(stack, -entry.getRequiredPoints());
                    }
                }
                case REROLL -> {
                    if (relic.mayPlayerReroll(player, stack, ability)) {
                        player.giveExperiencePoints(-relic.getRerollRequiredExperience(ability));

                        relic.randomizeStats(stack, ability);
                    }
                }
                case RESET -> {
                    if (relic.mayPlayerReset(player, stack, ability)) {
                        player.giveExperiencePoints(-relic.getResetRequiredExperience(stack, ability));

                        relic.addPoints(stack, relic.getAbilityPoints(stack, ability) * entry.getRequiredPoints());
                        relic.setAbilityPoints(stack, ability, 0);
                    }
                }
            }

            try {
                player.containerMenu.getSlot(slot).set(stack);
            } catch (Exception e) {
                e.printStackTrace();

                causeError(player);
            }
        });

        return true;
    }


    @Getter
    @AllArgsConstructor
    public enum Operation {
        RESET(0),
        UPGRADE(1),
        REROLL(2);

        public static final IntFunction<Operation> BY_ID = ByIdMap.continuous(Operation::getId, Operation.values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        private final int id;
    }
}