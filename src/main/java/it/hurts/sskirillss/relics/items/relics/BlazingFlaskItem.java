package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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

public class BlazingFlaskItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_FIRE_AMOUNT = "fire";

    public BlazingFlaskItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.blazing_flask.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.blazing_flask.shift_2"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0) > 0) {
            tooltip.add(new TranslationTextComponent("tooltip.relics.blazing_flask.tooltip_1", NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0)));
        }
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            World world = player.getEntityWorld();
            int fire = NBTUtils.getInt(stack, TAG_FIRE_AMOUNT, 0);
            if (!(player.abilities.isCreativeMode)) {
                player.abilities.allowFlying = fire > 0;
                if (player.abilities.isFlying) {
                    for (int i = 0; i < 3; i++)
                        world.addParticle(ParticleTypes.LARGE_SMOKE, player.getPosX() + MathUtils.generateReallyRandomFloat(world.getRandom()) * 0.5F,
                                player.getPosY() + MathUtils.generateReallyRandomFloat(world.getRandom()) * 0.5F, player.getPosZ()
                                        + MathUtils.generateReallyRandomFloat(world.getRandom()) * 0.5F, 0, -0.1, 0);
                    world.addParticle(ParticleTypes.FLAME, player.getPosX()
                            + MathUtils.generateReallyRandomFloat(world.getRandom()) * 0.5F, player.getPosY()
                            + MathUtils.generateReallyRandomFloat(world.getRandom()) * 0.5F, player.getPosZ()
                            + MathUtils.generateReallyRandomFloat(world.getRandom()) * 0.5F, 0, -0.25F, 0);
                    for (LivingEntity entity : world.getEntitiesWithinAABB(LivingEntity.class, player.getBoundingBox()
                            .grow(0.5D).expand(0, -getGroundHeight(player) - 1, 0))) {
                        if (entity != player) entity.setFire(RelicsConfig.BlazingFlask.IGNITE_DURATION.get());
                    }
                    player.fallDistance = 0.0F;
                    double riseVelocity = 0.0D;
                    player.abilities.isFlying = fire > 0;
                    player.setMotion(player.getMotion().mul(RelicsConfig.BlazingFlask.LEVITATION_SPEED_MULTIPLIER.get(),
                            RelicsConfig.BlazingFlask.LEVITATION_SPEED_MULTIPLIER.get(), RelicsConfig.BlazingFlask.LEVITATION_SPEED_MULTIPLIER.get()));
                    if (player.moveForward > 0) player.setMotion(player.getMotion().getX() + new Vector3d(player.getLookVec().x,
                                    0, player.getLookVec().z).normalize().getX() * 0.025F, player.getMotion().getY(),
                            player.getMotion().getZ() + new Vector3d(player.getLookVec().x, 0, player.getLookVec().z).normalize().getZ() * 0.025F);
                    if (world.isRemote && player instanceof ClientPlayerEntity && ((ClientPlayerEntity) player).movementInput.jump) riseVelocity = 0.04D;
                    if (!player.isSneaking()) player.setMotion(player.getMotion().getX(), riseVelocity * ((getGroundHeight(player)
                            - (player.getPosY() - RelicsConfig.BlazingFlask.LEVITATION_HEIGHT.get()))), player.getMotion().getZ());
                    if (player.getPosY() - RelicsConfig.BlazingFlask.LEVITATION_HEIGHT.get() > getGroundHeight(player)) {
                        if (player.getMotion().getY() > 0) player.setMotion(player.getMotion().getX(), 0, player.getMotion().getZ());
                        player.setMotion(player.getMotion().getX(), -Math.min(player.getPosY() - RelicsConfig.BlazingFlask.LEVITATION_HEIGHT.get()
                                - getGroundHeight(player), 2) / 8, player.getMotion().getZ());
                    }
                    if (player.ticksExisted % 20 == 0) NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire - 1);
                }
            }
            if (!world.isRemote() && !player.isSpectator() && player.ticksExisted % (RelicsConfig.BlazingFlask.FIRE_CONSUMPTION_COOLDOWN.get() * 20)
                    == 0 && fire < RelicsConfig.BlazingFlask.FIRE_CAPACITY.get()) {
                List<BlockPos> sphere = WorldUtils.getBlockSphere(player.getPosition(), RelicsConfig.BlazingFlask.FIRE_CONSUMPTION_RADIUS.get());
                for (BlockPos pos : sphere) {
                    if (world.getBlockState(pos).getBlock() instanceof AbstractFireBlock) {
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        ((ServerWorld) world).spawnParticle(ParticleTypes.CLOUD, pos.getX() + 0.5F,
                                pos.getY() + 0.5F, pos.getZ() + 0.5F, 5, 0.35, 0.2, 0.35, 0.01);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, fire + 1);
                        return;
                    }
                }
            }
            if (fire > 0) {
                if (player.ticksExisted % 20 == 0 && (world.isRainingAt(player.getPosition()) || player.isInWater())) {
                    world.playSound(player, player.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 1.0F);
                    NBTUtils.setInt(stack, TAG_FIRE_AMOUNT, Math.max(0, fire - 1));
                }
            }
        }
    }

    private double getGroundHeight(PlayerEntity player) {
        RayTraceResult result = player.world.rayTraceBlocks(new RayTraceContext(player.getPositionVec(), new Vector3d(player.getPosX(),
                player.getPosY() - 64, player.getPosZ()), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, player));
        if (result.getType() == RayTraceResult.Type.BLOCK) return result.getHitVec().getY();
        return -player.getEntityWorld().getHeight();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (NBTUtils.getInt(newStack, TAG_FIRE_AMOUNT, 0) <= 0 && slotContext.getWearer() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) slotContext.getWearer();
            player.abilities.allowFlying = false;
            player.abilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class BlazingFlaskServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity && (event.getSource() == DamageSource.IN_FIRE || event.getSource() == DamageSource.ON_FIRE)) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BLAZING_FLASK.get(), player).isPresent()) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onEntityAttack(LivingAttackEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity && (event.getSource() == DamageSource.IN_FIRE || event.getSource() == DamageSource.ON_FIRE)) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BLAZING_FLASK.get(), player).isPresent()) {
                    event.setCanceled(true);
                }
            }
        }
    }
}