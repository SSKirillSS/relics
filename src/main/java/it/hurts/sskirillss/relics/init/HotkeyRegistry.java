package it.hurts.sskirillss.relics.init;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

public class HotkeyRegistry {
    private static final String CATEGORY = "Relics";

    public static final KeyMapping ABILITY_LIST = new KeyMapping("key.relics.ability_list", GLFW_KEY_LEFT_ALT, CATEGORY);

    public static void register() {
        ClientRegistry.registerKeyBinding(ABILITY_LIST);
    }
}