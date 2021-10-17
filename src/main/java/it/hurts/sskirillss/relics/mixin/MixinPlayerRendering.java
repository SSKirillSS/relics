package it.hurts.sskirillss.relics.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRendering {
    @Inject(at = @At(value = "TAIL"), method = "renderRightHand")
    private void renderHand(MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, AbstractClientPlayerEntity player, CallbackInfo callbackInfo) {
        renderArm(player, (PlayerRenderer) (Object) this, matrixStack, buffer, light);
    }

    private void renderArm(AbstractClientPlayerEntity player, PlayerRenderer renderer, MatrixStack matrix, IRenderTypeBuffer buffer, int light) {
        if (player.isSpectator())
            return;

        CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(curios -> {
            ICurioStacksHandler handler = curios.getCurios().get(SlotTypePreset.HANDS.getIdentifier());

            if (handler == null)
                return;

            IDynamicStackHandler items = handler.getStacks();

            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack stack = items.getStackInSlot(i);

                if (!(stack.getItem() instanceof RelicItem) || RelicItem.isBroken(stack)
                        || !handler.getRenders().get(i))
                    continue;

                RelicItem<?> relic = (RelicItem<?>) stack.getItem();
                BipedModel<LivingEntity> curioModel = relic.getModel();

                if (curioModel == null)
                    continue;

                matrix.pushPose();

                curioModel.setupAnim(player, 0, 0, 0, 0, 0);
                curioModel.prepareMobModel(player, 0, 0, 0);
                ICurio.RenderHelper.followBodyRotations(player, curioModel);
                curioModel.renderToBuffer(matrix, buffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation(
                        Reference.MODID, "textures/items/models/" + relic.getRegistryName().getPath() + ".png"))),
                        light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                matrix.popPose();

                break;
            }
        });
    }
}