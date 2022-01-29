package it.hurts.sskirillss.relics.items.relics.ring;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.BushBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CamouflageRingItem extends RelicItem<RelicStats> {
    public CamouflageRingItem() {
        super(RelicData.builder()
                .rarity(Rarity.UNCOMMON)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#009122", "#002816")
                .ability(AbilityTooltip.builder()
                        .active(Minecraft.getInstance().options.keyShift)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<RelicStats> getConfigData() {
        return RelicConfigData.builder()
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.tickCount % 20 == 0 && canHide(livingEntity))
            livingEntity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30, 0, false, false));
    }

    private static boolean canHide(LivingEntity entity) {
        return !EntityUtils.findEquippedCurio(entity, ItemRegistry.CAMOUFLAGE_RING.get()).isEmpty() && entity.isShiftKeyDown()
                && entity.getCommandSenderWorld().getBlockState(entity.blockPosition()).getBlock() instanceof BushBlock;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class CamouflageRingClientEvents {
        @SubscribeEvent
        public static void onEntityRender(RenderPlayerEvent.Pre event) {
            if (canHide(event.getPlayer()))
                event.setCanceled(true);
        }
    }
}