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

            Level level = player.level;

            if (!(level.getBlockEntity(pos) instanceof ResearchingTableTile tile))
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
                    int maxPoints = entry.getMaxLevel() == -1 ? ((relic.getNewData().getLevelingData().getMaxLevel() - entry.getRequiredLevel()) / entry.getRequiredPoints()) : entry.getMaxLevel();

                    if (RelicItem.getAbilityPoints(stack, ability) < maxPoints)
                        RelicItem.setAbilityPoints(stack, ability, RelicItem.getAbilityPoints(stack, ability) + 1);
                }
                case REROLL -> RelicItem.randomizeStats(stack, ability);
                case RESET -> {
                    RelicItem.setAbilityPoints(stack, ability, 0);
                    RelicItem.setPoints(stack, 0);
                }
            }

            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 2);
        });

        return true;
    }

    public enum Operation {
        RESET,
        INCREASE,
        REROLL,
    }
}