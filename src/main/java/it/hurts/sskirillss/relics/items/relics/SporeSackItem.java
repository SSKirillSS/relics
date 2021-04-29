package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;

public class SporeSackItem extends RelicItem implements ICurioItem, IHasTooltip {
    public SporeSackItem() {
        super(Rarity.UNCOMMON);
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.spore_sack.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SporeSackEvents {
        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (event.getEntity() instanceof ProjectileEntity) {
                ProjectileEntity projectile = (ProjectileEntity) event.getEntity();
                if (projectile.getOwner() != null && projectile.getOwner() instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) projectile.getOwner();
                    if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SPORE_SACK.get(), player).isPresent()) {
                        if (!player.getCooldowns().isOnCooldown(ItemRegistry.SPORE_SACK.get())
                                && player.getCommandSenderWorld().getRandom().nextFloat() <= RelicsConfig.SporeSack.SPORE_CHANCE.get()) {
                            ParticleUtils.createBall(new CircleTintData(new Color(0.25F, 1.0F, 0.0F), 0.5F, 40, 0.94F, true),
                                    projectile.position(), player.getCommandSenderWorld(), 3, 0.1F);
                            player.getCommandSenderWorld().playSound(null, projectile.blockPosition(), SoundEvents.FIRE_EXTINGUISH,
                                    SoundCategory.PLAYERS, 1.0F, 0.5F);
                            player.getCooldowns().addCooldown(ItemRegistry.SPORE_SACK.get(), RelicsConfig.SporeSack.SPORE_COOLDOWN.get() * 20);
                            for (LivingEntity entity : player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox()
                                    .inflate(RelicsConfig.SporeSack.SPORE_RADIUS.get()))) {
                                if (entity != player) {
                                    entity.addEffect(new EffectInstance(Effects.POISON, RelicsConfig.SporeSack.POISON_DURATION.get() * 20,
                                            RelicsConfig.SporeSack.POISON_AMPLIFIER.get()));
                                    entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, RelicsConfig.SporeSack.SLOWNESS_DURATION.get() * 20,
                                            RelicsConfig.SporeSack.SLOWNESS_AMPLIFIER.get()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}