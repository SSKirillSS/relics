package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;

public class BastionRingItem extends RelicItem implements ICurioItem, IHasTooltip {
    public BastionRingItem() {
        super(Rarity.RARE);
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.bastion_ring.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class BastionRingEvents {
        @SubscribeEvent
        public static void onEntityKill(LivingDeathEvent event) {
            if (!(event.getEntityLiving() instanceof PiglinEntity)) return;
            PiglinEntity entity = (PiglinEntity) event.getEntityLiving();
            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            if (entity.getCommandSenderWorld().dimension() == World.NETHER
                    && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BASTION_RING.get(), player).isPresent()
                    && random.nextFloat() <= RelicsConfig.BastionRing.LOCATE_CHANCE.get()) {
                ServerWorld world = (ServerWorld) entity.getCommandSenderWorld();
                BlockPos bastionPos = world.getChunkSource().getGenerator().findNearestMapFeature(world, Structure.BASTION_REMNANT, player.blockPosition(), 100, false);
                if (bastionPos == null) return;
                Vector3d currentVec = entity.position();
                Vector3d finalVec = currentVec.add(new Vector3d(bastionPos.getX(), entity.getY(),
                        bastionPos.getZ()).subtract(currentVec).normalize().multiply(5, 5, 5));
                int distance = (int) Math.round(currentVec.distanceTo(finalVec)) * 3;
                for (int i = 0; i < distance; i++) {
                    float x = (float) (((finalVec.x - currentVec.x) * i / distance) + currentVec.x);
                    float z = (float) (((finalVec.z - currentVec.z) * i / distance) + currentVec.z);
                    world.sendParticles(new CircleTintData(new Color(255, 240, 150),
                                    0.4F - i * 0.0125F, 180, 0.99F, false),
                            x, entity.getY() + 1.5F, z, 1, 0F, 0F, 0F, 0);
                }
            }
        }
    }
}