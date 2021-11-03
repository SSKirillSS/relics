package it.hurts.sskirillss.relics.client.renderer.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.blocks.PedestalBlock;
import it.hurts.sskirillss.relics.tiles.PedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class PedestalTileRenderer extends TileEntityRenderer<PedestalTile> {
    public PedestalTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(PedestalTile tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = tileEntity.getStack();
        if (stack == null || stack.isEmpty()) return;
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.55, 0.5);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        IBakedModel ibakedmodel = itemRenderer.getModel(stack, tileEntity.getLevel(), null);
        matrixStack.scale(0.35F, 0.35F, 0.35F);
        matrixStack.mulPose(tileEntity.getBlockState().getValue(PedestalBlock.DIRECTION).getRotation());
        matrixStack.translate(0.0D, MathHelper.sin(tileEntity.ticksExisted / 10.0F) * 0.25F, 0.0D);
        matrixStack.mulPose(Vector3f.YP.rotation(tileEntity.ticksExisted / 20.0F));
        matrixStack.translate(0.0D, MathHelper.sin(tileEntity.ticksExisted / 10.0F) * 0.25F, 0.0D);
        matrixStack.mulPose(Vector3f.YP.rotation(tileEntity.ticksExisted / 20.0F));
        itemRenderer.render(stack, ItemCameraTransforms.TransformType.FIXED, true, matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);
        matrixStack.popPose();
    }
}