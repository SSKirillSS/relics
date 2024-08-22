package it.hurts.sskirillss.relics.badges.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@AllArgsConstructor
public abstract sealed class AbstractBadge permits AbilityBadge, RelicBadge {
    @Getter
    private final String id;

    public abstract ResourceLocation getIconTexture();

    public abstract ResourceLocation getOutlineTexture();
}