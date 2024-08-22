package it.hurts.sskirillss.relics.badges.base;

import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract non-sealed class RelicBadge extends AbstractBadge {
    public RelicBadge(String id) {
        super(id);
    }

    public MutableComponent getTitle(ItemStack stack) {
        return Component.translatable("tooltip.relics.researching.badge.relic." + getId() + ".title");
    }

    public List<MutableComponent> getDescription(ItemStack stack) {
        return Arrays.asList(Component.translatable("tooltip.relics.researching.badge.relic." + getId() + ".description"));
    }

    public List<MutableComponent> getHint(ItemStack stack) {
        return new ArrayList<>();
    }

    public boolean isVisible(ItemStack stack) {
        return false;
    }

    @Override
    public final ResourceLocation getIconTexture() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/general/badges/relic/" + getId() + ".png");
    }

    @Override
    public final ResourceLocation getOutlineTexture() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/gui/description/general/badges/relic/" + getId() + "_outline.png");
    }
}