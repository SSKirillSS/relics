package it.hurts.sskirillss.relics.client.gui.layers;

import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.ring.CamouflageRingItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteAnchor;
import it.hurts.sskirillss.relics.utils.data.SpriteMirror;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

public class CamouflageRingHideLayer implements LayeredDraw.Layer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/hud/camouflage_ring_hide.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        var MC = Minecraft.getInstance();

        var player = MC.player;

        if (player == null)
            return;

        var stack = EntityUtils.findEquippedCurio(player, ItemRegistry.CAMOUFLAGE_RING.get());

        if (!(stack.getItem() instanceof CamouflageRingItem relic) || !relic.isHiding(player))
            return;

        var poseStack = guiGraphics.pose();

        poseStack.pushPose();

        var window = MC.getWindow();

        var width = window.getGuiScaledWidth();
        var height = window.getGuiScaledHeight();

        var alpha = 0.75F;

        RenderSystem.enableBlend();

        GUIRenderer.begin(TEXTURE, poseStack)
                .anchor(SpriteAnchor.TOP_LEFT)
                .pos(0, 0)
                .alpha(alpha)
                .scale(2)
                .end();

        GUIRenderer.begin(TEXTURE, poseStack)
                .mirror(SpriteMirror.HORIZONTAL)
                .anchor(SpriteAnchor.TOP_RIGHT)
                .pos(width, 0)
                .alpha(alpha)
                .scale(2)
                .end();

        GUIRenderer.begin(TEXTURE, poseStack)
                .anchor(SpriteAnchor.BOTTOM_LEFT)
                .mirror(SpriteMirror.VERTICAL)
                .pos(0, height)
                .alpha(alpha)
                .scale(2)
                .end();

        GUIRenderer.begin(TEXTURE, poseStack)
                .mirror(SpriteMirror.HORIZONTAL, SpriteMirror.VERTICAL)
                .anchor(SpriteAnchor.BOTTOM_RIGHT)
                .pos(width, height)
                .alpha(alpha)
                .scale(2)
                .end();

        RenderSystem.disableBlend();

        poseStack.popPose();
    }
}
