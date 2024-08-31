package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.entities.ShadowGlaiveEntity;
import it.hurts.sskirillss.relics.entities.ShadowSawEntity;
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
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.*;

public class ShadowGlaiveItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("glaive")
                                .stat(StatData.builder("recharge")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(30D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.09D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("bounces")
                                        .icon(StatIcons.REFLECT)
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat(StatData.builder("damage")
                                        .icon(StatIcons.DEALT_DAMAGE)
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("radius")
                                        .icon(StatIcons.DISTANCE)
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("saw")
                                .requiredLevel(5)
                                .stat(StatData.builder("speed")
                                        .icon(StatIcons.SPEED)
                                        .initialValue(20D, 15D)
                                        .upgradeModifier(UpgradeOperation.ADD, -1D)
                                        .formatValue(value -> MathUtils.round(value / 20, 2))
                                        .build())
                                .stat(StatData.builder("damage")
                                        .icon(StatIcons.DEALT_DAMAGE)
                                        .initialValue(0.75D, 1.5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.END)
                        .entry(LootCollections.SCULK)
                        .build())
                .build();
    }

    @Override
    public List<ItemStack> processCreativeTab() {
        ItemStack stack = this.getDefaultInstance();

        stack.set(CHARGE, 8);

        return Lists.newArrayList(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isClientSide())
            return;

        int charges = stack.getOrDefault(CHARGE, 0);

        if (entityIn.tickCount % 20 != 0 || charges >= 8)
            return;

        int time = stack.getOrDefault(TIME, 0);

        if (getSaw(stack, worldIn) != null)
            return;

        if (time >= getStatValue(stack, "glaive", "recharge")) {
            stack.set(CHARGE, charges + 1);
            stack.set(TIME, 0);
        } else
            stack.set(TIME, ++time);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        int charges = stack.getOrDefault(CHARGE, 0);
        RandomSource random = playerIn.getRandom();

        if (playerIn.getCooldowns().isOnCooldown(stack.getItem()))
            return InteractionResultHolder.fail(stack);

        ShadowSawEntity entity = getSaw(stack, worldIn);

        if (entity != null) {
            if (!entity.isReturning)
                entity.isReturning = true;
        } else {
            if (playerIn.isShiftKeyDown() && isAbilityUnlocked(stack, "saw")) {
                if (charges == 8 && getSaw(stack, worldIn) == null) {
                    ShadowSawEntity saw = new ShadowSawEntity(stack, playerIn);

                    saw.setStack(stack);
                    saw.setOwner(playerIn);
                    saw.teleportTo(playerIn.getX(), playerIn.getY() + playerIn.getBbHeight() * 0.5F, playerIn.getZ());
                    saw.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.75F, 1, 0.0F);

                    worldIn.addFreshEntity(saw);

                    worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundRegistry.THROW.get(),
                            SoundSource.MASTER, 0.5F, 0.35F + (random.nextFloat() * 0.25F));

                    stack.set(CHARGE, 0);
                    stack.set(SAW, saw.getStringUUID());
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

                    stack.set(CHARGE, charges - 1);
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Nullable
    public ShadowSawEntity getSaw(ItemStack stack, Level level) {
        try {
            UUID uuid = UUID.fromString(stack.getOrDefault(SAW, ""));

            if (level.isClientSide())
                return null;

            ServerLevel serverLevel = (ServerLevel) level;
            Entity entity = serverLevel.getEntity(uuid);

            if (entity instanceof ShadowSawEntity saw)
                return saw;

            stack.set(SAW, "");

            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }
}