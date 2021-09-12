package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Collections;
import java.util.List;

public class HunterBeltItem extends RelicItem<HunterBeltItem.Stats> implements ICurioItem {
    public static HunterBeltItem INSTANCE;

    public HunterBeltItem() {
        super(Rarity.UNCOMMON);

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg(config.additionalLooting)
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.petDamageMultiplier * 100 - 100) + "%")
                        .build())
                .build();
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return config.additionalLooting;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.VILLAGE_BUTCHER);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class HunterBeltEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            Entity entity = event.getSource().getEntity();

            if (!(entity instanceof TameableEntity))
                return;

            TameableEntity pet = (TameableEntity) entity;

            if (pet.getOwner() instanceof PlayerEntity && CuriosApi.getCuriosHelper()
                    .findEquippedCurio(ItemRegistry.HUNTER_BELT.get(), pet.getOwner()).isPresent())
                event.setAmount(event.getAmount() * config.petDamageMultiplier);
        }
    }

    public static class Stats extends RelicStats {
        public int additionalLooting = 1;
        public float petDamageMultiplier = 3.0F;
    }
}