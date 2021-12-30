package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicLootData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketItemActivation;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
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
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#008cd7", "#0a3484")
                .ability(AbilityTooltip.builder()
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(RelicLootData.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.1F)
                        .build())
                .build();
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (playerIn.getCooldowns().isOnCooldown(ItemRegistry.MAGIC_MIRROR.get())
                || DurabilityUtils.isBroken(stack) || worldIn.isClientSide())
            return ActionResult.fail(stack);

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
        BlockPos pos = serverPlayer.getRespawnPosition();
        float angle = serverPlayer.getRespawnAngle();
        ServerWorld world = serverPlayer.getServer().getLevel(serverPlayer.getRespawnDimension());
        TranslationTextComponent message = new TranslationTextComponent("tooltip.relics.magic_mirror.invalid_location");

        if (pos == null || world == null) {
            playerIn.displayClientMessage(message, true);

            return ActionResult.fail(stack);
        }

        Optional<Vector3d> optional = PlayerEntity.findRespawnPositionAndUseSpawnBlock(world, pos, angle, true, false);

        if (optional.isPresent()) {
            pos = new BlockPos(optional.get());

            if (playerIn.getVehicle() != null)
                playerIn.stopRiding();

            serverPlayer.teleportTo(world, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F, playerIn.yRot, playerIn.xRot);
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            NetworkHandler.sendToClient(new PacketItemActivation(stack), serverPlayer);

            if (!playerIn.abilities.instabuild)
                playerIn.getCooldowns().addCooldown(ItemRegistry.MAGIC_MIRROR.get(), stats.cooldown * 20);
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