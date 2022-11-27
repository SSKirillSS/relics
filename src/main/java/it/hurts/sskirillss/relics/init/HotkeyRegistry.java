package it.hurts.sskirillss.relics.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import static org.lwjgl.glfw.GLFW.*;

public class HotkeyRegistry {
    private static final String CATEGORY = "Relics";
    public static final KeyMapping
            HUD_UP = new KeyMapping("key.relics.hud_up", GLFW_KEY_RIGHT_BRACKET, CATEGORY),
            HUD_DOWN = new KeyMapping("key.relics.hud_down", GLFW_KEY_APOSTROPHE, CATEGORY),
            HUD_FIRST = new KeyMapping("key.relics.hud_first", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW_KEY_1, CATEGORY),
            HUD_SECOND = new KeyMapping("key.relics.hud_second", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW_KEY_2, CATEGORY),
            HUD_THIRD = new KeyMapping("key.relics.hud_third", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW_KEY_3, CATEGORY),
            HUD_FOURTH = new KeyMapping("key.relics.hud_fourth", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW_KEY_4, CATEGORY),
            HUD_FIFTH = new KeyMapping("key.relics.hud_fifth", KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW_KEY_5, CATEGORY);

    public static void register() {
        register(HUD_UP);
        register(HUD_DOWN);
        register(HUD_FIRST);
        register(HUD_SECOND);
        register(HUD_THIRD);
        register(HUD_FOURTH);
        register(HUD_FIFTH);
    }

    private static void register(KeyMapping binding) {

    }
}