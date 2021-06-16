package it.hurts.sskirillss.relics.tiles.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.tiles.RunicAnvilTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class RunicAnvilTileRenderer extends TileEntityRenderer<RunicAnvilTile> {
    public RunicAnvilTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(RunicAnvilTile tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = tileEntity.getStack();
        if (stack == null || stack.isEmpty()) return;
        matrixStack.pushPose();
        matrixStack.translate(0.5F, 0.95F, 0.5F);
        matrixStack.mulPose(Direction.NORTH.getRotation());
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.render(stack, ItemCameraTransforms.TransformType.FIXED, true, matrixStack, buffer, combinedLight, combinedOverlay,
                itemRenderer.getModel(stack, tileEntity.getLevel(), null));
        matrixStack.popPose();
    }
}