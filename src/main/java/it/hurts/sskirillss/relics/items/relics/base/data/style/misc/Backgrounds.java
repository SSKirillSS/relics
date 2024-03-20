package it.hurts.sskirillss.relics.items.relics.base.data.style.misc;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class Backgrounds {
    public static final ResourceLocation DEFAULT = background("default");

    public static final ResourceLocation AQUATIC = background("aquatic");

    public static ResourceLocation background(String name) {
        return background(Reference.MODID, name);
    }

    public static ResourceLocation background(String modid, String name) {
        return new ResourceLocation(modid, "textures/gui/description/backgrounds/" + name.toLowerCase(Locale.ROOT) + ".png");
    }
}