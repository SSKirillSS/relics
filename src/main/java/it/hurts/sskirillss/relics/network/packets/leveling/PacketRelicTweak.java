package it.hurts.sskirillss.relics.network.packets.leveling;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketRelicTweak {
    private final BlockPos pos;
    private final String ability;
    private final Operation operation;

    public PacketRelicTweak(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        ability = buf.readUtf();
        operation = buf.readEnum(Operation.class);
    }

    public PacketRelicTweak(BlockPos pos, String ability, Operation operation) {
        this.pos = pos;
        this.ability = ability;
        this.operation = operation;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(ability);
        buf.writeEnum(operation);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null)
                return;

            Level world = player.level;

            if (!(world.getBlockEntity(pos) instanceof ResearchingTableTile tile))
                return;

            ItemStack stack = tile.getStack();

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;

            AbilityData entry = relic.getAbilityData(ability);

            if (entry == null)
                return;

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

            world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        });

        return true;
    }

    public enum Operation {
        RESET,
        INCREASE,
        REROLL
    }
}