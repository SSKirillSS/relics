package it.hurts.sskirillss.relics.config.handlers;

import it.hurts.sskirillss.octolib.config.api.IOctoConfig;
import it.hurts.sskirillss.octolib.config.api.events.ConfigConstructEvent;
import it.hurts.sskirillss.relics.config.data.RelicConfigData;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {
    @SubscribeEvent
    public static void onConfigConstruct(ConfigConstructEvent event) {
        IOctoConfig constructor = event.getConstructor();

        if (!(constructor instanceof RelicConfigData config))
            return;

        config.getRelic().appendConfig(event.getSchema());
    }
}