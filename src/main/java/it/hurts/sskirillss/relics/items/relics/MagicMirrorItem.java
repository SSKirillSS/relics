package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketItemActivation;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
                .build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (playerIn.getCooldowns().isOnCooldown(ItemRegistry.MAGIC_MIRROR.get())
                || DurabilityUtils.isBroken(stack) || worldIn.isClientSide())
            return InteractionResultHolder.fail(stack);

        ServerPlayer serverPlayer = (ServerPlayer) playerIn;
        BlockPos pos = serverPlayer.getRespawnPosition();
        float angle = serverPlayer.getRespawnAngle();
        ServerLevel world = serverPlayer.getServer().getLevel(serverPlayer.getRespawnDimension());
        TranslatableComponent message = new TranslatableComponent("tooltip.relics.magic_mirror.invalid_location");

        if (pos == null || world == null) {
            playerIn.displayClientMessage(message, true);

            return InteractionResultHolder.fail(stack);
        }

        Optional<Vec3> optional = Player.findRespawnPositionAndUseSpawnBlock(world, pos, angle, true, false);

        if (optional.isPresent()) {
            pos = new BlockPos(optional.get());

            if (playerIn.getVehicle() != null)
                playerIn.stopRiding();

            serverPlayer.teleportTo(world, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F, playerIn.getYRot(), playerIn.getXRot());
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            NetworkHandler.sendToClient(new PacketItemActivation(stack), serverPlayer);

            if (!playerIn.getAbilities().instabuild)
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