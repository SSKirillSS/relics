package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigDataOld;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BlazingFlaskItem extends RelicItem<BlazingFlaskItem.Stats> {
    public static final String TAG_FIRE_AMOUNT = "fire";

    public BlazingFlaskItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .build());
    }

    @Override
    public RelicStyleData getStyle(ItemStack stack) {
        return RelicStyleData.builder()
                .borders("#e09614", "#302a44")
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
                        .active(Minecraft.getInstance().options.keyJump.getKey().getDisplayName().getString() + " x2")
                        .build())
                .build();
    }

    @Override
    public RelicConfigDataOld<Stats> getConfigData() {
        return RelicConfigDataOld.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);

        if (fire <= 0)
            return;

        tooltip.add(new TranslatableComponent("tooltip.relics.blazing_flask.tooltip_1", fire));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        Level world = player.getCommandSenderWorld();
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);

        if (!player.isSpectator() && !player.isCreative()) {
            player.getAbilities().mayfly = fire > 0;

            if (player.getAbilities().flying) {
                handleIgnite(player);

                handleLevitation(player, stack);
            }
        }

        collectFire(player, stack);

        if (fire <= 0 || player.tickCount % 20 != 0 || !(world.isRainingAt(player.blockPosition()) || player.isInWater()))
            return;

        world.playSound(player, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 1.0F);

        NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire - 1);
    }

    protected double getGroundHeight(Player player) {
        HitResult result = player.level.clip(new ClipContext(player.position(), new Vec3(player.getX(),
                player.getY() - 64, player.getZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));

        if (result.getType() == HitResult.Type.BLOCK)
            return result.getLocation().y();

        return -player.getCommandSenderWorld().getMaxBuildHeight();
    }

    protected void handleIgnite(Player player) {
        Level world = player.getCommandSenderWorld();

        for (int i = 0; i < 3; i++)
            world.addParticle(player.isInWater() ? ParticleTypes.CLOUD : ParticleTypes.LARGE_SMOKE,
                    player.getX() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    player.getY() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    player.getZ() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    0, player.isInWater() ? 0 : -0.1, 0);

        if (player.isInWater())
            return;

        world.addParticle(world.dimension() == Level.NETHER ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                player.getX() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                player.getY() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                player.getZ() + MathUtils.randomFloat(world.getRandom()) * 0.5F, 0, -0.25F, 0);

        for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox()
                .inflate(0.5D).expandTowards(0, -getGroundHeight(player) - 1, 0))) {
            if (entity == player)
                continue;

            entity.setSecondsOnFire(5);
        }
    }

    protected void handleLevitation(Player player, ItemStack stack) {
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);
        double riseVelocity = 0.0D;

        player.getAbilities().flying = fire > 0;

        player.setDeltaMovement(player.getDeltaMovement().multiply(stats.levitationSpeed, stats.levitationSpeed, stats.levitationSpeed));

        Vec3 motion = player.getDeltaMovement();

        if (player.zza > 0)
            player.setDeltaMovement(motion.x() + new Vec3(player.getLookAngle().x,
                            0, player.getLookAngle().z).normalize().x() * 0.025F, motion.y(),
                    motion.z() + new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z).normalize().z() * 0.025F);

        if (player.getCommandSenderWorld().isClientSide() && ((LocalPlayer) player).input.jumping)
            riseVelocity = 0.04D;

        if (!player.isShiftKeyDown())
            player.setDeltaMovement(motion.x(), riseVelocity * ((getGroundHeight(player)
                    - (player.getY() - stats.levitationHeight))), motion.z());

        if (player.getY() - stats.levitationHeight > getGroundHeight(player)) {
            if (motion.y() > 0)
                player.setDeltaMovement(motion.x(), 0, motion.z());

            player.setDeltaMovement(motion.x(), -Math.min(player.getY() - stats.levitationHeight
                    - getGroundHeight(player), 2) / 8, motion.z());
        }

        if (player.tickCount % 20 == 0)
            NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire - 1);
    }

    protected void collectFire(Player player, ItemStack stack) {
        Level world = player.getCommandSenderWorld();
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);

        if (player.isSpectator() || fire >= stats.capacity)
            return;

        List<BlockPos> positions = WorldUtils.getBlockSphere(player.blockPosition(), stats.consumptionRadius).stream()
                .filter(pos -> (world.getBlockState(pos).getBlock() instanceof BaseFireBlock)).collect(Collectors.toList());

        for (BlockPos pos : positions) {
            Vec3 blockVec = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            double distance = player.position().add(0, 1, 0).distanceTo(blockVec);
            Vec3 direction = player.position().add(0, 1, 0).subtract(blockVec).normalize();

            world.addParticle(new CircleTintData((world.getBlockState(pos).getBlock() instanceof SoulFireBlock) ? new Color(0, 200, 255)
                            : new Color(255, 122, 0), (float) (distance * 0.075F), (int) distance * 5, 0.95F, false),
                    blockVec.x(), blockVec.y(), blockVec.z(), direction.x * 0.2F, direction.y * 0.2F, direction.z * 0.2F);

            if (player.tickCount % stats.consumptionCooldown == 0) {
                world.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.PLAYERS, 1.0F, 1.0F);

                NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, Math.min(stats.capacity, fire + positions.size()));
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.getWearer() instanceof Player player) || NBTUtils.getInt(newStack, TAG_FIRE_AMOUNT, 0) > 0)
            return;

        if (player.isCreative() || player.isSpectator())
            return;

        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;

        player.onUpdateAbilities();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class BlazingFlaskServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();

            if (!EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.BLAZING_FLASK.get()).isEmpty()
                    && (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE))
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onEntityAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.BLAZING_FLASK.get()).isEmpty()
                    && (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE))
                event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public float levitationHeight = 5.0F;
        public float levitationSpeed = 0.75F;
        public int consumptionCooldown = 20;
        public int consumptionRadius = 10;
        public int capacity = 100;
    }
}