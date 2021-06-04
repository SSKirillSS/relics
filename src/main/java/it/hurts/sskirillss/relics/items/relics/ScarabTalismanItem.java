package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class ScarabTalismanItem extends RelicItem implements ICurioItem, IHasTooltip {
    private static final AttributeModifier SCARAB_TALISMAN_SPEED_BOOST = new AttributeModifier(UUID.fromString("09bc5b60-3277-45ee-8bf0-aae7acba4385"),
            Reference.MODID + ":" + "scarab_talisman_movement_speed", RelicsConfig.ScarabTalisman.SPEED_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

    public ScarabTalismanItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.scarab_talisman.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.scarab_talisman.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (livingEntity.getCommandSenderWorld().getBiome(livingEntity.blockPosition()).getBiomeCategory() == Biome.Category.DESERT
                || livingEntity.getCommandSenderWorld().getBiome(livingEntity.blockPosition()).getBiomeCategory() == Biome.Category.MESA) {
            if (!movementSpeed.hasModifier(SCARAB_TALISMAN_SPEED_BOOST)) {
                movementSpeed.addTransientModifier(SCARAB_TALISMAN_SPEED_BOOST);
                livingEntity.maxUpStep = Math.max(livingEntity.maxUpStep,
                        RelicsConfig.ScarabTalisman.STEP_HEIGHT.get().floatValue());
            }
        } else if (movementSpeed.hasModifier(SCARAB_TALISMAN_SPEED_BOOST)) {
            movementSpeed.removeModifier(SCARAB_TALISMAN_SPEED_BOOST);
            livingEntity.maxUpStep = 0.6F;
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed.hasModifier(SCARAB_TALISMAN_SPEED_BOOST)) {
            movementSpeed.removeModifier(SCARAB_TALISMAN_SPEED_BOOST);
            livingEntity.maxUpStep = 0.6F;
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.DESERT;
    }

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        ICurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
        ICurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
        matrixStack.scale(0.12F, 0.12F, 0.12F);
        matrixStack.translate(1.1F, 2.5F, -1.1F);
        matrixStack.mulPose(Direction.DOWN.getRotation());
        Minecraft.getInstance().getItemRenderer()
                .renderStatic(new ItemStack(ItemRegistry.SCARAB_TALISMAN.get()), ItemCameraTransforms.TransformType.NONE, light, OverlayTexture.NO_OVERLAY,
                        matrixStack, renderTypeBuffer);
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ScarabTalismanServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(), player).isPresent()
                        && (event.getSource() == DamageSource.IN_WALL
                        || (event.getSource() == DamageSource.FALL && player.getCommandSenderWorld().getBlockState(player.blockPosition().below()).getBlock() == Blocks.SAND))) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onEntityAttack(LivingAttackEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(), player).isPresent()
                        && (event.getSource() == DamageSource.IN_WALL
                        || (event.getSource() == DamageSource.FALL && player.getCommandSenderWorld().getBlockState(player.blockPosition().below()).getBlock() == Blocks.SAND))) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onBlockBreakCalculate(PlayerEvent.BreakSpeed event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() == Blocks.SAND
                        && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(), player).isPresent()) {
                    event.setNewSpeed(event.getNewSpeed() * 2.0F);
                }
            }
        }
    }
}