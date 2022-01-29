package it.hurts.sskirillss.relics.items.relics.belt;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

public class HunterBeltItem extends RelicItem<HunterBeltItem.Stats> {
    public static HunterBeltItem INSTANCE;

    public HunterBeltItem() {
        super(RelicData.builder()
                .rarity(Rarity.UNCOMMON)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#32a167", "#16702e")
                .ability(AbilityTooltip.builder()
                        .arg(stats.additionalLooting)
                        .build())
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.petDamageMultiplier * 100 - 100) + "%")
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return RelicSlotModifier.builder()
                .entry(Pair.of("talisman", 1))
                .build();
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return stats.additionalLooting;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class HunterBeltEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingHurtEvent event) {
            Stats stats = INSTANCE.stats;
            Entity entity = event.getSource().getEntity();

            if (!(entity instanceof TamableAnimal pet))
                return;

            if (!(pet.getOwner() instanceof Player)
                    && EntityUtils.findEquippedCurio(pet.getOwner(), ItemRegistry.HUNTER_BELT.get()).isEmpty())
                return;

            event.setAmount(event.getAmount() * stats.petDamageMultiplier);
        }
    }

    public static class Stats extends RelicStats {
        public int additionalLooting = 1;
        public float petDamageMultiplier = 3.0F;
    }
}