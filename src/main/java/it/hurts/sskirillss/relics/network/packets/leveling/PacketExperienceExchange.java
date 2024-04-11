package it.hurts.sskirillss.relics.network.packets.leveling;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketExperienceExchange {
    private final BlockPos pos;
    private final int amount;

    public PacketExperienceExchange(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        amount = buf.readInt();
    }

    public PacketExperienceExchange(BlockPos pos, int amount) {
        this.pos = pos;
        this.amount = amount;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(amount);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            if (player == null)
                return;

            Level world = player.getLevel();

            if (!(world.getBlockEntity(pos) instanceof ResearchingTableTile tile))
                return;

            ItemStack stack = tile.getStack();

            if (!(stack.getItem() instanceof IRelicItem relic) || relic.isMaxLevel(stack))
                return;

            int playerExperience = EntityUtils.getPlayerTotalExperience(player);

            if (playerExperience <= 0)
                return;

            int exchanges = relic.getExchanges(stack);
            int level = relic.getLevel(stack);

            int cost = 5;

            int toAdd = 0;
            int toTake = 0;

            for (int i = 0; i < amount; i++) {
                int oneCost = (int) (cost + (cost * ((exchanges + i) * 0.01F)));

                if (playerExperience < toTake + oneCost)
                    break;

                toAdd += (int) Math.ceil(relic.getExperienceBetweenLevels(stack, level, level + 1) / 100F);
                toTake += oneCost;

                relic.addExchanges(stack, 1);
            }

            player.giveExperiencePoints(-toTake);

            relic.addExperience(player, stack, toAdd);

            world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        });

        return true;
    }
}