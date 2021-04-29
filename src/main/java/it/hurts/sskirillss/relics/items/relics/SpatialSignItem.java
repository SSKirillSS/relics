package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.lang.*;

public class SpatialSignItem extends RelicItem implements IHasTooltip {
    public static final String TAG_POSITION = "pos";
    public static final String TAG_TIME = "time";
    public static final String TAG_WORLD = "world";

    public SpatialSignItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            Vector3d pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));
            tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.tooltip_1", pos.x(), pos.y(), pos.z()));
            tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.tooltip_2", NBTUtils.getInt(stack, TAG_TIME, 0)));
        }
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            NBTUtils.setString(stack, TAG_POSITION, NBTUtils.writePosition(playerIn.position()));
            NBTUtils.setString(stack, TAG_WORLD, playerIn.getCommandSenderWorld().dimension().location().toString());
            NBTUtils.setInt(stack, TAG_TIME, RelicsConfig.SpatialSign.TIME_BEFORE_ACTIVATION.get());
            worldIn.playSound(playerIn, playerIn.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
        } else if (playerIn.isShiftKeyDown()) teleportPlayer(playerIn, stack);
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isClientSide()) return;
        if (!(entityIn instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entityIn;
        if (player.tickCount % 20 == 0 && NBTUtils.getInt(stack, TAG_TIME, -1) >= 0) {
            int time = NBTUtils.getInt(stack, TAG_TIME, -1);
            if (time > 0) NBTUtils.setInt(stack, TAG_TIME, time - 1);
            else teleportPlayer(player, stack);
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return NBTUtils.getInt(stack, TAG_TIME, 0) > 0;
    }

    public static void teleportPlayer(PlayerEntity player, ItemStack stack) {
        if (player.getCommandSenderWorld().isClientSide()) return;
        Vector3d pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));
        ServerWorld world = NBTUtils.parseWorld(player.getCommandSenderWorld(), NBTUtils.getString(stack, TAG_WORLD, ""));
        if (pos == null || world == null) return;
        EntityUtils.teleportWithMount(player, world, pos);
        player.getCommandSenderWorld().playSound(player, pos.x(), pos.y(), pos.z(),
                SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        if (!player.abilities.instabuild) player.getCooldowns().addCooldown(stack.getItem(),
                (RelicsConfig.SpatialSign.TIME_BEFORE_ACTIVATION.get() - NBTUtils.getInt(stack, TAG_TIME, 0)) * 20);
        NBTUtils.setString(stack, TAG_WORLD, "");
        NBTUtils.setString(stack, TAG_POSITION, "");
        NBTUtils.setInt(stack, TAG_TIME, -1);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SpatialSignServerEvents {
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()) == -1) return;
            ItemStack stack = player.inventory.getItem(EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()));
            if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
                teleportPlayer(player, stack);
                player.setHealth(1.0F);
                stack.shrink(1);
                event.setCanceled(true);
            }
        }
    }
}