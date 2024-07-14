package it.hurts.sskirillss.relics.network.packets.leveling;

import io.netty.buffer.ByteBuf;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.tiles.ResearchingTableTile;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Data
@AllArgsConstructor
public class PacketExperienceExchange implements CustomPacketPayload {
    private final BlockPos pos;
    private final int amount;

    public static final CustomPacketPayload.Type<PacketExperienceExchange> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "experience_exchange"));

    public static final StreamCodec<ByteBuf, PacketExperienceExchange> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketExperienceExchange::getPos,
            ByteBufCodecs.INT, PacketExperienceExchange::getAmount,
            PacketExperienceExchange::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level world = player.level();

            if (!(world.getBlockEntity(pos) instanceof ResearchingTableTile tile))
                return;

            ItemStack stack = tile.getStack();

            if (!(stack.getItem() instanceof IRelicItem relic) || relic.isMaxLevel(stack))
                return;

            int playerExperience = EntityUtils.getPlayerTotalExperience(player);

            if (playerExperience <= 0)
                return;

            BlockState state = world.getBlockState(pos);

            int exchanges = relic.getExchanges(stack);
            int level = relic.getLevel(stack);

            int cost = 5;

            int toAdd = 0;
            int toTake = 0;

            for (int i = 0; i < amount; i++) {
                int oneCost = (int) (cost + (cost * ((exchanges + i) * 0.01F)));

                if (playerExperience < toTake + oneCost)
                    break;

                toAdd += (int) Math.ceil(relic.getExperienceBetweenLevels(level, level + 1) / 100F);
                toTake += oneCost;

                relic.addExchanges(stack, 1);
            }

            player.giveExperiencePoints(-toTake);

            relic.addExperience(player, stack, toAdd);

            tile.setStack(stack);
            tile.setChanged();

            world.sendBlockUpdated(pos, state, world.getBlockState(pos), 3);
        });
    }
}