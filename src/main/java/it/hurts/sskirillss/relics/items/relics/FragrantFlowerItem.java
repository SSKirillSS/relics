package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FragrantFlowerItem extends RelicItem<FragrantFlowerItem.Stats> implements ICurioItem, IHasTooltip {
    public static final String TAG_NECTAR_AMOUNT = "nectar";
    public static final String TAG_UPDATE_TIME = "time";

    public static FragrantFlowerItem INSTANCE;

    public FragrantFlowerItem() {
        super(Rarity.UNCOMMON);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.fragrant_flower.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.fragrant_flower.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("tooltip.relics.fragrant_flower.tooltip_1", NBTUtils.getInt(stack, TAG_NECTAR_AMOUNT, 0)));
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            World world = player.getCommandSenderWorld();
            int nectar = NBTUtils.getInt(stack, TAG_NECTAR_AMOUNT, 0);
            int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
            for (BeeEntity bee : world.getEntitiesOfClass(BeeEntity.class,
                    player.getBoundingBox().inflate(config.luringRadius, config.luringRadius, config.luringRadius))) {
                if (bee.getPersistentAngerTarget() != null && bee.getPersistentAngerTarget().equals(player.getUUID())) {
                    bee.setLastHurtByMob(null);
                    bee.setPersistentAngerTarget(null);
                    bee.setTarget(null);
                    bee.setRemainingPersistentAngerTime(0);
                }
                if (bee.hasNectar() && nectar < config.capacity) {
                    bee.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), 1.0F);
                    if (player.position().distanceTo(bee.position()) < config.consumptionRadius) {
                        NBTUtils.setInt(stack, TAG_NECTAR_AMOUNT, nectar + 1);
                        bee.dropOffNectar();
                        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.BEE_POLLINATE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    }
                }
            }
            if (player.isShiftKeyDown()) {
                if (!player.getCooldowns().isOnCooldown(stack.getItem()) && nectar > 0) {
                    float radius = config.effectRadius;
                    double extraY = player.getY() + 0.5F;
                    for (int i = 0; i < 5; i++) {
                        float angle = (0.01F * (player.tickCount * 3 + i * 125));
                        double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle))) + player.getX();
                        double extraZ = (double) (radius * MathHelper.cos(angle)) + player.getZ();
                        world.addParticle(new CircleTintData(
                                        new Color(1.0F, 0.4F, 0.7F), 0.5F,
                                        30, 0.95F, false),
                                extraX, extraY, extraZ, 0F, 0F, 0F);
                    }
                    for (int i = 0; i < 2; i++) {
                        float angle = (-0.02F * (player.tickCount * 3 + i * 160));
                        double extraX = (double) (radius * 0.5F * MathHelper.sin((float) (Math.PI + angle))) + player.getX();
                        double extraZ = (double) (radius * 0.5F * MathHelper.cos(angle)) + player.getZ();
                        world.addParticle(new CircleTintData(
                                        new Color(0.2F, 1.0F, 0.0F), 0.5F,
                                        40, 0.95F, false),
                                extraX, extraY, extraZ, 0F, 0F, 0F);
                    }
                    if (player.tickCount % 20 == 0) {
                        List<BlockPos> sphere = WorldUtils.getBlockSphere(player.blockPosition(), radius);
                        if (time < 5) {
                            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                        } else {
                            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                            NBTUtils.setInt(stack, TAG_NECTAR_AMOUNT, nectar - 1);
                            player.getCooldowns().addCooldown(stack.getItem(), 10 * 20);
                            ParticleUtils.createBall(new CircleTintData(
                                            new Color(0.2F, 1.0F, 0.0F), 0.5F,
                                            40, 0.93F, false),
                                    player.position(), world, 2, 0.3F);
                            for (BlockPos pos : sphere) {
                                BlockState state = world.getBlockState(pos);
                                Block block = state.getBlock();
                                if (block instanceof IGrowable && !(block instanceof GrassBlock)) {
                                    IGrowable plant = (IGrowable) block;
                                    if (!world.isClientSide()) {
                                        for (int i = 0; i < config.growIterations; i++) {
                                            if (plant.isValidBonemealTarget(world, pos, state, world.isClientSide)
                                                    && plant.isBonemealSuccess(world, world.random, pos, state)) {
                                                plant.performBonemeal((ServerWorld) world,
                                                        world.getRandom(), pos, state);
                                            }
                                        }
                                    }
                                    if (world.isClientSide) {
                                        BoneMealItem.addGrowthParticles(world, pos, 0);
                                    }
                                }
                            }
                            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class,
                                    player.getBoundingBox().inflate(radius))) {
                                if (entity.isInvertedHealAndHarm()) {
                                    entity.hurt(DamageSource.playerAttack(player), config.healAmount);
                                } else {
                                    entity.heal(config.healAmount);
                                }
                            }
                        }
                    }
                }
            } else {
                if (time > 0) NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
            }
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.JUNGLE_TEMPLE);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class FragrantFlowerServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.FRAGRANT_FLOWER.get(), player).isPresent()) {
                    for (BeeEntity bee : player.getCommandSenderWorld().getEntitiesOfClass(BeeEntity.class, player.getBoundingBox()
                            .inflate(config.aggroRadius, config.aggroRadius, config.aggroRadius))) {
                        if (!(event.getEntityLiving() instanceof BeeEntity)) {
                            bee.setLastHurtByMob(event.getEntityLiving());
                            bee.setPersistentAngerTarget(event.getEntityLiving().getUUID());
                            bee.setTarget(event.getEntityLiving());
                            bee.setRemainingPersistentAngerTime(TickRangeConverter.rangeOfSeconds(20, 39)
                                    .randomValue(bee.getCommandSenderWorld().getRandom()));
                        }
                    }
                }
            }
            if (event.getSource().getEntity() instanceof BeeEntity) {
                BeeEntity bee = (BeeEntity) event.getSource().getEntity();
                for (PlayerEntity player : bee.getCommandSenderWorld().getEntitiesOfClass(PlayerEntity.class, bee.getBoundingBox()
                        .inflate(config.aggroRadius, config.aggroRadius, config.aggroRadius))) {
                    if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.FRAGRANT_FLOWER.get(), player).isPresent()) {
                        event.setAmount(event.getAmount() * config.damageMultiplier);
                    }
                }
            }
        }
    }

    public static class Stats extends RelicStats {
        public int luringRadius = 16;
        public int aggroRadius = 32;
        public float damageMultiplier = 3.0F;
        public int capacity = 10;
        public int consumptionRadius = 3;
        public int effectRadius = 5;
        public int growIterations = 2;
        public int healAmount = 10;
    }
}