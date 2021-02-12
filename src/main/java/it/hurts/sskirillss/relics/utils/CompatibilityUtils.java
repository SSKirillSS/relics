package it.hurts.sskirillss.relics.utils;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.versions.forge.ForgeVersion;

public class CompatibilityUtils {
    public static boolean isValidForgeVersion() {
        String[] version = ForgeVersion.getVersion().split("\\.");
        return Integer.parseInt(version[0] + version[1] + version[2]) >= 36014;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CompatibilityEvent {
        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (!isValidForgeVersion() && RelicsConfig.RelicsCompatibility.WARN_ABOUT_OLD_FORGE.get()) {
                event.getPlayer().sendStatusMessage(new TranslationTextComponent("message.relics.version.forge"), false);
            }
        }
    }
}