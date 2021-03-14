package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
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

public class SporeSackItem extends Item implements ICurioItem, IHasTooltip {
    public SporeSackItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.spore_sack.shift_1"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SporeSackEvents {
        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (event.getEntity() instanceof ProjectileEntity) {
                ProjectileEntity projectile = (ProjectileEntity) event.getEntity();
                if (projectile.func_234616_v_() != null && projectile.func_234616_v_() instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) projectile.func_234616_v_();
                    if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SPORE_SACK.get(), player).isPresent()) {
                        if (!player.getCooldownTracker().hasCooldown(ItemRegistry.SPORE_SACK.get())
                                && player.getEntityWorld().getRandom().nextFloat() <= RelicsConfig.SporeSack.SPORE_CHANCE.get()) {
                            ParticleUtils.createBall(new CircleTintData(new Color(0.25F, 1.0F, 0.0F), 0.5F, 40, 0.94F, true),
                                    projectile.getPositionVec(), player.getEntityWorld(), 3, 0.1F);
                            player.getEntityWorld().playSound(null, projectile.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH,
                                    SoundCategory.PLAYERS, 1.0F, 0.5F);
                            player.getCooldownTracker().setCooldown(ItemRegistry.SPORE_SACK.get(), RelicsConfig.SporeSack.SPORE_COOLDOWN.get() * 20);
                            for (LivingEntity entity : player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, projectile.getBoundingBox()
                                    .grow(RelicsConfig.SporeSack.SPORE_RADIUS.get()))) {
                                if (entity != player) {
                                    entity.addPotionEffect(new EffectInstance(Effects.POISON, RelicsConfig.SporeSack.POISON_DURATION.get() * 20,
                                            RelicsConfig.SporeSack.POISON_AMPLIFIER.get()));
                                    entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, RelicsConfig.SporeSack.SLOWNESS_DURATION.get() * 20,
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