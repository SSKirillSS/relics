package it.hurts.sskirillss.relics.client.renderer.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import it.hurts.sskirillss.relics.blocks.PedestalBlock;
import it.hurts.sskirillss.relics.tiles.PedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;


public class PedestalTileRenderer implements BlockEntityRenderer<PedestalTile> {
    public PedestalTileRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(PedestalTile tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = tileEntity.getStack();

        if (stack == null || stack.isEmpty())
            return;

        matrixStack.pushPose();

        matrixStack.translate(0.5D, 0.5D, 0.5D);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel ibakedmodel = itemRenderer.getModel(stack, tileEntity.getLevel(), null, 0);

        matrixStack.scale(0.35F, 0.35F, 0.35F);
        matrixStack.mulPose(tileEntity.getBlockState().getValue(PedestalBlock.DIRECTION).getRotation());
        matrixStack.translate(0.0D, Math.cos(tileEntity.ticksExisted * 0.1D) * 0.2D, 0.0D);
        matrixStack.mulPose(Vector3f.YP.rotation(tileEntity.ticksExisted / 20.0F));

        itemRenderer.render(stack, ItemTransforms.TransformType.FIXED, true, matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel);

        matrixStack.popPose();
    }
}