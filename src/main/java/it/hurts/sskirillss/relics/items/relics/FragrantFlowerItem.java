package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
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
import java.util.List;

public class FragrantFlowerItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_NECTAR_AMOUNT = "nectar";
    public static final String TAG_UPDATE_TIME = "time";

    public FragrantFlowerItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.fragrant_flower.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.fragrant_flower.shift_2"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("tooltip.relics.fragrant_flower.tooltip_1", NBTUtils.getInt(stack, TAG_NECTAR_AMOUNT, 0)));
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            int nectar = NBTUtils.getInt(stack, TAG_NECTAR_AMOUNT, 0);
            int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
            for (BeeEntity bee : player.getEntityWorld().getEntitiesWithinAABB(BeeEntity.class,
                    player.getBoundingBox().grow(RelicsConfig.FragrantFlower.BEE_LURING_RADIUS.get(),
                            RelicsConfig.FragrantFlower.BEE_LURING_RADIUS.get(), RelicsConfig.FragrantFlower.BEE_LURING_RADIUS.get()))) {
                if (bee.getAngerTarget() != null && bee.getAngerTarget().equals(player.getUniqueID())) {
                    bee.setRevengeTarget(null);
                    bee.setAngerTarget(null);
                    bee.setAttackTarget(null);
                    bee.setAngerTime(0);
                }
                if (bee.hasNectar() && nectar < RelicsConfig.FragrantFlower.NECTAR_CAPACITY.get()) {
                    bee.getNavigator().tryMoveToXYZ(player.getPosX(), player.getPosY(), player.getPosZ(), 1.0F);
                    if (player.getPositionVec().distanceTo(bee.getPositionVec()) < RelicsConfig.FragrantFlower.NECTAR_CONSUMPTION_RADIUS.get()) {
                        NBTUtils.setInt(stack, TAG_NECTAR_AMOUNT, nectar + 1);
                        bee.onHoneyDelivered();
                        player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                                SoundEvents.ENTITY_BEE_POLLINATE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    }
                }
            }
            if (player.isSneaking()) {
                if (!player.getCooldownTracker().hasCooldown(stack.getItem()) && nectar > 0) {
                    float radius = RelicsConfig.FragrantFlower.EFFECT_RADIUS.get();
                    double extraY = player.getPosY() + 0.5F;
                    for (int i = 0; i < 5; i++) {
                        float angle = (0.01F * (player.ticksExisted * 3 + i * 125));
                        double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle))) + player.getPosX();
                        double extraZ = (double) (radius * MathHelper.cos(angle)) + player.getPosZ();
                        player.getEntityWorld().addParticle(new CircleTintData(
                                        new Color(1.0F, 0.4F, 0.7F), 0.5F,
                                        30, 0.95F, false),
                                extraX, extraY, extraZ, 0F, 0F, 0F);
                    }
                    for (int i = 0; i < 2; i++) {
                        float angle = (-0.02F * (player.ticksExisted * 3 + i * 160));
                        double extraX = (double) (radius * 0.5F * MathHelper.sin((float) (Math.PI + angle))) + player.getPosX();
                        double extraZ = (double) (radius * 0.5F * MathHelper.cos(angle)) + player.getPosZ();
                        player.getEntityWorld().addParticle(new CircleTintData(
                                        new Color(0.2F, 1.0F, 0.0F), 0.5F,
                                        40, 0.95F, false),
                                extraX, extraY, extraZ, 0F, 0F, 0F);
                    }
                    if (player.ticksExisted % 20 == 0) {
                        List<BlockPos> sphere = WorldUtils.getBlockSphere(player.getPosition(), radius);
                        if (time < 5) {
                            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                        } else {
                            NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                            NBTUtils.setInt(stack, TAG_NECTAR_AMOUNT, nectar - 1);
                            player.getCooldownTracker().setCooldown(stack.getItem(), 10 * 20);
                            ParticleUtils.createBall(new CircleTintData(
                                            new Color(0.2F, 1.0F, 0.0F), 0.5F,
                                            40, 0.93F, false),
                                    player.getPositionVec(), player.getEntityWorld(), 2, 0.3F);
                            for (BlockPos pos : sphere) {
                                BlockState state = player.getEntityWorld().getBlockState(pos);
                                if (state.getBlock() instanceof IGrowable && !(state.getBlock() instanceof GrassBlock)) {
                                    IGrowable plant = (IGrowable) state.getBlock();
                                    if (!player.getEntityWorld().isRemote()) {
                                        for (int i = 0; i < RelicsConfig.FragrantFlower.GROW_EFFICIENCY.get(); i++) {
                                            state = player.getEntityWorld().getBlockState(pos);
                                            if (plant.canGrow(player.getEntityWorld(), pos, state, player.getEntityWorld().isRemote)
                                                    && plant.canUseBonemeal(player.getEntityWorld(), player.getEntityWorld().rand, pos, state)) {
                                                plant.grow((ServerWorld) player.getEntityWorld(),
                                                        player.getEntityWorld().getRandom(), pos, state);
                                            }
                                        }
                                    }
                                    if (player.getEntityWorld().isRemote()) {
                                        BoneMealItem.spawnBonemealParticles(player.getEntityWorld(), pos, 0);
                                    }
                                }
                            }
                            for (LivingEntity entity : player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class,
                                    player.getBoundingBox().grow(radius))) {
                                if (entity.isEntityUndead()) {
                                    entity.attackEntityFrom(DamageSource.causePlayerDamage(player),
                                            RelicsConfig.FragrantFlower.HEAL_AMOUNT.get().floatValue());
                                } else {
                                    entity.heal(RelicsConfig.FragrantFlower.HEAL_AMOUNT.get().floatValue());
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

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class FragrantFlowerServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingHurtEvent event) {
            if (event.getSource().getTrueSource() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.FRAGRANT_FLOWER.get(), player).isPresent()) {
                    for (BeeEntity bee : player.getEntityWorld().getEntitiesWithinAABB(BeeEntity.class, player.getBoundingBox()
                            .grow(RelicsConfig.FragrantFlower.BEE_AGGRO_RADIUS.get(), RelicsConfig.FragrantFlower.BEE_AGGRO_RADIUS.get(),
                                    RelicsConfig.FragrantFlower.BEE_AGGRO_RADIUS.get()))) {
                        if (!(event.getEntityLiving() instanceof BeeEntity)) {
                            bee.setRevengeTarget(event.getEntityLiving());
                            bee.setAngerTarget(event.getEntityLiving().getUniqueID());
                            bee.setAttackTarget(event.getEntityLiving());
                            bee.setAngerTime(TickRangeConverter.convertRange(20, 39)
                                    .getRandomWithinRange(bee.getEntityWorld().getRandom()));
                        }
                    }
                }
            }
            if (event.getSource().getTrueSource() instanceof BeeEntity) {
                BeeEntity bee = (BeeEntity) event.getSource().getTrueSource();
                for (PlayerEntity player : bee.getEntityWorld().getEntitiesWithinAABB(PlayerEntity.class, bee.getBoundingBox()
                        .grow(RelicsConfig.FragrantFlower.BEE_AGGRO_RADIUS.get(), RelicsConfig.FragrantFlower.BEE_AGGRO_RADIUS.get(),
                                RelicsConfig.FragrantFlower.BEE_AGGRO_RADIUS.get()))) {
                    if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.FRAGRANT_FLOWER.get(), player).isPresent()) {
                        event.setAmount(event.getAmount() * RelicsConfig.FragrantFlower.BEE_DAMAGE_MULTIPLIER.get().floatValue());
                    }
                }
            }
        }
    }
}