package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.RelicStats;
import it.hurts.sskirillss.relics.entities.SpaceDissectorEntity;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTables;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SpaceDissectorItem extends RelicItem<SpaceDissectorItem.Stats> implements IHasTooltip {
    public static final String TAG_IS_THROWN = "thrown";
    public static final String TAG_UUID = "uuid";
    public static final String TAG_UPDATE_TIME = "time";

    public static SpaceDissectorItem INSTANCE;

    public SpaceDissectorItem() {
        super(Rarity.EPIC);

        INSTANCE = this;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getMainHandItem();
        if (handIn == Hand.MAIN_HAND && !playerIn.getCooldowns().isOnCooldown(stack.getItem())) {
            if (!NBTUtils.getBoolean(stack, TAG_IS_THROWN, false)) {
                SpaceDissectorEntity entity = new SpaceDissectorEntity(worldIn, playerIn);
                entity.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 1.0F, config.projectileSpeed, 0.0F);
                NBTUtils.setBoolean(stack, TAG_IS_THROWN, true);
                NBTUtils.setString(stack, TAG_UUID, entity.getUUID().toString());
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                entity.stack = playerIn.getMainHandItem();
                entity.setOwner(playerIn);
                worldIn.addFreshEntity(entity);
            } else {
                if (!NBTUtils.getString(stack, TAG_UUID, "").equals("")) {
                    UUID uuid = UUID.fromString(NBTUtils.getString(stack, TAG_UUID, ""));
                    if (worldIn instanceof ServerWorld) {
                        if (((ServerWorld) worldIn).getEntity(uuid) != null) {
                            if (((ServerWorld) worldIn).getEntity(uuid) instanceof SpaceDissectorEntity) {
                                SpaceDissectorEntity dissector = (SpaceDissectorEntity) ((ServerWorld) worldIn).getEntity(uuid);
                                if (!playerIn.isShiftKeyDown()) {
                                    if (!dissector.getEntityData().get(SpaceDissectorEntity.IS_RETURNING)) {
                                        dissector.getEntityData().set(SpaceDissectorEntity.IS_RETURNING, true);
                                    }
                                } else {
                                    if (playerIn.position().distanceTo(dissector.position()) > config.distanceForTeleport) {
                                        playerIn.teleportTo(dissector.getX(), dissector.getY(), dissector.getZ());
                                        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(),
                                                SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                        dissector.remove();
                                        playerIn.getCooldowns().addCooldown(stack.getItem(), config.cooldownAfterTeleport * 20);
                                        NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
                                    }
                                }
                            }
                        } else {
                            playerIn.getCooldowns().addCooldown(stack.getItem(), config.cooldownAfterTeleport * 20);
                            NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
                        }
                    }
                }
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn.tickCount % 20 == 0 && NBTUtils.getBoolean(stack, TAG_IS_THROWN, false)) {
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) + 1);
            if (NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0) > config.maxThrownTime) {
                NBTUtils.setBoolean(stack, TAG_IS_THROWN, false);
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.space_dissector.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.space_dissector.shift_2"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.space_dissector.shift_3"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.END_CITY_TREASURE);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    public static class Stats extends RelicStats {
        public float projectileSpeed = 0.75F;
        public int maxThrownTime = 60;
        public int timeBeforeReturn = 10;
        public int distanceForTeleport = 5;
        public int cooldownAfterTeleport = 30;
        public int cooldownAfterReturn = 5;
        public int maxBounces = 10;
        public int additionalTimeAfterBounce = 4;
        public int baseDamage = 4;
        public float damageMultiplierPerBounce = 2.0F;
    }
}