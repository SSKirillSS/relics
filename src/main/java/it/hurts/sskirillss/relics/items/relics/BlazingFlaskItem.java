package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.indev.*;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.List;

public class BlazingFlaskItem extends RelicItem {
    public static final String TAG_POSITION = "pos";
    public static final String TAG_COUNT = "count";
    public static final String TAG_RADIUS = "radius";

    public BlazingFlaskItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());
    }

    @Override
    public RelicDataNew getNewData() {
        return RelicDataNew.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("bonfire", RelicAbilityEntry.builder()
                                .stat("step", RelicAbilityStat.builder()
                                        .initialValue(1, 3)
                                        .upgradeModifier("add", 0.5D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .stat("speed", RelicAbilityStat.builder()
                                        .initialValue(0.25D, 0.5D)
                                        .upgradeModifier("add", 0.05D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .stat("height", RelicAbilityStat.builder()
                                        .initialValue(3, 5)
                                        .upgradeModifier("add", 1D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#eed551", "#dcbe1d")
                        .build())
                .build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!(entity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        Level world = player.getCommandSenderWorld();

        int fire = getFireAround(stack, world);

        if (!player.isSpectator() && !player.isCreative())
            player.getAbilities().mayfly = fire > 0;

        if (fire <= 0) {
            NBTUtils.clearTag(stack, TAG_POSITION);
            NBTUtils.clearTag(stack, TAG_COUNT);
        } else {
            NBTUtils.setInt(stack, TAG_COUNT, fire);
        }

        Vec3 center = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));

        if (center != null) {
            double radius = NBTUtils.getDouble(stack, TAG_RADIUS, 0D);

            if (!player.isCreative() && !player.isSpectator()) {
                if (new Vec3(player.getX(), center.y(), player.getZ()).distanceTo(center) <= radius + 0.5F)
                    player.getAbilities().mayfly = true;
                else {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                }

                if (player.getAbilities().flying) {
                    double speed = Math.min(1D, getAbilityValue(stack, "bonfire", "speed"));

                    if (player.zza != 0 || player.xxa != 0 || player.yya != 0)
                        player.setDeltaMovement(player.getDeltaMovement().multiply(speed, 1D, speed));

                    Vec3 motion = player.getDeltaMovement();

                    double height = getAbilityValue(stack, "bonfire", "height");

                    if (!player.isShiftKeyDown() && player instanceof LocalPlayer localPlayer && localPlayer.input.jumping)
                        player.setDeltaMovement(motion.x(), Math.min(0.3, 0.04 * ((getGroundHeight(player)
                                - (player.getY() - height)))), motion.z());
                }
            }

            double size = NBTUtils.getInt(stack, TAG_COUNT, 0) * getAbilityValue(stack, "bonfire", "step");

            double step = 0.1D;

            int time = 0;

            if (radius < size) {
                if (radius + step < size)
                    radius += step;
                else
                    radius = size;

                time = 10;

                NBTUtils.setDouble(stack, TAG_RADIUS, radius);
            }

            if (radius > size) {
                if (radius - step > size)
                    radius -= step;
                else
                    radius = size;

                time = 10;

                NBTUtils.setDouble(stack, TAG_RADIUS, radius);
            }

            if (radius <= step)
                ParticleUtils.createBall(new CircleTintData(new Color(255, 100, 0), 0.3F, 20, 0.9F, true),
                        center, level, 3, 0.2F);

            ParticleUtils.createCyl(new CircleTintData(new Color(255, 100, 0), 0.2F, time, 0.8F, true),
                    center, level, radius, 0.15F);
        }
    }

    protected double getGroundHeight(Player player) {
        HitResult result = player.level.clip(new ClipContext(player.position(), new Vec3(player.getX(),
                player.getY() - 64, player.getZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));

        if (result.getType() == HitResult.Type.BLOCK)
            return result.getLocation().y();

        return -player.getCommandSenderWorld().getMaxBuildHeight();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        Vec3 view = player.getViewVector(0);
        Vec3 eyeVec = player.getEyePosition(0);

        float distance = (float) (8F + getAbilityValue(stack, "bonfire", "height"));

        Vec3 end = level.clip(new ClipContext(eyeVec, eyeVec.add(view.x * distance, view.y * distance,
                view.z * distance), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getLocation();

        if (getFireAround(stack, end, level) > 0) {
            Vec3 center = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));

            double radius = NBTUtils.getDouble(stack, TAG_RADIUS, 0D);

            NBTUtils.setDouble(stack, TAG_RADIUS, (center != null && end.distanceTo(center) <= radius) ? radius - center.distanceTo(end) : 0D);
            NBTUtils.setString(stack, TAG_POSITION, NBTUtils.writePosition(end));

            player.getCooldowns().addCooldown(this, 20);
        }

        return InteractionResultHolder.pass(stack);
    }

    public int getFireAround(ItemStack stack, Level level) {
        Vec3 center = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));

        if (center == null)
            return 0;

        return getFireAround(stack, center, level);
    }

    public int getFireAround(ItemStack stack, Vec3 center, Level level) {
        List<BlockPos> positions = WorldUtils.getBlockSphere(new BlockPos(center), getAbilityValue(stack, "bonfire", "step"))
                .stream().filter(pos -> (level.getBlockState(pos).getBlock() instanceof BaseFireBlock)).toList();

        return positions.size();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (player.isCreative() || player.isSpectator())
            return;

        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;

        player.onUpdateAbilities();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}