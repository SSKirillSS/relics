package it.hurts.sskirillss.relics.client.models.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.models.parts.HaloModel;
import it.hurts.sskirillss.relics.client.models.parts.WingsModel;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.necklace.HolyLocketItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class WingsLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final HaloModel haloModel;
    private final WingsModel wingsModel;

    public WingsLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);

        haloModel = new HaloModel(HaloModel.createBodyLayer().bakeRoot());
        wingsModel = new WingsModel(WingsModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        ItemStack stackLocal = EntityUtils.findEquippedCurio(Minecraft.getInstance().player, ItemRegistry.HOLY_LOCKET.get());

        if (!(stackLocal.getItem() instanceof HolyLocketItem relic) || !relic.isAbilityOnCooldown(stackLocal, "blessing"))
            return;

        pPoseStack.pushPose();

        pPoseStack.scale(0.75F, 0.75F, 0.75F);
        pPoseStack.translate(0F, Math.sin(pLivingEntity.tickCount * 0.1F) * 0.05F, 0F);

        ICurioRenderer.followBodyRotations(pLivingEntity, haloModel);

        haloModel.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.entityTranslucentCull(HaloModel.LAYER_LOCATION.getModel())), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        pPoseStack.popPose();

        pPoseStack.pushPose();

        ICurioRenderer.followBodyRotations(pLivingEntity, wingsModel);

        wingsModel.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.entityTranslucentCull(WingsModel.LAYER_LOCATION.getModel())), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        pPoseStack.popPose();
    }
}