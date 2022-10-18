package it.hurts.sskirillss.relics.items.relics.necklace;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.LifeEssenceEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class HolyLocketItem extends RelicItem {
    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("steal", RelicAbilityEntry.builder()
                                .stat("radius", RelicAbilityStat.builder()
                                        .initialValue(4D, 8D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 2D)
                                        .formatValue(value -> String.valueOf(MathUtils.round(value, 1)))
                                        .build())
                                .stat("amount", RelicAbilityStat.builder()
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 0.1D)
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 200))
                .styleData(RelicStyleData.builder()
                        .borders("#dc41ff", "#832698")
                        .build())
                .build();
    }

    @Mod.EventBusSubscriber
    static class Events {
        @SubscribeEvent
        public static void onLivingHurt(LivingHealEvent event) {
            LivingEntity entity = event.getEntityLiving();
            Level level = entity.getCommandSenderWorld();

            for (Player player : level.getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(32))) {
                ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.HOLY_LOCKET.get());

                if (stack.isEmpty() || getAbilityValue(stack, "steal", "radius") < player.position().distanceTo(entity.position())
                        || entity.getStringUUID().equals(player.getStringUUID()))
                    return;

                float amount = (float) (event.getAmount() * getAbilityValue(stack, "steal", "amount"));

                LifeEssenceEntity essence = new LifeEssenceEntity(player, amount);

                essence.setPos(entity.position().add(0, entity.getBbHeight() / 2, 0));
                essence.setOwner(player);

                level.addFreshEntity(essence);

                event.setAmount(event.getAmount() - amount);

                addExperience(stack, Math.round(amount * 2F));
            }
        }
    }
}