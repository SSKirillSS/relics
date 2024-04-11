package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.entities.ShadowGlaiveEntity;
import it.hurts.sskirillss.relics.entities.ShadowSawEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public class ShadowGlaiveItem extends RelicItem {
    public static final String TAG_CHARGES = "charges";
    public static final String TAG_TIME = "time";
    public static final String TAG_SAW = "saw";

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("glaive")
                                .stat(StatData.builder("recharge")
                                        .initialValue(30D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.09D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("bounces")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat(StatData.builder("damage")
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("saw")
                                .requiredLevel(5)
                                .stat(StatData.builder("speed")
                                        .initialValue(20D, 15D)
                                        .upgradeModifier(UpgradeOperation.ADD, -1D)
                                        .formatValue(value -> MathUtils.round(value / 20, 2))
                                        .build())
                                .stat(StatData.builder("damage")
                                        .initialValue(0.75D, 1.5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .style(StyleData.builder()
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.END)
                        .entry(LootCollections.SCULK)
                        .build())
                .build();
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        if (group != RelicsTab.RELICS_TAB)
            return;

        ItemStack stack = new ItemStack(ItemRegistry.SHADOW_GLAIVE.get());

        NBTUtils.setInt(stack, TAG_CHARGES, 8);

        items.add(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isClientSide())
            return;

        int charges = NBTUtils.getInt(stack, TAG_CHARGES, 0);

        if (entityIn.tickCount % 20 != 0 || charges >= 8)
            return;

        int time = NBTUtils.getInt(stack, TAG_TIME, 0);

        if (getSaw(stack, worldIn) != null)
            return;

        if (time >= getAbilityValue(stack, "glaive", "recharge")) {
            NBTUtils.setInt(stack, TAG_CHARGES, charges + 1);
            NBTUtils.setInt(stack, TAG_TIME, 0);
        } else
            NBTUtils.setInt(stack, TAG_TIME, time + 1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        int charges = NBTUtils.getInt(stack, TAG_CHARGES, 0);
        Random random = playerIn.getRandom();

        if (playerIn.getCooldowns().isOnCooldown(stack.getItem()))
            return InteractionResultHolder.fail(stack);

        ShadowSawEntity entity = getSaw(stack, worldIn);

        if (entity != null) {
            if (!entity.isReturning)
                entity.isReturning = true;
        } else {
            if (playerIn.isShiftKeyDown() && canUseAbility(stack, "saw")) {
                if (charges == 8 && getSaw(stack, worldIn) == null) {
                    ShadowSawEntity saw = new ShadowSawEntity(stack, playerIn);

                    saw.setStack(stack);
                    saw.setOwner(playerIn);
                    saw.teleportTo(playerIn.getX(), playerIn.getY() + playerIn.getBbHeight() * 0.5F, playerIn.getZ());
                    saw.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.75F, 1, 0.0F);

                    worldIn.addFreshEntity(saw);

                    worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundRegistry.THROW.get(),
                            SoundSource.MASTER, 0.5F, 0.35F + (random.nextFloat() * 0.25F));

                    NBTUtils.setInt(stack, TAG_CHARGES, 0);
                    NBTUtils.setString(stack, TAG_SAW, saw.getStringUUID());
                }
            } else {
                if (charges > 0) {
                    ShadowGlaiveEntity glaive = new ShadowGlaiveEntity(worldIn, playerIn);

                    glaive.setStack(stack);
                    glaive.setOwner(playerIn);
                    glaive.teleportTo(playerIn.getX(), playerIn.getY() + playerIn.getBbHeight() * 0.5F, playerIn.getZ());
                    glaive.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.75F, 1, 0.0F);

                    worldIn.addFreshEntity(glaive);

                    EntityHitResult result = EntityUtils.rayTraceEntity(playerIn, entry -> !EntityUtils.isAlliedTo(playerIn, entry), 32);

                    if (result != null) {
                        if (result.getEntity() instanceof LivingEntity target)
                            glaive.setTarget(target);
                    }

                    worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundRegistry.THROW.get(),
                            SoundSource.MASTER, 0.5F, 0.75F + (random.nextFloat() * 0.5F));

                    NBTUtils.setInt(stack, TAG_CHARGES, charges - 1);
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Nullable
    public ShadowSawEntity getSaw(ItemStack stack, Level level) {
        try {
            UUID uuid = UUID.fromString(NBTUtils.getString(stack, TAG_SAW, ""));

            if (level.isClientSide())
                return null;

            ServerLevel serverLevel = (ServerLevel) level;
            Entity entity = serverLevel.getEntity(uuid);

            if (entity instanceof ShadowSawEntity saw)
                return saw;

            NBTUtils.clearTag(stack, TAG_SAW);

            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}