package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.particles.CircleTintData;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;

public class SporeSackItem extends Item implements ICurioItem, IHasTooltip {
    public SporeSackItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.UNCOMMON));
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