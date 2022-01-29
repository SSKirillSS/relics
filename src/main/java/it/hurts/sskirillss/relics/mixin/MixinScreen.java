package it.hurts.sskirillss.relics.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.tooltip.TooltipBorderHandler;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen extends AbstractContainerEventHandler {
    @Shadow(remap = false)
    private final ItemStack tooltipStack = ItemStack.EMPTY;

    @Final
    @Shadow
    private final List<GuiEventListener> children = Lists.newArrayList();

    @Inject(method = "renderTooltipInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;blitOffset:F", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void renderTooltipInternal(PoseStack matrix, List<ClientTooltipComponent> components, int preX, int preY, CallbackInfo info, RenderTooltipEvent.Pre pre, int width, int height, int postX, int postY) {
        if (TooltipBorderHandler.getBorderColors(tooltipStack) == null)
            return;

        ResourceLocation texture = new ResourceLocation(Reference.MODID,
                "textures/gui/tooltip/" + tooltipStack.getItem().getRegistryName().getPath() + ".png");

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, texture);

        Minecraft.getInstance().getTextureManager().getTexture(texture).bind();

        int texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        if (texHeight == 0 || texWidth == 0)
            return;

        matrix.pushPose();

        RenderSystem.enableBlend();

        matrix.translate(0, 0, 410.0);

        GuiComponent.blit(matrix, postX - 8 - 6, postY - 8 - 6, 1, 1 % texHeight, 16, 16, texWidth, texHeight);
        GuiComponent.blit(matrix, postX + width - 8 + 6, postY - 8 - 6, texWidth - 16 - 1, 1 % texHeight, 16, 16, texWidth, texHeight);

        GuiComponent.blit(matrix, postX - 8 - 6, postY + height - 8 + 6, 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);
        GuiComponent.blit(matrix, postX + width - 8 + 6, postY + height - 8 + 6, texWidth - 16 - 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);

        if (width >= 94) {
            GuiComponent.blit(matrix, postX + (width / 2) - 47, postY - 16, 16 + 2 * texWidth + 1, 1 % texHeight, 94, 16, texWidth, texHeight);
            GuiComponent.blit(matrix, postX + (width / 2) - 47, postY + height, 16 + 2 * texWidth + 1, 1 % texHeight + 16, 94, 16, texWidth, texHeight);
        }

        RenderSystem.disableBlend();

        matrix.popPose();
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }
}