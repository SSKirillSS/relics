package it.hurts.sskirillss.relics.items.relics.boots;

import it.hurts.sskirillss.relics.blocks.MagmaStoneBlock;
import it.hurts.sskirillss.relics.init.BlockRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.MagmaWalkerModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class MagmaWalkerItem extends RelicItem<RelicStats> implements ICurioItem {
    public MagmaWalkerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .model(new MagmaWalkerModel())
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.NETHER)
                        .chance(0.15F)
                        .build())
                .build());
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        World world = livingEntity.getCommandSenderWorld();
        BlockPos pos = livingEntity.blockPosition();
        BlockState state = world.getBlockState(pos.below());

        if (state == Fluids.LAVA.getSource().defaultFluidState().createLegacyBlock()) {
            world.setBlockAndUpdate(pos.below(), BlockRegistry.MAGMA_STONE_BLOCK.get().defaultBlockState());
            world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 1, 1, 1);
            world.playSound(null, livingEntity.blockPosition(), SoundEvents.BASALT_PLACE,
                    SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        if (state.getBlock() == BlockRegistry.MAGMA_STONE_BLOCK.get() && state.getValue(MagmaStoneBlock.AGE) > 0)
            world.setBlock(pos.below(), BlockRegistry.MAGMA_STONE_BLOCK.get().defaultBlockState(), 2);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class MagmaWalkerServerEvents {

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (event.getSource() == DamageSource.HOT_FLOOR && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MAGMA_WALKER.get(),
                    event.getEntityLiving()).isPresent())
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            if (event.getSource() == DamageSource.HOT_FLOOR && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.MAGMA_WALKER.get(),
                    event.getEntityLiving()).isPresent())
                event.setCanceled(true);
        }
    }
}