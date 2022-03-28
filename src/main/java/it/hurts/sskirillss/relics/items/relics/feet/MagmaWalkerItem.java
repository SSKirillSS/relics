package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.blocks.MagmaStoneBlock;
import it.hurts.sskirillss.relics.client.renderer.items.models.MagmaWalkerModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.BlockRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class MagmaWalkerItem extends RelicItem<RelicStats> {
    public MagmaWalkerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#ff6900", "#ff2e00")
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<RelicStats> getConfigData() {
        return RelicConfigData.builder()
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        World world = livingEntity.getCommandSenderWorld();
        BlockPos pos = livingEntity.blockPosition();
        BlockState state = world.getBlockState(pos.below());
        FluidState fluid = state.getFluidState();

        if (DurabilityUtils.isBroken(stack) || livingEntity.isSpectator())
            return;

        if (fluid.getType() == Fluids.LAVA || fluid.getType() == Fluids.FLOWING_LAVA) {
            world.setBlockAndUpdate(pos.below(), BlockRegistry.MAGMA_STONE_BLOCK.get().defaultBlockState()
                    .setValue(MagmaStoneBlock.LEVEL, fluid.isSource() ? 0 : fluid.getValue(FlowingFluid.LEVEL))
                    .setValue(MagmaStoneBlock.FALLING, !fluid.isSource() && fluid.getValue(FlowingFluid.FALLING)));

            world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 1, 1, 1);
            world.playSound(null, livingEntity.blockPosition(), SoundEvents.BASALT_PLACE,
                    SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        if (state.getBlock() == BlockRegistry.MAGMA_STONE_BLOCK.get() && state.getValue(MagmaStoneBlock.AGE) > 0)
            world.setBlock(pos.below(), BlockRegistry.MAGMA_STONE_BLOCK.get().defaultBlockState(), 2);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new MagmaWalkerModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MagmaWalkerServerEvents {

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.MAGMA_WALKER.get()).isEmpty()
                    && event.getSource() == DamageSource.HOT_FLOOR)
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            if (!EntityUtils.findEquippedCurio(event.getEntityLiving(), ItemRegistry.MAGMA_WALKER.get()).isEmpty()
                    && event.getSource() == DamageSource.HOT_FLOOR)
                event.setCanceled(true);
        }
    }
}