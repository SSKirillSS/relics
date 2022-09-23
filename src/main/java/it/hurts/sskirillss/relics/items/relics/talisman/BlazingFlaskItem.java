package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import it.hurts.sskirillss.relics.utils.WorldUtils;
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!(entity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        Level world = player.getCommandSenderWorld();

        if (player.tickCount % 20 == 0) {
            int fire = getFireAround(stack, world);

            if (!player.isSpectator() && !player.isCreative())
                player.getAbilities().mayfly = fire > 0;

            if (fire <= 0) {
                NBTUtils.clearTag(stack, TAG_POSITION);
                NBTUtils.clearTag(stack, TAG_COUNT);
            } else {
                NBTUtils.setInt(stack, TAG_COUNT, fire);
            }
        }

        Vec3 center = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));

        if (center != null) {
            float radius = NBTUtils.getFloat(stack, TAG_RADIUS, 0F);

            if (!player.isCreative() && !player.isSpectator()) {
                if (new Vec3(player.getX(), center.y(), player.getZ()).distanceTo(center) <= radius + 0.5F)
                    player.getAbilities().mayfly = true;
                else {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                }

                if (player.getAbilities().flying) {
                    float speed = 0.5F;

                    if (player.zza != 0 || player.xxa != 0 || player.yya != 0)
                        player.setDeltaMovement(player.getDeltaMovement().multiply(speed, 1F, speed));

                    Vec3 motion = player.getDeltaMovement();

                    float height = 3;

                    if (!player.isShiftKeyDown() && player instanceof LocalPlayer localPlayer && localPlayer.input.jumping)
                        player.setDeltaMovement(motion.x(), Math.min(0.3, 0.04 * ((getGroundHeight(player)
                                - (player.getY() - height)))), motion.z());

                    if (player.getY() - height > getGroundHeight(player)) {
                        player.setDeltaMovement(motion.x(), -Math.min(player.getY() - height
                                - getGroundHeight(player), 2) / 8, motion.z());
                    }
                }
            }

            float size = NBTUtils.getInt(stack, TAG_COUNT, 0) * 5;

            float step = 0.1F;

            int time = 0;

            if (radius < size) {
                if (radius + step < size)
                    radius += 0.1F;
                else
                    radius = size;

                time = 10;

                NBTUtils.setFloat(stack, TAG_RADIUS, radius);
            }

            if (radius > size) {
                if (radius - step > size)
                    radius -= step;
                else
                    radius = size;

                time = 10;

                NBTUtils.setFloat(stack, TAG_RADIUS, radius);
            }

            if (radius <= step)
                ParticleUtils.createBall(new CircleTintData(new Color(255, 100, 0), 0.3F, 60, 0.9F, true),
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

    protected void handleLevitation(Player player) {
        double riseVelocity = 0.0D;

        player.getAbilities().flying = true;

        player.setDeltaMovement(player.getDeltaMovement().multiply(0.75, 0.75, 0.75));

        Vec3 motion = player.getDeltaMovement();

        if (player.zza > 0)
            player.setDeltaMovement(motion.x() + new Vec3(player.getLookAngle().x,
                            0, player.getLookAngle().z).normalize().x() * 0.025F, motion.y(),
                    motion.z() + new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z).normalize().z() * 0.025F);

        if (player.getCommandSenderWorld().isClientSide() && ((LocalPlayer) player).input.jumping)
            riseVelocity = 0.04D;

        if (!player.isShiftKeyDown())
            player.setDeltaMovement(motion.x(), riseVelocity * ((getGroundHeight(player)
                    - (player.getY() - 5))), motion.z());

        if (player.getY() - 5 > getGroundHeight(player)) {
            if (motion.y() > 0)
                player.setDeltaMovement(motion.x(), 0, motion.z());

            player.setDeltaMovement(motion.x(), -Math.min(player.getY() - 5
                    - getGroundHeight(player), 2) / 8, motion.z());
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        Vec3 view = player.getViewVector(0);
        Vec3 eyeVec = player.getEyePosition(0);

        float distance = 32;

        Vec3 end = level.clip(new ClipContext(eyeVec, eyeVec.add(view.x * distance, view.y * distance,
                view.z * distance), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getLocation();

        if (getFireAround(end, level) > 0) {
            NBTUtils.setString(stack, TAG_POSITION, NBTUtils.writePosition(end));
            NBTUtils.setFloat(stack, TAG_RADIUS, 0F);

            player.getCooldowns().addCooldown(this, 20);
        }

        return InteractionResultHolder.pass(stack);
    }

    public int getFireAround(ItemStack stack, Level level) {
        Vec3 center = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));

        if (center == null)
            return 0;

        return getFireAround(center, level);
    }

    public int getFireAround(Vec3 center, Level level) {
        List<BlockPos> positions = WorldUtils.getBlockSphere(new BlockPos(center), 10)
                .stream().filter(pos -> (level.getBlockState(pos).getBlock() instanceof BaseFireBlock)).toList();

        return positions.size();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.getWearer() instanceof Player player))
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