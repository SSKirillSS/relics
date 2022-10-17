package it.hurts.sskirillss.relics.network.packets.leveling;

import it.hurts.sskirillss.relics.indev.RelicAbilityData;
import it.hurts.sskirillss.relics.indev.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
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

            if (!(stack.getItem() instanceof RelicItem<?> relic))
                return;

            RelicAbilityData data = relic.getNewData().getAbilityData();

            if (data == null)
                return;

            RelicAbilityEntry entry = data.getAbilities().get(ability);

            if (entry == null)
                return;

            switch (operation) {
                case INCREASE -> {
                    if (RelicItem.mayPlayerUpgrade(player, stack, ability)) {
                        RelicItem.setAbilityPoints(stack, ability, RelicItem.getAbilityPoints(stack, ability) + 1);
                        RelicItem.addPoints(stack, -entry.getRequiredPoints());

                        player.giveExperiencePoints(-RelicItem.getUpgradeRequiredExperience(stack, ability));
                    }
                }
                case REROLL -> {
                    if (RelicItem.mayPlayerReroll(player, stack, ability)) {
                        RelicItem.randomizeStats(stack, ability);
                    }
                }
                case RESET -> {
                    if (RelicItem.mayPlayerReset(player, stack, ability)) {
                        RelicItem.setAbilityPoints(stack, ability, 0);

                        RelicItem.addPoints(stack, RelicItem.getAbilityPoints(stack, ability) * entry.getRequiredPoints());
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