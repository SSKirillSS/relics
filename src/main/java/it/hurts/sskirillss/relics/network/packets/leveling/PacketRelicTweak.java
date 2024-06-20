package it.hurts.sskirillss.relics.network.packets.leveling;

import io.netty.buffer.ByteBuf;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.IntFunction;

@Data
@AllArgsConstructor
public class PacketRelicTweak implements CustomPacketPayload {
    private final BlockPos pos;
    private final String ability;
    private final Operation operation;

    public static final CustomPacketPayload.Type<PacketRelicTweak> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "relic_tweak"));

    public static final StreamCodec<ByteBuf, PacketRelicTweak> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketRelicTweak::getPos,
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
            Level world = player.level();

            if (!(world.getBlockEntity(pos) instanceof ResearchingTableTile tile))
                return;

            ItemStack stack = tile.getStack();

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;

            AbilityData entry = relic.getAbilityData(ability);

            if (entry == null)
                return;

            BlockState state = world.getBlockState(pos);

            switch (operation) {
                case INCREASE -> {
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

            tile.setStack(stack);
            tile.setChanged();

            world.sendBlockUpdated(pos, state, world.getBlockState(pos), 3);
        });
    }

    @Getter
    @AllArgsConstructor
    public enum Operation {
        RESET(0),
        INCREASE(1),
        REROLL(2);

        public static final IntFunction<Operation> BY_ID = ByIdMap.continuous(Operation::getId, Operation.values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        private final int id;
    }
}