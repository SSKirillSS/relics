package it.hurts.sskirillss.relics.items.relics.base.data.misc;

import it.hurts.sskirillss.relics.utils.Reference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@Getter
@AllArgsConstructor
public class StatIcon {
    private final String id;
    private final int color;

    public ResourceLocation getPath() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/stats/" + getId() + ".png");
    }
}