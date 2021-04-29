package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;

public class SoulDevourerItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_UPDATE_TIME = "time";
    public static final String TAG_SOUL_AMOUNT = "soul";
    public static final String TAG_EXPLOSION_READINESS = "readiness";

    public SoulDevourerItem() {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
        int readiness = NBTUtils.getInt(stack, TAG_EXPLOSION_READINESS, 0);
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            if (player.tickCount % 20 == 0) {
                if (soul > 0) {
                    if (time < RelicsConfig.SoulDevourer.TIME_PER_SOUL_DECREASE.get()) {
                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                    } else {
                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                        NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.round(Math.max(soul - (soul
                                * RelicsConfig.SoulDevourer.SOUL_DECREASE_MULTIPLIER_PER_SOUL.get().floatValue()
                                + RelicsConfig.SoulDevourer.MIN_SOUL_DECREASE_AMOUNT.get()), 0)));
                    }
                }
            }
            if (soul > RelicsConfig.SoulDevourer.MIN_SOUL_AMOUNT_FOR_EXPLOSION.get()) {
                if (player.isShiftKeyDown()) {
                    if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                        if (readiness < RelicsConfig.SoulDevourer.EXPLOSION_PREPARING_TIME.get() * 20) {
                            NBTUtils.setInt(stack, TAG_EXPLOSION_READINESS, readiness + 1);
                            float radius = (float) Math.sin(readiness * 0.1) + 1.0F + (readiness * 0.002F);
                            double extraY = player.getY() + 0.5F;
                            for (int i = 0; i < 5; i++) {
                                float angle = (0.0105F * (readiness * 4 + i * 120));
                                double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle))) + player.getX();
                                double extraZ = (double) (radius * MathHelper.cos(angle)) + player.getZ();
                                CircleTintData circleTintData = new CircleTintData(
                                        new Color(0.3F, 0.7F, 1.0F), 0.2F + (readiness * 0.004F),
                                        40, 0.95F, false);
                                player.getCommandSenderWorld().addParticle(circleTintData,
                                        extraX, extraY, extraZ,
                                        0F, 0F, 0F);
                            }
                        } else {
                            for (LivingEntity entity : player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class,
                                    player.getBoundingBox().inflate(RelicsConfig.SoulDevourer.EXPLOSION_RADIUS.get()))) {
                                Vector3d motion = entity.position().add(0.0F, 1.0F, 0.0F).subtract(player.position()).normalize().multiply(
                                        RelicsConfig.SoulDevourer.EXPLOSION_VELOCITY_MULTIPLIER.get(),
                                        RelicsConfig.SoulDevourer.EXPLOSION_VELOCITY_MULTIPLIER.get(),
                                        RelicsConfig.SoulDevourer.EXPLOSION_VELOCITY_MULTIPLIER.get());
                                if (entity instanceof PlayerEntity) {
                                    if (!entity.getUUID().equals(player.getUUID()) && entity instanceof ServerPlayerEntity)
                                        NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayerEntity) entity);
                                } else {
                                    entity.setDeltaMovement(motion);
                                }
                                entity.hurt(DamageSource.playerAttack(player),
                                        (float) (RelicsConfig.SoulDevourer.MIN_EXPLOSION_DAMAGE_AMOUNT.get()
                                                + (soul * RelicsConfig.SoulDevourer.EXPLOSION_DAMAGE_PER_SOUL_MULTIPLIER.get())));
                                player.getCooldowns().addCooldown(stack.getItem(), RelicsConfig.SoulDevourer.EXPLOSION_COOLDOWN.get() * 20);
                                ParticleUtils.createBall(new CircleTintData(new Color(0.3F, 0.7F, 1.0F),
                                                0.5F, 50, 0.95F, false), player.position(),
                                        player.getCommandSenderWorld(), 5, 0.2F);
                                NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, 0);
                                NBTUtils.setInt(stack, TAG_EXPLOSION_READINESS, 0);
                            }
                        }
                    }
                } else {
                    if (readiness != 0) NBTUtils.setInt(stack, TAG_EXPLOSION_READINESS, 0);
                }
            }
        }
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.soul_devourer.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.soul_devourer.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("tooltip.relics.soul_devourer.tooltip_1", NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0)));
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SoulEaterNecklaceServerEvents {
        @SubscribeEvent
        public static void onEntityDeath(LivingDeathEvent event) {
            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                LivingEntity entity = event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SOUL_DEVOURER.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SOUL_DEVOURER.get(), player).get().getRight();
                    int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
                    if (!player.getCooldowns().isOnCooldown(stack.getItem()) && soul < RelicsConfig.SoulDevourer.SOUL_CAPACITY.get()) {
                        NBTUtils.setInt(stack, TAG_SOUL_AMOUNT, Math.min(soul + Math.round(entity.getMaxHealth()
                                * RelicsConfig.SoulDevourer.SOUL_PER_HEALTH_MULTIPLIER.get().floatValue()), RelicsConfig.SoulDevourer.SOUL_CAPACITY.get()));
                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SOUL_DEVOURER.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SOUL_DEVOURER.get(), player).get().getRight();
                    int soul = NBTUtils.getInt(stack, TAG_SOUL_AMOUNT, 0);
                    if (soul > 0) {
                        event.setAmount((float) (event.getAmount() + (soul * RelicsConfig.SoulDevourer.ADDITIONAL_DAMAGE_PER_SOUL_MULTIPLIER.get())));
                    }
                }
            }
        }
    }
}