package it.hurts.sskirillss.relics.client.renderer.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import it.hurts.sskirillss.relics.blocks.BloodyLecternBlock;
import it.hurts.sskirillss.relics.tiles.BloodyLecternTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BloodyLecternTileRenderer implements BlockEntityRenderer<BloodyLecternTile> {
    public BloodyLecternTileRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BloodyLecternTile tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = tileEntity.getStack();
        
        if (stack == null || stack.isEmpty())
            return;

        Level world = tileEntity.getLevel();

        if (world == null)
            return;

        Direction direction = world.getBlockState(tileEntity.getBlockPos()).getValue(BloodyLecternBlock.FACING);

        matrixStack.pushPose();
        matrixStack.translate(0.5F, 1.06F, 0.5F);
        
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

        itemRenderer.render(stack, ItemTransforms.TransformType.FIXED, true, matrixStack, buffer, combinedLight, combinedOverlay,
                itemRenderer.getModel(stack, tileEntity.getLevel(), null, 0));

        matrixStack.popPose();
    }
}