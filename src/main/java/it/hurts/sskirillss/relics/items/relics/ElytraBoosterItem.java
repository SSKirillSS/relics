package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;

public class ElytraBoosterItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_BREATH_AMOUNT = "breath";

    public ElytraBoosterItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.elytra_booster.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.elytra_booster.shift_2"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0) > 0) {
            tooltip.add(new TranslationTextComponent("tooltip.relics.elytra_booster.tooltip_1", NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0)));
        }
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity && livingEntity.isSneaking()) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            int breath = NBTUtils.getInt(stack, TAG_BREATH_AMOUNT, 0);
            if (player.isElytraFlying()) {
                if (breath > 0) {
                    Vector3d look = player.getLookVec();
                    Vector3d motion = player.getMotion();
                    player.setMotion(motion.add(look.x * 0.1D + (look.x * RelicsConfig.ElytraBooster.MOVEMENT_SPEED_MULTIPLIER.get() - motion.x) * 0.5D,
                            look.y * 0.1D + (look.y * RelicsConfig.ElytraBooster.MOVEMENT_SPEED_MULTIPLIER.get() - motion.y) * 0.5D,
                            look.z * 0.1D + (look.z * RelicsConfig.ElytraBooster.MOVEMENT_SPEED_MULTIPLIER.get() - motion.z) * 0.5D));
                    player.getEntityWorld().addParticle(ParticleTypes.DRAGON_BREATH,
                            player.getPosX() + (MathUtils.generateReallyRandomFloat(player.getEntityWorld().getRandom()) * 0.5F),
                            player.getPosY() + (MathUtils.generateReallyRandomFloat(player.getEntityWorld().getRandom()) * 0.5F),
                            player.getPosZ() + (MathUtils.generateReallyRandomFloat(player.getEntityWorld().getRandom()) * 0.5F),
                            0, 0, 0);
                    if (player.ticksExisted % 20 == 0) NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath - 1);
                    for (LivingEntity entity : player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, player.getBoundingBox().grow(2.0F))) {
                        if (!entity.getUniqueID().equals(player.getUniqueID())) {
                            entity.setMotion(entity.getPositionVec().subtract(player.getPositionVec()).normalize().mul(RelicsConfig.ElytraBooster.RAM_KNOCKBACK_POWER.get(),
                                    RelicsConfig.ElytraBooster.RAM_KNOCKBACK_POWER.get(), RelicsConfig.ElytraBooster.RAM_KNOCKBACK_POWER.get()));
                            entity.attackEntityFrom(DamageSource.causePlayerDamage(player), RelicsConfig.ElytraBooster.RAM_DAMAGE_AMOUNT.get().floatValue());
                        }
                    }
                }
            } else {
                for (AreaEffectCloudEntity cloud : player.getEntityWorld().getEntitiesWithinAABB(AreaEffectCloudEntity.class,
                        player.getBoundingBox().grow(RelicsConfig.ElytraBooster.BREATH_CONSUMPTION_RADIUS.get()))) {
                    if (cloud.getParticleData() == ParticleTypes.DRAGON_BREATH && breath < RelicsConfig.ElytraBooster.BREATH_CAPACITY.get()) {
                        if (player.ticksExisted % 10 == 0) NBTUtils.setInt(stack, TAG_BREATH_AMOUNT, breath + 1);
                        if (cloud.getRadius() <= 0) cloud.remove();
                        cloud.setRadius(cloud.getRadius() - RelicsConfig.ElytraBooster.BREATH_CONSUMPTION_AMOUNT.get().floatValue());
                        Vector3d direction = player.getPositionVec().add(0, 1, 0).subtract(cloud.getPositionVec()).normalize();
                        player.getEntityWorld().addParticle(new CircleTintData(new Color(0.35F, 0.0F, 1.0F),
                                        (float) player.getPositionVec().add(0, 1, 0).distanceTo(cloud.getPositionVec()) * 0.075F,
                                        (int) player.getPositionVec().add(0, 1, 0).distanceTo(cloud.getPositionVec()) * 5,
                                        0.95F, false), cloud.getPosX(), cloud.getPosY(), cloud.getPosZ(),
                                direction.x * 0.2F, direction.y * 0.2F, direction.z * 0.2F);
                    }
                }
            }
        }
    }
}