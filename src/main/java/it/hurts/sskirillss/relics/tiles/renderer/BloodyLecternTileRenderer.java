package it.hurts.sskirillss.relics.tiles.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.blocks.BloodyLecternBlock;
import it.hurts.sskirillss.relics.tiles.BloodyLecternTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class BloodyLecternTileRenderer extends TileEntityRenderer<BloodyLecternTile> {
    public BloodyLecternTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(BloodyLecternTile tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = tileEntity.getStack();
        if (stack == null || stack.isEmpty()) return;
        matrixStack.pushPose();
        matrixStack.translate(0.5F, 1.06F, 0.5F);
        Direction direction = tileEntity.getLevel().getBlockState(tileEntity.getBlockPos()).getValue(BloodyLecternBlock.FACING);
        float xRot = 0F, yRot = 0F, xOff = 0F, yOff = 0F, zOff = 0F;
        if (direction == Direction.NORTH) {
            xRot = 292.5F;
            yOff = -0.1F;
        } else if (direction == Direction.EAST) {
            yRot = 90F;
            xRot = 67.5F;
            yOff = -0.1F;
        } else if (direction == Direction.SOUTH) {
            yRot = 180F;
            xRot = 292.5F;
            yOff = -0.1F;
        } else if (direction == Direction.WEST) {
            yRot = 270F;
            xRot = 67.5F;
            yOff = -0.1F;
        }
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        matrixStack.mulPose(Vector3f.XN.rotationDegrees(xRot));
        matrixStack.translate(xOff, yOff, zOff);
        matrixStack.scale(0.7F, 0.7F, 0.7F);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.render(stack, ItemCameraTransforms.TransformType.FIXED, true, matrixStack, buffer, combinedLight, combinedOverlay,
                itemRenderer.getModel(stack, tileEntity.getLevel(), null));
        matrixStack.popPose();
    }
}