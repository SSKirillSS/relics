package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class JellyfishNecklaceItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_TIME = "time";
    public static final String TAG_CHARGES = "charges";

    public JellyfishNecklaceItem() {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.jellyfish_necklace.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.jellyfish_necklace.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            if (livingEntity.tickCount % 20 == 0) {
                int time = NBTUtils.getInt(stack, TAG_TIME, 0);
                int charges = NBTUtils.getInt(stack, TAG_CHARGES, 0);
                if (livingEntity.isInWater()) {
                    if (time < RelicsConfig.JellyfishNecklace.TIME_PER_CHARGE.get() && charges < RelicsConfig.JellyfishNecklace.MAX_CHARGES_AMOUNT.get()) {
                        NBTUtils.setInt(stack, TAG_TIME, time + 1);
                    } else {
                        NBTUtils.setInt(stack, TAG_TIME, 0);
                        NBTUtils.setInt(stack, TAG_CHARGES, charges + 1);
                    }

                    if (charges > 0) {
                        List<LivingEntity> entities = livingEntity.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox()
                                .inflate(RelicsConfig.JellyfishNecklace.ATTACK_RADIUS_MULTIPLIER.get()));
                        if (!entities.isEmpty()) {
                            for (LivingEntity entity : entities) {
                                if (entity.isInWater() && entity != livingEntity && !(entity instanceof TameableEntity
                                        && ((TameableEntity) entity).isOwnedBy(livingEntity))) {
                                    entity.hurt(DamageSource.playerAttack((PlayerEntity) livingEntity), RelicsConfig.JellyfishNecklace.DAMAGE_PER_CHARGE.get().floatValue());
                                    NBTUtils.setInt(stack, TAG_CHARGES, charges - 1);
                                }
                            }
                        }
                    }
                } else {
                    if (time > 0) NBTUtils.setInt(stack, TAG_TIME, time - 1);
                }
            }
        }
    }

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        ICurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
        ICurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
        matrixStack.scale(0.35F, 0.35F, 0.35F);
        matrixStack.translate(0.0F, 0.25F, -0.4F);
        matrixStack.mulPose(Direction.DOWN.getRotation());
        Minecraft.getInstance().getItemRenderer()
                .renderStatic(new ItemStack(ItemRegistry.JELLYFISH_NECKLACE.get()), ItemCameraTransforms.TransformType.NONE, light, OverlayTexture.NO_OVERLAY,
                        matrixStack, renderTypeBuffer);
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class JellyfishNecklaceEvents {
        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving().isInWater()
                    && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.JELLYFISH_NECKLACE.get(), event.getEntityLiving()).isPresent()) {
                event.setAmount(event.getAmount() * RelicsConfig.JellyfishNecklace.HEALING_MULTIPLIER.get().floatValue());
            }
        }
    }
}