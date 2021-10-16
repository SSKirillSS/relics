package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.DrownedBeltModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class DrownedBeltItem extends RelicItem<DrownedBeltItem.Stats> implements ICurioItem {
    public static DrownedBeltItem INSTANCE;

    public DrownedBeltItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.AQUATIC)
                        .chance(0.1F)
                        .build())
                .loot(RelicLoot.builder()
                        .table(EntityType.DROWNED.getDefaultLootTable().toString())
                        .chance(0.01F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.dealtDamageMultiplier * 100 - 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.incomingDamageMultiplier * 100 - 100) + "%")
                        .negative()
                        .build())
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new DrownedBeltModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class DrownedBeltServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            PlayerEntity player = null;
            float value = 1.0F;

            if (event.getEntityLiving() instanceof PlayerEntity) {
                player = (PlayerEntity) event.getEntityLiving();
                value = config.incomingDamageMultiplier;
            } else if (event.getSource().getEntity() instanceof PlayerEntity) {
                player = (PlayerEntity) event.getSource().getEntity();
                value = config.dealtDamageMultiplier;
            }

            if (player != null && player.isInWater()) {
                float multiplier = value;

                CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.DROWNED_BELT.get(), player).ifPresent(triple -> {
                    if (!isBroken(triple.getRight()))
                        event.setAmount(event.getAmount() * multiplier);
                });
            }
        }
    }

    public static class Stats extends RelicStats {
        public float incomingDamageMultiplier = 1.5F;
        public float dealtDamageMultiplier = 3.0F;
    }
}