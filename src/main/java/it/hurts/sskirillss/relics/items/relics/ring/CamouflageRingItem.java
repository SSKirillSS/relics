package it.hurts.sskirillss.relics.items.relics.ring;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.ConfigData;
import it.hurts.sskirillss.relics.configs.data.LootData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.block.BushBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
    public ConfigData<RelicStats> getConfigData() {
        return ConfigData.builder()
                .loot(LootData.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.1F)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.tickCount % 20 == 0 && canHide(livingEntity))
            livingEntity.addEffect(new EffectInstance(Effects.INVISIBILITY, 30, 0, false, false));
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