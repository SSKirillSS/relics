package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class ElytraBoosterItem extends RelicItem<ElytraBoosterItem.Stats> implements ICurioItem {
    public static final String TAG_BREATH_AMOUNT = "breath";

    public ElytraBoosterItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.elytra_booster.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.elytra_booster.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0) > 0) {
            tooltip.add(new TranslationTextComponent("tooltip.relics.elytra_booster.tooltip_1", NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0)));
        }
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            int breath = NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0);
            if (player.isFallFlying()) {
                if (player.isShiftKeyDown() && breath > 0) {
                    Vector3d look = player.getLookAngle();
                    Vector3d motion = player.getDeltaMovement();
                    player.setDeltaMovement(motion.add(look.x * 0.1D + (look.x * config.flySpeed - motion.x) * 0.5D,
                            look.y * 0.1D + (look.y * config.flySpeed - motion.y) * 0.5D,
                            look.z * 0.1D + (look.z * config.flySpeed - motion.z) * 0.5D));
                    player.getCommandSenderWorld().addParticle(ParticleTypes.DRAGON_BREATH,
                            player.getX() + (MathUtils.randomFloat(player.getCommandSenderWorld().getRandom()) * 0.5F),
                            player.getY() + (MathUtils.randomFloat(player.getCommandSenderWorld().getRandom()) * 0.5F),
                            player.getZ() + (MathUtils.randomFloat(player.getCommandSenderWorld().getRandom()) * 0.5F),
                            0, 0, 0);
                    if (player.tickCount % 20 == 0) NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath - 1);
                    for (LivingEntity entity : player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(2.0F))) {
                        if (!entity.getUUID().equals(player.getUUID())) {
                            entity.setDeltaMovement(entity.position().subtract(player.position()).normalize().multiply(config.ramKnockback,
                                    config.ramKnockback, config.ramKnockback));
                            entity.hurt(DamageSource.playerAttack(player), config.ramDamage);
                        }
                    }
                }
            } else {
                if (breath < config.breathCapacity) {
                    if (player.getCommandSenderWorld().dimension() == World.END && player.tickCount %
                            (config.breathRegenerationCooldown * 20) == 0)
                        NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath + 1);
                    if (player.isShiftKeyDown()) {
                        for (AreaEffectCloudEntity cloud : player.getCommandSenderWorld().getEntitiesOfClass(AreaEffectCloudEntity.class,
                                player.getBoundingBox().inflate(config.breathConsumptionRadius))) {
                            if (cloud.getParticle() == ParticleTypes.DRAGON_BREATH) {
                                if (player.tickCount % 5 == 0) NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath + 1);
                                if (cloud.getRadius() <= 0) cloud.remove();
                                cloud.setRadius(cloud.getRadius() - config.breathConsumptionSpeed);
                                Vector3d direction = player.position().add(0, 1, 0).subtract(cloud.position()).normalize();
                                player.getCommandSenderWorld().addParticle(new CircleTintData(new Color(0.35F, 0.0F, 1.0F),
                                                (float) player.position().add(0, 1, 0).distanceTo(cloud.position()) * 0.075F,
                                                (int) player.position().add(0, 1, 0).distanceTo(cloud.position()) * 5,
                                                0.95F, false), cloud.getX(), cloud.getY(), cloud.getZ(),
                                        direction.x * 0.2F, direction.y * 0.2F, direction.z * 0.2F);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.END_CITY_TREASURE);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    public static class Stats extends RelicStats {
        public float flySpeed = 1.5F;
        public float ramDamage = 5.0F;
        public float ramKnockback = 3.0F;
        public int breathConsumptionRadius = 10;
        public int breathCapacity = 1000;
        public float breathConsumptionSpeed = 0.02F;
        public int breathRegenerationCooldown = 2;
    }
}