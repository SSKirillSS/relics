package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.BlazingFlaskModel;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BlazingFlaskItem extends RelicItem<BlazingFlaskItem.Stats> implements ICurioItem {
    public static final String TAG_FIRE_AMOUNT = "fire";

    public BlazingFlaskItem() {
        super(RelicData.builder()
                .rarity(Rarity.EPIC)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.NETHER)
                        .chance(0.1F)
                        .build())
                .build());
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .active(Minecraft.getInstance().options.keyJump.getKey().getDisplayName().getString() + " x2")
                        .build())
                .build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);

        if (fire <= 0)
            return;

        tooltip.add(new TranslationTextComponent("tooltip.relics.blazing_flask.tooltip_1", fire));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || isBroken(stack))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        World world = player.getCommandSenderWorld();
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);

        if (!player.isSpectator() && !player.isCreative()) {
            player.abilities.mayfly = fire > 0;

            if (player.abilities.flying) {
                handleIgnite(player);

                handleLevitation(player, stack);
            }
        }

        collectFire(player, stack);

        if (fire <= 0 || player.tickCount % 20 != 0 || !(world.isRainingAt(player.blockPosition()) || player.isInWater()))
            return;

        world.playSound(player, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 1.0F);

        NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire - 1);
    }

    protected double getGroundHeight(PlayerEntity player) {
        RayTraceResult result = player.level.clip(new RayTraceContext(player.position(), new Vector3d(player.getX(),
                player.getY() - 64, player.getZ()), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, player));

        if (result.getType() == RayTraceResult.Type.BLOCK)
            return result.getLocation().y();

        return -player.getCommandSenderWorld().getMaxBuildHeight();
    }

    protected void handleIgnite(PlayerEntity player) {
        World world = player.getCommandSenderWorld();

        for (int i = 0; i < 3; i++)
            world.addParticle(player.isInWater() ? ParticleTypes.CLOUD : ParticleTypes.LARGE_SMOKE,
                    player.getX() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    player.getY() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    player.getZ() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                    0, player.isInWater() ? 0 : -0.1, 0);

        if (player.isInWater())
            return;

        world.addParticle(world.dimension() == World.NETHER ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
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

    protected void handleLevitation(PlayerEntity player, ItemStack stack) {
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);
        double riseVelocity = 0.0D;

        player.abilities.flying = fire > 0;

        player.setDeltaMovement(player.getDeltaMovement().multiply(config.levitationSpeed, config.levitationSpeed, config.levitationSpeed));

        Vector3d motion = player.getDeltaMovement();

        if (player.zza > 0)
            player.setDeltaMovement(motion.x() + new Vector3d(player.getLookAngle().x,
                            0, player.getLookAngle().z).normalize().x() * 0.025F, motion.y(),
                    motion.z() + new Vector3d(player.getLookAngle().x, 0, player.getLookAngle().z).normalize().z() * 0.025F);

        if (player.getCommandSenderWorld().isClientSide() && player instanceof ClientPlayerEntity && ((ClientPlayerEntity) player).input.jumping)
            riseVelocity = 0.04D;

        if (!player.isShiftKeyDown())
            player.setDeltaMovement(motion.x(), riseVelocity * ((getGroundHeight(player)
                    - (player.getY() - config.levitationHeight))), motion.z());

        if (player.getY() - config.levitationHeight > getGroundHeight(player)) {
            if (motion.y() > 0)
                player.setDeltaMovement(motion.x(), 0, motion.z());

            player.setDeltaMovement(motion.x(), -Math.min(player.getY() - config.levitationHeight
                    - getGroundHeight(player), 2) / 8, motion.z());
        }

        if (player.tickCount % 20 == 0)
            NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire - 1);
    }

    protected void collectFire(PlayerEntity player, ItemStack stack) {
        World world = player.getCommandSenderWorld();
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);

        if (player.isSpectator() || fire >= config.capacity)
            return;

        List<BlockPos> positions = WorldUtils.getBlockSphere(player.blockPosition(), config.consumptionRadius).stream()
                .filter(pos -> (world.getBlockState(pos).getBlock() instanceof AbstractFireBlock)).collect(Collectors.toList());

        for (BlockPos pos : positions) {
            Vector3d blockVec = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            double distance = player.position().add(0, 1, 0).distanceTo(blockVec);
            Vector3d direction = player.position().add(0, 1, 0).subtract(blockVec).normalize();

            world.addParticle(new CircleTintData((world.getBlockState(pos).getBlock() instanceof SoulFireBlock) ? new Color(0, 200, 255)
                            : new Color(255, 122, 0), (float) (distance * 0.075F), (int) distance * 5, 0.95F, false),
                    blockVec.x(), blockVec.y(), blockVec.z(), direction.x * 0.2F, direction.y * 0.2F, direction.z * 0.2F);

            if (player.tickCount % config.consumptionCooldown == 0) {
                world.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.PLAYERS, 1.0F, 1.0F);

                NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, Math.min(config.capacity, fire + positions.size()));
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.getWearer() instanceof PlayerEntity) || NBTUtils.getInt(newStack, TAG_FIRE_AMOUNT, 0) > 0)
            return;

        PlayerEntity player = (PlayerEntity) slotContext.getWearer();

        if (player.isCreative() || player.isSpectator())
            return;

        player.abilities.mayfly = false;
        player.abilities.flying = false;

        player.onUpdateAbilities();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new BlazingFlaskModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class BlazingFlaskServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();

            if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE)
                CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BLAZING_FLASK.get(),
                        event.getEntityLiving()).ifPresent(triple -> {
                    if (!isBroken(triple.getRight()))
                        event.setCanceled(true);
                });
        }

        @SubscribeEvent
        public static void onEntityAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE)
                CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BLAZING_FLASK.get(),
                        event.getEntityLiving()).ifPresent(triple -> {
                    if (!isBroken(triple.getRight()))
                        event.setCanceled(true);
                });
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