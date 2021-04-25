package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.lang.*;

public class SpatialSignItem extends Item implements IHasTooltip {
    public static final String TAG_POSITION_X = "pos_x";
    public static final String TAG_POSITION_Y = "pos_y";
    public static final String TAG_POSITION_Z = "pos_z";
    public static final String TAG_TIME = "time";
    public static final String TAG_WORLD = "world";

    public SpatialSignItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.shift_2"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (NBTUtils.getInt(stack, TAG_TIME, -1) > 0) {
            tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.tooltip_1",
                    NBTUtils.getFloat(stack, TAG_POSITION_X, 0),
                    NBTUtils.getFloat(stack, TAG_POSITION_Y, 0),
                    NBTUtils.getFloat(stack, TAG_POSITION_Z, 0)));
            tooltip.add(new TranslationTextComponent("tooltip.relics.spatial_sign.tooltip_2", NBTUtils.getInt(stack, TAG_TIME, 0)));
        }
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (NBTUtils.getInt(stack, TAG_TIME, -1) <= -1) {
            setPosition(stack, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ());
            NBTUtils.setString(stack, TAG_WORLD, playerIn.getEntityWorld().getDimensionKey().getLocation().toString());
            NBTUtils.setInt(stack, TAG_TIME, RelicsConfig.SpatialSign.TIME_BEFORE_ACTIVATION.get());
            worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
        } else if (NBTUtils.getInt(stack, TAG_TIME, -1) > 0 && playerIn.isSneaking()) teleportPlayer(playerIn, stack);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote()) return;
        if (!(entityIn instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entityIn;
        if (player.ticksExisted % 20 == 0 && NBTUtils.getInt(stack, TAG_TIME, -1) >= 0) {
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
    public boolean hasEffect(ItemStack stack) {
        return NBTUtils.getInt(stack, TAG_TIME, 0) > 0;
    }

    public static void teleportPlayer(PlayerEntity player, ItemStack stack) {
        Vector3d pos = getPosition(stack);
        String worldString = NBTUtils.getString(stack, TAG_WORLD, "").equals("")
                ? player.getEntityWorld().getDimensionKey().getLocation().toString() : NBTUtils.getString(stack, TAG_WORLD, "");

        if (!player.getEntityWorld().isRemote() && player.getEntityWorld().getServer()
                .getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(worldString))) != null) {
            EntityUtils.teleportWithMount(player, player.getEntityWorld().getServer().getWorld(RegistryKey
                    .getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(worldString))), pos);
        }

        player.getEntityWorld().playSound(player, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        if (!player.abilities.isCreativeMode) player.getCooldownTracker().setCooldown(stack.getItem(),
                (RelicsConfig.SpatialSign.TIME_BEFORE_ACTIVATION.get() - NBTUtils.getInt(stack, TAG_TIME, 0)) * 20);

        NBTUtils.setString(stack, TAG_WORLD, "");
        NBTUtils.setInt(stack, TAG_TIME, -1);
    }

    private static void setPosition(ItemStack stack, double posX, double posY, double posZ) {
        NBTUtils.setFloat(stack, TAG_POSITION_X, Math.round(posX * 10F) / 10F);
        NBTUtils.setFloat(stack, TAG_POSITION_Y, Math.round(posY * 10F) / 10F);
        NBTUtils.setFloat(stack, TAG_POSITION_Z, Math.round(posZ * 10F) / 10F);
    }

    private static Vector3d getPosition(ItemStack stack) {
        return new Vector3d(NBTUtils.getFloat(stack, TAG_POSITION_X, 0),
                NBTUtils.getFloat(stack, TAG_POSITION_Y, 0),
                NBTUtils.getFloat(stack, TAG_POSITION_Z, 0));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SpatialSignServerEvents {
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()) == -1) return;
            ItemStack stack = player.inventory.getStackInSlot(EntityUtils.getSlotWithItem(player, ItemRegistry.SPATIAL_SIGN.get()));
            if (NBTUtils.getInt(stack, TAG_TIME, 0) > 0) {
                teleportPlayer(player, stack);
                player.setHealth(1.0F);
                stack.shrink(1);
                event.setCanceled(true);
            }
        }
    }
}