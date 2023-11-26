package it.hurts.sskirillss.relics.client.renderer.items.items.back;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.hurts.sskirillss.relics.client.models.items.back.ElytraBoosterModel;
import it.hurts.sskirillss.relics.items.relics.back.ElytraBoosterItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class ElytraBoosterRenderer implements ICurioRenderer {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation(Reference.MODID, "textures/models/items/elytra_booster_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation(Reference.MODID, "textures/models/items/elytra_booster_1.png");

    private final ElytraBoosterModel model;

    public ElytraBoosterRenderer() {
        this.model = new ElytraBoosterModel(Minecraft.getInstance().getEntityModels().bakeLayer(ElytraBoosterModel.LAYER));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();

        LivingEntity entity = slotContext.entity();

        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ICurioRenderer.followBodyRotations(entity, this.model);

        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(NBTUtils.getInt(stack,
                ElytraBoosterItem.TAG_FUEL, 0) > 0 ? TEXTURE_1 : TEXTURE_0), false, stack.hasFoil());

        this.model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
    }
}