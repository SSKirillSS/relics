package it.hurts.sskirillss.relics.client.renderer.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.entities.BlockSimulationEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.Random;

public class BlockSimulationRenderer extends EntityRenderer<BlockSimulationEntity> {
    public BlockSimulationRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(BlockSimulationEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        BlockState blockstate = pEntity.getBlockState();

        if (blockstate == null)
            return;

        if (blockstate.getRenderShape() == RenderShape.MODEL) {
            Level level = pEntity.getLevel();
            pMatrixStack.pushPose();
            BlockPos blockpos = new BlockPos(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());
            pMatrixStack.translate(-0.5D, 0.0D, -0.5D);
            BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();
            for (RenderType type : RenderType.chunkBufferLayers()) {
                if (ItemBlockRenderTypes.canRenderInLayer(blockstate, type)) {
                    ForgeHooksClient.setRenderType(type);

                    blockrenderdispatcher.getModelRenderer().tesselateBlock(level, blockrenderdispatcher.getBlockModel(blockstate), blockstate, blockpos, pMatrixStack, pBuffer.getBuffer(type), false, new Random(), blockstate.getSeed(pEntity.blockPosition()), OverlayTexture.NO_OVERLAY);
                }
            }

            ForgeHooksClient.setRenderType(null);

            pMatrixStack.popPose();
            super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(BlockSimulationEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}