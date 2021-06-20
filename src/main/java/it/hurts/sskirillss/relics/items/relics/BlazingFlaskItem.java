package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BlazingFlaskItem extends RelicItem<BlazingFlaskItem.Stats> implements ICurioItem {
    public static final String TAG_FIRE_AMOUNT = "fire";

    public BlazingFlaskItem() {
        super(Rarity.EPIC);
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.blazing_flask.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.blazing_flask.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);
        if (fire <= 0) return;
        tooltip.add(new TranslationTextComponent("tooltip.relics.blazing_flask.tooltip_1", fire));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity)) return;
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
        if (fire <= 0 || player.tickCount % 20 != 0 || !(world.isRainingAt(player.blockPosition()) || player.isInWater())) return;
        world.playSound(player, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 1.0F);
        NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire - 1);
    }

    protected double getGroundHeight(PlayerEntity player) {
        RayTraceResult result = player.level.clip(new RayTraceContext(player.position(), new Vector3d(player.getX(),
                player.getY() - 64, player.getZ()), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, player));
        if (result.getType() == RayTraceResult.Type.BLOCK) return result.getLocation().y();
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
        if (player.isInWater()) return;
        world.addParticle(ParticleTypes.FLAME,
                player.getX() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                player.getY() + MathUtils.randomFloat(world.getRandom()) * 0.5F,
                player.getZ() + MathUtils.randomFloat(world.getRandom()) * 0.5F, 0, -0.25F, 0);
        for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox()
                .inflate(0.5D).expandTowards(0, -getGroundHeight(player) - 1, 0))) {
            if (entity == player) continue;
            entity.setSecondsOnFire(config.igniteDuration);
        }
    }

    protected void handleLevitation(PlayerEntity player, ItemStack stack) {
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);
        double riseVelocity = 0.0D;
        player.fallDistance = 0.0F;
        player.abilities.flying = fire > 0;
        player.setDeltaMovement(player.getDeltaMovement().multiply(config.levitationSpeed, config.levitationSpeed, config.levitationSpeed));
        Vector3d motion = player.getDeltaMovement();
        if (player.zza > 0) player.setDeltaMovement(motion.x() + new Vector3d(player.getLookAngle().x,
                        0, player.getLookAngle().z).normalize().x() * 0.025F, motion.y(),
                motion.z() + new Vector3d(player.getLookAngle().x, 0, player.getLookAngle().z).normalize().z() * 0.025F);
        if (player instanceof ClientPlayerEntity && ((ClientPlayerEntity) player).input.jumping) riseVelocity = 0.04D;
        if (!player.isShiftKeyDown()) player.setDeltaMovement(motion.x(), riseVelocity * ((getGroundHeight(player)
                - (player.getY() - config.levitationHeight))), motion.z());
        if (player.getY() - config.levitationHeight > getGroundHeight(player)) {
            if (motion.y() > 0) player.setDeltaMovement(motion.x(), 0, motion.z());
            player.setDeltaMovement(motion.x(), -Math.min(player.getY() - config.levitationHeight
                    - getGroundHeight(player), 2) / 8, motion.z());
        }
        if (player.tickCount % 20 == 0) NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire - 1);
    }

    protected void collectFire(PlayerEntity player, ItemStack stack) {
        World world = player.getCommandSenderWorld();
        int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);
        if (world.isClientSide() || player.isSpectator() || player.tickCount % config.consumptionCooldown != 0 || fire >= config.capacity) return;
        for (BlockPos pos : WorldUtils.getBlockSphere(player.blockPosition(), config.consumptionRadius)) {
            if (!(world.getBlockState(pos).getBlock() instanceof AbstractFireBlock)) continue;
            world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, 1.0F);
            ((ServerWorld) world).sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5F,
                    pos.getY() + 0.5F, pos.getZ() + 0.5F, 5, 0.35, 0.2, 0.35, 0.01);
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire + 1);
            break;
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.getWearer() instanceof PlayerEntity) || NBTUtils.getInt(newStack, TAG_FIRE_AMOUNT, 0) > 0) return;
        PlayerEntity player = (PlayerEntity) slotContext.getWearer();
        player.abilities.mayfly = false;
        player.abilities.flying = false;
        player.onUpdateAbilities();
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.NETHER;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class BlazingFlaskServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            DamageSource source = event.getSource();
            if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE) {
                if (!CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BLAZING_FLASK.get(), player).isPresent()) return;
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onEntityAttack(LivingAttackEvent event) {
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            DamageSource source = event.getSource();
            if (source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE) {
                if (!CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BLAZING_FLASK.get(), player).isPresent()) return;
                event.setCanceled(true);
            }
        }
    }

    public static class Stats extends RelicStats {
        public float levitationHeight = 5.0F;
        public float levitationSpeed = 0.75F;
        public int igniteDuration = 5;
        public int consumptionCooldown = 10;
        public int consumptionRadius = 10;
        public int capacity = 100;
    }
}