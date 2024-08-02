package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.octolib.config.ConfigManager;
import it.hurts.sskirillss.octolib.config.annotations.registration.ConfigRegistration;
import it.hurts.sskirillss.relics.config.data.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

@ConfigRegistration(modId = Reference.MODID)
public class ConfigRegistry {
    static {
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (!(entry.getValue() instanceof IRelicItem relic))
                continue;

            ConfigManager.registerConfig(ResourceLocation.fromNamespaceAndPath(entry.getKey().location().getNamespace(), "relics/" + entry.getKey().location().getPath()), new RelicConfigData(relic.getRelicData()) {
                @Override
                public void onLoadObject(Object object) {
                    // TODO: relic.setRelicData(((RelicConfigData) object).toData(relic));
                }
            });
        }
    }
}