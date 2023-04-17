package it.hurts.sskirillss.relics.init;

import com.mojang.blaze3d.platform.InputConstants;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HotkeyRegistry {
    private static final String CATEGORY = "Relics";

    public static final KeyMapping ABILITY_LIST = new KeyMapping("key.relics.ability_list", GLFW_KEY_LEFT_ALT, CATEGORY);
    public static final KeyMapping ABILITY_CAST = new KeyMapping("key.relics.ability_cast", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.MOUSE, 0, CATEGORY);

    @SubscribeEvent
    public static void onKeybindingRegistry(RegisterKeyMappingsEvent event) {
        event.register(ABILITY_LIST);
        event.register(ABILITY_CAST);
    }
}