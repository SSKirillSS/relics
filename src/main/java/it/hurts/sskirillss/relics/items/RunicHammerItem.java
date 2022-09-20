package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class RunicHammerItem extends SwordItem {
    public RunicHammerItem() {
        super(Tiers.GOLD, 11, -3F, new Properties()
                .durability(130)
                .rarity(Rarity.UNCOMMON)
                .tab(RelicsTab.RELICS_TAB));
    }

    @Mod.EventBusSubscriber
    public static class ServerEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)
                    || player.getMainHandItem().getItem() != ItemRegistry.RUNIC_HAMMER.get())
                return;

            LivingEntity victim = event.getEntityLiving();

            victim.setDeltaMovement(player.getLookAngle().normalize().multiply(5, 5, 5));
        }
    }
}