package it.hurts.sskirillss.relics.items.relics.hands;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.hurts.sskirillss.relics.client.models.effects.IceCubeModel;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.CHARGE;

public class WoolMittenItem extends RelicItem {
    private static final Map<LivingEntity, Integer> COLD_TOUCH_CHARGES = new HashMap<>();
    private static final Map<LivingEntity, Integer> MELT_TIMER = new HashMap<>();
    private static boolean flag = false;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("mold")
                                .stat(StatData.builder("size")
                                        .icon(StatIcons.SIZE)
                                        .initialValue(12D, 32D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .stat(StatData.builder("damage")
                                        .icon(StatIcons.DEALT_DAMAGE)
                                        .initialValue(0.05D, 0.25D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(StatData.builder("stun")
                                        .icon(StatIcons.STUN)
                                        .initialValue(0.025D, 0.05D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 3))
                                        .build())
                                .stat(StatData.builder("freeze")
                                        .icon(StatIcons.FREEZING)
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.3D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("frosty_shroud")
                                .active(CastData.builder()
                                        .type(CastType.INSTANTANEOUS)
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("duration")
                                        .initialValue(80D, 150D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5D)
                                        .formatValue(value -> (int) MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("cooldown")
                                        .initialValue(20D, 15D)
                                        .upgradeModifier(UpgradeOperation.ADD, -0.5D)
                                        .formatValue(value -> (int) MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("cold_touch")
                                .stat(StatData.builder("max_charges")
                                        .initialValue(22D, 8D)
                                        .upgradeModifier(UpgradeOperation.ADD, -1D)
                                        .formatValue(value -> {
                                            int roundedValue = (int) MathUtils.round(value, 0);
                                            return Math.max(roundedValue, 1);
                                        })
                                        .build())
                                .stat(StatData.builder("melt_time")
                                        .initialValue(50D, 100D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_TOTAL, 0.05D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
                                        .build())
                                .build())
                        .build())

                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.COLD)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        Level level = player.getCommandSenderWorld();
        RandomSource random = level.getRandom();

        ItemStack relicStack = EntityUtils.findEquippedCurio(player, ItemRegistry.WOOL_MITTEN.get());

        if (relicStack.isEmpty() || !(relicStack.getItem() instanceof IRelicItem))
            return;

        if (ability.equals("frosty_shroud")) {
            double duration = getStatValue(stack, "frosty_shroud", "duration") * 20;
            double radius = getStatValue(stack, "frosty_shroud", "radius");

            Vec3 pEyePos = player.getEyePosition();

            AABB sphereBox = new AABB(pEyePos.x - radius, pEyePos.y - radius, pEyePos.z - radius,
                    pEyePos.x + radius, pEyePos.y + radius, pEyePos.z + radius);

            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, sphereBox,
                    entity -> entity != player && entity.distanceToSqr(player) <= radius * radius);

            if (targets.isEmpty())
                return;

            if (!level.isClientSide())
                setAbilityCooldown(stack, "frosty_shroud", (int) (getStatValue(stack, "frosty_shroud", "cooldown") * 20));

            for (LivingEntity currentTarget : targets) {
                Vec3 targetEyePos = currentTarget.getEyePosition();
                Vec3 delta = targetEyePos.subtract(pEyePos).normalize();

                for (int i = 0; i < radius * 20; ++i) {
                    double progress = i / (radius * 20.0);
                    Vec3 particlePos = pEyePos.add(delta.scale(progress * radius));
                    level.playSound(null, player.blockPosition(), SoundEvents.ALLAY_THROW, SoundSource.MASTER, 1F, 0.75F + random.nextFloat() * 0.5F);
                    level.addParticle(ParticleUtils.constructSimpleSpark(new Color(32, 195 + random.nextInt(60), 208), 0.2F + random.nextFloat() * 0.5F,
                                    60 + random.nextInt(60), 0.95F),
                            particlePos.x, particlePos.y, particlePos.z,
                            0, MathUtils.randomFloat(random) * 0.075F, 0);
                }
            }
            flag = true;
            if (!level.isClientSide()) {
                for (LivingEntity entity : targets) {
                    if (entity.invulnerableTime > 0 || EntityUtils.isAlliedTo(player, entity))
                        continue;

                    player.attack(entity);
                    entity.addEffect(new MobEffectInstance(EffectRegistry.FROSTBITE, (int) duration, 0));
                }
            }
            flag = false;
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        Player player = Minecraft.getInstance().player;
        ItemStack relicStack = EntityUtils.findEquippedCurio(player, ItemRegistry.WOOL_MITTEN.get());

        if (player == null)
            return;

        if (relicStack.isEmpty() ||!(relicStack.getItem() instanceof IRelicItem relic))
            return;

        for (LivingEntity entity : MELT_TIMER.keySet()) {
            int meltTime = MELT_TIMER.get(entity);
            if (meltTime > 0) {
                meltTime--;
                MELT_TIMER.put(entity, meltTime);
                if (meltTime <= 1) {
                    int charges = COLD_TOUCH_CHARGES.getOrDefault(entity, 0);
                    if (charges > 0) {
                        COLD_TOUCH_CHARGES.put(entity, charges - 1);
                        MELT_TIMER.put(entity, (int) relic.getStatValue(relicStack, "cold_touch", "melt_time"));
                    }
                }
            }
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (stack.getItem() == prevStack.getItem())
            return;
        COLD_TOUCH_CHARGES.clear();
        MELT_TIMER.clear();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack.getItem() == newStack.getItem())
            return;
        COLD_TOUCH_CHARGES.clear();
        MELT_TIMER.clear();
    }

    @EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onEntityHit(LivingDamageEvent.Post event) {
            if (!(event.getSource().getEntity() instanceof Player player))
                return;

            ItemStack relicStack = EntityUtils.findEquippedCurio(player, ItemRegistry.WOOL_MITTEN.get());
            LivingEntity target = event.getEntity();

            if (!player.getMainHandItem().isEmpty())
                return;

            if (relicStack.isEmpty() || !(relicStack.getItem() instanceof IRelicItem relic))
                return;

            if (target.isOnFire())
                target.clearFire();

            if (flag)
                return;

            int charges = COLD_TOUCH_CHARGES.getOrDefault(target, 0);
            double maxCharges = (int) relic.getStatValue(relicStack, "cold_touch", "max_charges");
            double meltTime = (int) relic.getStatValue(relicStack, "cold_touch", "melt_time");

            COLD_TOUCH_CHARGES.put(target, charges + 1);
            MELT_TIMER.put(target, (int) meltTime);

            if (charges >= maxCharges) {
                target.addEffect(new MobEffectInstance(EffectRegistry.FROSTBITE, 180, 0));
                target.addEffect(new MobEffectInstance(EffectRegistry.STUN, 180, 0));
                COLD_TOUCH_CHARGES.put(target, 0);

                for (LivingEntity nearby : target.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3))) {
                    if (nearby != target && !EntityUtils.isAlliedTo(player, nearby)) {
                        int nearbyCharges = COLD_TOUCH_CHARGES.getOrDefault(nearby, 0);
                        COLD_TOUCH_CHARGES.put(nearby, nearbyCharges + 1);
                        nearby.push(nearby.getX() - target.getX(), 0, nearby.getZ() - target.getZ());
                        nearby.addEffect(new MobEffectInstance(EffectRegistry.FROSTBITE, 80, 1));
                        nearby.addEffect(new MobEffectInstance(EffectRegistry.STUN, 180, 0));
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onEntityRender(RenderLivingEvent.Pre<?, ?> event) {
            LivingEntity entity = event.getEntity();
            PoseStack poseStack = event.getPoseStack();
            Player player = Minecraft.getInstance().player;

            if (player == null || entity == player)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.WOOL_MITTEN.get());

            if (stack.isEmpty() || !(stack.getItem() instanceof IRelicItem))
                return;

            int charges = COLD_TOUCH_CHARGES.getOrDefault(entity, 0);
            if (charges <= 0 || flag)
                return;


            poseStack.pushPose();
            poseStack.translate(0, entity.getBbHeight() + 0.5, 0);
            poseStack.scale(0.5F, 0.5F, 0.5F);

            Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 entityPos = entity.position().add(0, entity.getBbHeight() + 0.5, 0);
            Vec3 cameraDir = cameraPos.subtract(entityPos).normalize();

            float yaw = (float) Math.toDegrees(Math.atan2(cameraDir.z, cameraDir.x)) - 90.0F;
            float pitch = (float) Math.toDegrees(Math.asin(cameraDir.y));

            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));

            for (int i = 1; i <= charges; i++) {
                poseStack.pushPose();
                float spacing = 0.6F;
                float xOffset = (i - charges / 2.0F) * spacing;

                poseStack.translate(xOffset, 0, 0);

                IceCubeModel.createBodyLayer().bakeRoot().render(poseStack, event.getMultiBufferSource().getBuffer(RenderType.entityCutout(IceCubeModel.TEXTURE.getModel())),
                        event.getPackedLight(), OverlayTexture.NO_OVERLAY);

                poseStack.popPose();
            }
            poseStack.popPose();
        }

        @SubscribeEvent
        public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
            Player player = event.getEntity();

            ItemStack relicStack = EntityUtils.findEquippedCurio(player, ItemRegistry.WOOL_MITTEN.get());

            if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty() || !(relicStack.getItem() instanceof IRelicItem relic))
                return;

            Level level = player.level();
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            int layers = block == Blocks.SNOW ? state.getValue(SnowLayerBlock.LAYERS) : block == Blocks.SNOW_BLOCK ? 9 : 0;

            if (layers == 0)
                return;

            Inventory inventory = player.getInventory();

            int size = (int) Math.round(relic.getStatValue(relicStack, "mold", "size"));

            Optional<Integer> slot = EntityUtils.getSlotsWithItem(player, ItemRegistry.SOLID_SNOWBALL.get()).stream()
                    .filter(id -> inventory.getItem(id).getOrDefault(CHARGE, 0) < size)
                    .max(Comparator.comparingInt(s -> inventory.items.get(s).getOrDefault(CHARGE, 0)));

            if (slot.isEmpty()) {
                if (inventory.add(new ItemStack(ItemRegistry.SOLID_SNOWBALL.get()))) {
                    slot = EntityUtils.getSlotsWithItem(player, ItemRegistry.SOLID_SNOWBALL.get()).stream().findFirst();

                    if (slot.isEmpty())
                        return;
                } else
                    return;
            }

            ItemStack stack = inventory.getItem(slot.get());

            level.destroyBlock(pos, false);

            int snow = stack.getOrDefault(CHARGE, 0);

            stack.set(CHARGE, Math.min(snow + layers, size));
        }
    }
}