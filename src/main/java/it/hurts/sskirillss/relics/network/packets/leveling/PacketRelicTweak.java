package it.hurts.sskirillss.relics.network.packets.leveling;

import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
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

            Level world = player.level();

            if (!(world.getBlockEntity(pos) instanceof ResearchingTableTile tile))
                return;

            ItemStack stack = tile.getStack();

            if (!(stack.getItem() instanceof RelicItem relic))
                return;

            RelicAbilityData data = relic.getRelicData().getAbilityData();

            if (data == null)
                return;

            RelicAbilityEntry entry = data.getAbilities().get(ability);

            if (entry == null)
                return;

            switch (operation) {
                case INCREASE -> {
                    if (AbilityUtils.mayPlayerUpgrade(player, stack, ability)) {
                        player.giveExperiencePoints(-AbilityUtils.getUpgradeRequiredExperience(stack, ability));

                        AbilityUtils.setAbilityPoints(stack, ability, AbilityUtils.getAbilityPoints(stack, ability) + 1);
                        LevelingUtils.addPoints(stack, -entry.getRequiredPoints());
                    }
                }
                case REROLL -> {
                    if (AbilityUtils.mayPlayerReroll(player, stack, ability)) {
                        player.giveExperiencePoints(-AbilityUtils.getRerollRequiredExperience(stack, ability));

                        AbilityUtils.randomizeStats(stack, ability);
                    }
                }
                case RESET -> {
                    if (AbilityUtils.mayPlayerReset(player, stack, ability)) {
                        player.giveExperiencePoints(-AbilityUtils.getResetRequiredExperience(stack, ability));

                        LevelingUtils.addPoints(stack, AbilityUtils.getAbilityPoints(stack, ability) * entry.getRequiredPoints());
                        AbilityUtils.setAbilityPoints(stack, ability, 0);
                    }
                }
            }

            world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        });

        return true;
    }

    public enum Operation {
        RESET,
        INCREASE,
        REROLL,
    }
}