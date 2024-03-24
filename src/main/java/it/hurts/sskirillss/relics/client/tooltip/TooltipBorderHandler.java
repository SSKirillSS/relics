package it.hurts.sskirillss.relics.client.tooltip;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.api.events.common.TooltipDisplayEvent;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipBorderHandler {
    @SubscribeEvent
    public static void onTooltipDisplay(TooltipDisplayEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return;

        ItemStack stack = event.getStack();

        if (!(stack.getItem() instanceof IRelicItem relic) || relic.getRelicData().getStyle().getBorders() == null)
            return;

        PoseStack poseStack = event.getPose();

        int width = event.getWidth();
        int height = event.getHeight();

        int x = event.getX();
        int y = event.getY();

        String id = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();

        ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/gui/tooltip/frame/" + id + "_frame.png");

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, texture);

        Minecraft.getInstance().getTextureManager().getTexture(texture).bind();

        int texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        int patternWidth = 160;
        int patternHeight = 64;

        int cornerWidth = 32;
        int cornerHeight = 32;

        int middleWidth = 96;
        int middleHeight = cornerHeight;

        poseStack.pushPose();

        RenderSystem.enableBlend();

        poseStack.translate(0, 0, 410.0);

        int frame = AnimationData.construct(texHeight, patternHeight, 2).getFrameByTime(player.tickCount).getKey();

        int offset = patternHeight * frame;

        GuiComponent.blit(poseStack, x - cornerWidth / 2 - 3, y - cornerHeight / 2 - 3, 0, offset, cornerWidth, cornerHeight, texWidth, texHeight);
        GuiComponent.blit(poseStack, x + width - cornerWidth / 2 + 3, y - cornerHeight / 2 - 3, patternWidth - cornerWidth, offset, cornerWidth, cornerHeight, texWidth, texHeight);

        GuiComponent.blit(poseStack, x - cornerWidth / 2 - 3, y + height - cornerHeight / 2 + 3, 0, (patternHeight - cornerHeight) + offset, cornerWidth, cornerHeight, texWidth, texHeight);
        GuiComponent.blit(poseStack, x + width - cornerWidth / 2 + 3, y + height - cornerHeight / 2 + 3, patternWidth - cornerWidth, (patternHeight - cornerHeight) + offset, cornerWidth, cornerHeight, texWidth, texHeight);

        GuiComponent.blit(poseStack, x + (width - middleWidth) / 2, y - middleHeight + 1, cornerWidth, offset, middleWidth, middleHeight, texWidth, texHeight);
        GuiComponent.blit(poseStack, x + (width - middleWidth) / 2, y + height - 1, cornerWidth, middleHeight + offset, middleWidth, middleHeight, texWidth, texHeight);

        texture = new ResourceLocation(Reference.MODID, "textures/gui/tooltip/frame/" + id + "_star.png");

        RenderSystem.setShaderTexture(0, texture);

        int xOff = 0;

        for (int i = 1; i < relic.getRelicQuality(stack) + 1; i++) {
            boolean isAliquot = i % 2 == 1;

            float color = (float) (0.85F + Math.sin(player.tickCount * Math.ceil(i / 2F) * 0.075F) * 0.3F);

            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            RenderSystem.setShaderColor(color, color, color, 1F);

            GuiComponent.blit(poseStack, x + width / 2 - 14 + xOff, y - 10, (isAliquot ? 0 : 3), 0, isAliquot ? 3 : 2, 5, 5, 5);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            xOff += 3;
        }

        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onTooltipColorEvent(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof IRelicItem relic))
            return;

        StyleData style = relic.getStyleData();

        if (style == null)
            return;

        Pair<Integer, Integer> borders = style.getBorders();

        if (borders == null)
            return;

        event.setBorderStart(borders.getKey() + 0xFF000000);
        event.setBorderEnd(borders.getValue() + 0xFF000000);
    }
}