package it.hurts.sskirillss.relics.network.packets.leveling;

import io.netty.buffer.ByteBuf;
import it.hurts.sskirillss.relics.client.screen.description.misc.DescriptionUtils;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.IntFunction;

@Data
@AllArgsConstructor
public class PacketRelicTweak implements CustomPacketPayload {
    private final int container;
    private final int slot;
    private final String ability;
    private final Operation operation;

    public static final CustomPacketPayload.Type<PacketRelicTweak> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "relic_tweak"));

    public static final StreamCodec<ByteBuf, PacketRelicTweak> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketRelicTweak::getContainer,
            ByteBufCodecs.INT, PacketRelicTweak::getSlot,
            ByteBufCodecs.STRING_UTF8, PacketRelicTweak::getAbility,
            ByteBufCodecs.idMapper(Operation.BY_ID, Operation::getId), PacketRelicTweak::getOperation,
            PacketRelicTweak::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();

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
                        player.giveExperienceLevels(-relic.getUpgradeRequiredLevel(stack, ability));

                        relic.setAbilityPoints(stack, ability, relic.getAbilityPoints(stack, ability) + 1);
                        relic.addPoints(stack, -entry.getRequiredPoints());
                    }
                }
                case REROLL -> {
                    if (relic.mayPlayerReroll(player, stack, ability)) {
                        player.giveExperienceLevels(-relic.getRerollRequiredLevel(stack, ability));

                        int prevQuality = relic.getAbilityQuality(stack, ability);

                        relic.randomizeStats(stack, ability, relic.getLuck(stack));

                        if (relic.getAbilityQuality(stack, ability) < prevQuality)
                            relic.addLuck(stack, 1);
                    }
                }
                case RESET -> {
                    if (relic.mayPlayerReset(player, stack, ability)) {
                        player.giveExperienceLevels(-relic.getResetRequiredLevel(stack, ability));

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
    }

    private static void causeError(Player player) {
        player.displayClientMessage(Component.translatable("info.relics.researching.wrong_container").withStyle(ChatFormatting.RED), false);

        player.closeContainer();
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