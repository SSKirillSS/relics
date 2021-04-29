package it.hurts.sskirillss.relics.utils;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.versions.forge.ForgeVersion;

public class CompatibilityUtils {
    public static boolean isValidForgeVersion() {
        String[] s1 = ForgeVersion.getVersion().split("\\.");
        String s2 = s1[0] + s1[1] + s1[2];
        while (s2.length() < 5) s2 = String.valueOf(Integer.parseInt(s2) * 10);
        return Integer.parseInt(s2) >= 36014;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CompatibilityEvent {
        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (!isValidForgeVersion() && RelicsConfig.RelicsCompatibility.WARN_ABOUT_OLD_FORGE.get()) {
                event.getPlayer().displayClientMessage(new TranslationTextComponent("message.relics.version.forge"), false);
            }
        }
    }
}