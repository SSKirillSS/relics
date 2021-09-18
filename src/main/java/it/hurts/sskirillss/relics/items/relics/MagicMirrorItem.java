package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketItemActivation;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Optional;

public class MagicMirrorItem extends RelicItem<MagicMirrorItem.Stats> {
    public MagicMirrorItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.15F)
                        .build())
                .build());
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .build();
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (playerIn.getCooldowns().isOnCooldown(ItemRegistry.MAGIC_MIRROR.get())
                || worldIn.isClientSide())
            return ActionResult.fail(stack);

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
        ServerWorld serverWorld = (ServerWorld) worldIn;
        BlockPos pos = serverPlayer.getRespawnPosition();
        boolean isForced = serverPlayer.isRespawnForced();
        float angle = serverPlayer.getRespawnAngle();
        ServerWorld world = serverPlayer.getServer().getLevel(serverPlayer.getRespawnDimension());
        TranslationTextComponent message = new TranslationTextComponent("tooltip.relics.magic_mirror.invalid_location");

        if (pos == null || world == null) {
            playerIn.displayClientMessage(message, true);
            return ActionResult.fail(stack);
        }

        Optional<Vector3d> optional = PlayerEntity.findRespawnPositionAndUseSpawnBlock(serverWorld, pos, angle, isForced, false);

        if (optional.isPresent()) {
            pos = new BlockPos(optional.get());

            if (playerIn.getVehicle() != null)
                playerIn.stopRiding();

            serverPlayer.teleportTo(world, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F, playerIn.yRot, playerIn.xRot);
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            NetworkHandler.sendToClient(new PacketItemActivation(stack), serverPlayer);

            if (!playerIn.abilities.instabuild)
                playerIn.getCooldowns().addCooldown(ItemRegistry.MAGIC_MIRROR.get(), config.cooldown * 20);
        } else
            playerIn.displayClientMessage(message, true);

        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    public static class Stats extends RelicStats {
        public int cooldown = 30;
    }
}