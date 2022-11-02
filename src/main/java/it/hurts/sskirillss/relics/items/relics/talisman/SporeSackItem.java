package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.SporeEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

public class SporeSackItem extends RelicItem {
    @Override
    public RelicData getRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("spore", RelicAbilityEntry.builder()
                                .requiredPoints(2)
                                .stat("amount", RelicAbilityStat.builder()
                                        .initialValue(1D, 1D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat("resize", RelicAbilityStat.builder()
                                        .initialValue(0.01D, 0.025D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, -0.001D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 2) * 100))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#ffe0d2", "#9c756b")
                        .build())
                .build();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getEntityLiving() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.SPORE_SACK.get());

            if (stack.isEmpty())
                return;

            Level level = player.getLevel();
            Random random = player.getRandom();

            for (int i = 0; i < random.nextInt((int) Math.round(getAbilityValue(stack, "spore", "amount"))) + 1; i++) {
                float speed = 0.25F + random.nextFloat() * 0.2F;

                Vec3 motion = new Vec3(MathUtils.randomFloat(random) * speed, speed, MathUtils.randomFloat(random) * speed);

                float mul = player.getBbHeight() / 1.5F;

                Vec3 pos = player.position().add(0, mul, 0).add(motion.normalize().multiply(mul, mul, mul));

                SporeEntity spore = new SporeEntity(level);

                spore.setPos(pos);
                spore.setStack(stack);
                spore.setOwner(player);
                spore.setDeltaMovement(motion);

                level.addFreshEntity(spore);
            }
        }
    }
}