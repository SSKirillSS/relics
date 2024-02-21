package it.hurts.sskirillss.relics.items.relics.ring;

import it.hurts.sskirillss.relics.entities.ChairEntity;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.BLOCK_STATE;

public class CamouflageRingItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("hide")
                                .stat(StatData.builder("example")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0D, 1D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("morph")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .stat(StatData.builder("distance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(2D, 4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 200))
                .loot(LootData.builder()
                        .entry(LootCollections.BASTION)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        var level = player.getCommandSenderWorld();

        if (level.isClientSide())
            return;

        if (ability.equals("morph")) {
            var distance = getStatValue(stack, "morph", "distance");

            Vec3 view = player.getViewVector(0);
            Vec3 eyeVec = player.getEyePosition(0);

            BlockHitResult ray = level.clip(new ClipContext(eyeVec, eyeVec.add(view.x * distance, view.y * distance,
                    view.z * distance), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

            if (ray.getType() == HitResult.Type.MISS)
                return;

            BlockPos pos = ray.getBlockPos();

            stack.set(BLOCK_STATE, level.getBlockState(pos));

            Vec3 center = Vec3.atCenterOf(pos.relative(ray.getDirection()));

            ChairEntity chair = new ChairEntity(EntityRegistry.CHAIR.get(), level);

            chair.setPos(center);

            player.startRiding(chair);

            level.addFreshEntity(chair);
        }
    }

    @Override
    public void tickActiveAbilitySelection(ItemStack stack, Player player, String ability) {
        if (ability.equals("morph")) {
            var distance = getStatValue(stack, "morph", "distance");

            ParticleUtils.createCyl(ParticleUtils.constructSimpleSpark(new Color(103, 255, 0), 0.25F, 0, 0.5F), player.position(), player.level(), distance, 0.1F, true);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        var level = player.getCommandSenderWorld();

        {
            var pos = player.getBoundingBox().getBottomCenter().add(0F, player.getBbHeight(), 0F);

            boolean isHiding = level.getBlockState(new BlockPos((int) Math.floor(pos.x()), (int) Math.floor(pos.y()), (int) Math.floor(pos.z()))).getBlock() instanceof BushBlock;

            if (isHiding)
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
        }

        {
            var state = stack.get(BLOCK_STATE);

            if (state != null) {
                var random = level.getRandom();

                if (random.nextInt(5) == 0)
                    state.getBlock().animateTick(state, level, player.blockPosition(), random);

                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 2, 0, false, false));

                var shape = state.getShape(level, player.blockPosition());

                if (!shape.isEmpty()) {
                    var aabb = shape.bounds();

                    if (!player.getBoundingBox().equals(aabb))
                        player.refreshDimensions();
                }

                if (!(player.getVehicle() instanceof ChairEntity)) {
                    stack.set(BLOCK_STATE, null);

                    player.refreshDimensions();
                }
            }
        }
    }

    @EventBusSubscriber
    public static class CommonEvents {
        @SubscribeEvent
        public static void onEntityResize(EntityEvent.Size event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            var level = player.getCommandSenderWorld();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.CAMOUFLAGE_RING.get());

            if (stack.isEmpty())
                return;

            var state = stack.get(BLOCK_STATE);

            if (state == null)
                return;

            var shape = state.getShape(level, player.blockPosition());

            if (shape.isEmpty())
                return;

            var aabb = shape.bounds();

            event.setNewSize(EntityDimensions.fixed((float) aabb.getXsize(), (float) aabb.getYsize()));
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onPlayerRender(RenderPlayerEvent.Pre event) {
            var player = event.getEntity();
            var level = player.getCommandSenderWorld();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.CAMOUFLAGE_RING.get());

            if (stack.isEmpty())
                return;

            var state = stack.get(BLOCK_STATE);

            if (state == null)
                return;

            var poseStack = event.getPoseStack();
            var blockRenderer = Minecraft.getInstance().getBlockRenderer();

            poseStack.translate(-0.5F, 0, -0.5F);

            blockRenderer.renderBatched(state, player.blockPosition(), level, poseStack, event.getMultiBufferSource().getBuffer(RenderType.CUTOUT), true, level.getRandom());

            poseStack.translate(0.5F, 0, 0.5F);
        }
    }
}