package it.hurts.sskirillss.relics.client.tooltip;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.api.events.common.TooltipDisplayEvent;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipBorderHandler {
    @SubscribeEvent
    public static void onTooltipDisplay(TooltipDisplayEvent event) { // FIXME 1.19.2 :: Removed in 1.20.1?
        ItemStack stack = event.getStack();
        PoseStack poseStack = event.getPoseStack();

        int width = event.getWidth();
        int height = event.getHeight();

        int x = event.getX();
        int y = event.getY();

        if (TooltipBorderHandler.getBorderColors(stack) == null)
            return;

        ResourceLocation texture = new ResourceLocation(Reference.MODID,
                "textures/gui/tooltip/" + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".png");

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, texture);

        Minecraft.getInstance().getTextureManager().getTexture(texture).bind();

        int texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        if (texHeight == 0 || texWidth == 0)
            return;

        poseStack.pushPose();

        RenderSystem.enableBlend();

        poseStack.translate(0, 0, 410.0);

        GuiComponent.blit(poseStack, x - 8 - 6, y - 8 - 6, 1, 1 % texHeight, 16, 16, texWidth, texHeight);
        GuiComponent.blit(poseStack, x + width - 8 + 6, y - 8 - 6, texWidth - 16 - 1, 1 % texHeight, 16, 16, texWidth, texHeight);

        GuiComponent.blit(poseStack, x - 8 - 6, y + height - 8 + 6, 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);
        GuiComponent.blit(poseStack, x + width - 8 + 6, y + height - 8 + 6, texWidth - 16 - 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);

        if (width >= 94) {
            GuiComponent.blit(poseStack, x + (width / 2) - 47, y - 16, 16 + 2 * texWidth + 1, 1 % texHeight, 94, 16, texWidth, texHeight);
            GuiComponent.blit(poseStack, x + (width / 2) - 47, y + height, 16 + 2 * texWidth + 1, 1 % texHeight + 16, 94, 16, texWidth, texHeight);
        }

        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onTooltipColorEvent(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof IRelicItem))
            return;

        Pair<String, String> colors = getBorderColors(stack);

        Color color = new Color(stack.getRarity().color.getColor()).darker();
        int top = colors == null ? color.getRGB() : Color.decode(colors.getLeft()).getRGB();
        int bottom = colors == null ? color.darker().darker().getRGB() : Color.decode(colors.getRight()).getRGB();

        event.setBorderStart(top);
        event.setBorderEnd(bottom);
    }

    @Nullable
    public static Pair<String, String> getBorderColors(ItemStack stack) {
//        if (!(stack.getItem() instanceof IRelicItem relic))
//            return null;
//
//        RelicDataNew data = relic.getNewData();
//
//        if (data == null)
//            return null;
//
//        RelicStyleData tooltip = relic.getNewData().getStyleData();
//
//        if (tooltip == null)
//            return null;
//
//        return tooltip.getBorders();

        return null;
    }
}