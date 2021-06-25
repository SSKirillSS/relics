package it.hurts.sskirillss.relics.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import static org.lwjgl.glfw.GLFW.*;

public class HotkeyRegistry {
    private static final String CATEGORY = "Relics";
    public static final KeyBinding
            HUD_UP = new KeyBinding("key.relics.hud_up", GLFW_KEY_RIGHT_BRACKET, CATEGORY),
            HUD_DOWN = new KeyBinding("key.relics.hud_down", GLFW_KEY_APOSTROPHE, CATEGORY),
            HUD_FIRST = new KeyBinding("key.relics.hud_first", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW_KEY_1, CATEGORY),
            HUD_SECOND = new KeyBinding("key.relics.hud_second", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW_KEY_2, CATEGORY),
            HUD_THIRD = new KeyBinding("key.relics.hud_third", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW_KEY_3, CATEGORY),
            HUD_FOURTH = new KeyBinding("key.relics.hud_fourth", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW_KEY_4, CATEGORY),
            HUD_FIFTH = new KeyBinding("key.relics.hud_fifth", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW_KEY_5, CATEGORY);

    public static void register() {
        register(HUD_UP);
        register(HUD_DOWN);
        register(HUD_FIRST);
        register(HUD_SECOND);
        register(HUD_THIRD);
        register(HUD_FOURTH);
        register(HUD_FIFTH);
    }

    private static void register(KeyBinding binding) {
        ClientRegistry.registerKeyBinding(binding);
    }
}