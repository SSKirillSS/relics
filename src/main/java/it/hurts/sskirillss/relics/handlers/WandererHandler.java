package it.hurts.sskirillss.relics.handlers;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber
public class WandererHandler { // FIXME 1.19.2 :: Removed in 1.20.1?
    @SubscribeEvent
    public static void onWandererTradesCompile(WandererTradesEvent event) {
        for (RegistryObject<Item> entry : ItemRegistry.ITEMS.getEntries()) {
            if (!entry.isPresent())
                continue;

            Item item = entry.get();

            if (!(item instanceof RelicItem) || !item.getCreativeTabs().contains(RelicsTab.RELICS_TAB))
                continue;

            ItemStack stack = item.getDefaultInstance();

            event.getRareTrades().add(new BasicItemListing(64, stack, 1, 8));
        }
    }
}