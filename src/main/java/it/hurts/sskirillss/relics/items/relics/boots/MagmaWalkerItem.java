package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.blocks.MagmaStoneBlock;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.BlockRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.relics.renderer.MagmaWalkerModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class MagmaWalkerItem extends RelicItem<RelicStats> implements ICurioItem {
    public MagmaWalkerItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.magma_walker.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.magma_walker.shift_2"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        World world = livingEntity.getCommandSenderWorld();
        if (world.getBlockState(livingEntity.blockPosition().below()) == Fluids.LAVA.getSource().defaultFluidState().createLegacyBlock()) {
            BlockPos pos = livingEntity.blockPosition();
            world.setBlockAndUpdate(pos.below(), BlockRegistry.MAGMA_STONE_BLOCK.get().defaultBlockState());
            world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 1, 1, 1);
        }

        if (world.getBlockState(livingEntity.blockPosition().below()).getBlock() == BlockRegistry.MAGMA_STONE_BLOCK.get()
                && world.getBlockState(livingEntity.blockPosition().below()).getValue(MagmaStoneBlock.AGE) > 0) {
            world.setBlock(livingEntity.blockPosition().below(), BlockRegistry.MAGMA_STONE_BLOCK.get().defaultBlockState(), 2);
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.NETHER;
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/magma_walker.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        MagmaWalkerModel model = new MagmaWalkerModel();
        matrixStack.pushPose();
        matrixStack.scale(1.025F, 1.025F, 1.025F);
        model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
        ICurio.RenderHelper.followBodyRotations(livingEntity, model);
        model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityTranslucent(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MagmaWalkerServerEvents {

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (event.getSource() == DamageSource.HOT_FLOOR) {
                LivingEntity player = event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MAGMA_WALKER.get(), player).isPresent()) {
                    event.setCanceled(true);
                }
            }
        }
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            if (event.getSource() == DamageSource.HOT_FLOOR) {
                LivingEntity player = event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MAGMA_WALKER.get(), player).isPresent()) {
                    event.setCanceled(true);
                }
            }
        }
    }
}