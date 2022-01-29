package it.hurts.sskirillss.relics.client.renderer.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.blocks.RunicAnvilBlock;
import it.hurts.sskirillss.relics.tiles.RunicAnvilTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class RunicAnvilTileRenderer implements BlockEntityRenderer<RunicAnvilTile> {
    public RunicAnvilTileRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(RunicAnvilTile tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        List<ItemStack> items = tileEntity.getItems();

        int iteration = 0;

        for (ItemStack stack : items) {
            matrixStack.pushPose();

            matrixStack.translate(0.5F, 0.95F + (iteration++ * 0.035F), 0.5F);
            matrixStack.mulPose(tileEntity.getBlockState().getValue(RunicAnvilBlock.FACING).getRotation());
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.render(stack, ItemTransforms.TransformType.FIXED, true, matrixStack, buffer, combinedLight, combinedOverlay,
                    itemRenderer.getModel(stack, tileEntity.getLevel(), null, 0));

            matrixStack.popPose();
        }
    }
}