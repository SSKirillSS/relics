package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.ShadowGlaiveEntity;
import it.hurts.sskirillss.relics.entities.ShadowSawEntity;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
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

public class ShadowGlaiveItem extends RelicItem {
    public static final String TAG_CHARGES = "charges";
    public static final String TAG_TIME = "time";
    public static final String TAG_SAW = "saw";

    @Override
    public RelicData constructRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("glaive", RelicAbilityEntry.builder()
                                .stat("recharge", RelicAbilityStat.builder()
                                        .initialValue(30D, 10D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.09D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat("bounces", RelicAbilityStat.builder()
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat("radius", RelicAbilityStat.builder()
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability("saw", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .stat("speed", RelicAbilityStat.builder()
                                        .initialValue(15D, 20D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, -1D)
                                        .formatValue(value -> MathUtils.round(20 - Math.max(1, value), 1))
                                        .build())
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(0.75D, 1.5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#ffe0d2", "#9c756b")
                        .build())
                .build();
    }

    @Override
    public List<ItemStack> processCreativeTab() {
        ItemStack stack = this.getDefaultInstance();

        NBTUtils.setInt(stack, TAG_CHARGES, 8);

        return Lists.newArrayList(stack);
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

        if (time >= AbilityUtils.getAbilityValue(stack, "glaive", "recharge")) {
            NBTUtils.setInt(stack, TAG_CHARGES, charges + 1);
            NBTUtils.setInt(stack, TAG_TIME, 0);
        } else
            NBTUtils.setInt(stack, TAG_TIME, time + 1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        int charges = NBTUtils.getInt(stack, TAG_CHARGES, 0);
        RandomSource random = playerIn.getRandom();

        if (playerIn.getCooldowns().isOnCooldown(stack.getItem()))
            return InteractionResultHolder.fail(stack);

        ShadowSawEntity entity = getSaw(stack, worldIn);

        if (entity != null) {
            if (!entity.isReturning)
                entity.isReturning = true;
        } else {
            if (playerIn.isShiftKeyDown() && LevelingUtils.getLevel(stack) >= 5) {
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