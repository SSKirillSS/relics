package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class LuckyHorseshoeItem extends RelicItem {
    public LuckyHorseshoeItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

//    @Override
//    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
//        return !DurabilityUtils.isBroken(curio) && livingEntity.getRandom().nextFloat() <= stats.lootingChance ? stats.additionalLooting : 0;
//    }
//
//    @Override
//    public int getFortuneBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
//        return !DurabilityUtils.isBroken(curio) && livingEntity.getRandom().nextFloat() <= stats.fortuneChance ? stats.additionalFortune : 0;
//    }
}