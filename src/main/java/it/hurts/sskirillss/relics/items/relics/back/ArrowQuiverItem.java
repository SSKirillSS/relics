package it.hurts.sskirillss.relics.items.relics.back;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.ArrowRainEntity;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.data.PredicateInfo;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.*;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArrowQuiverItem extends RelicItem {
    private static final String TAG_LEAP = "leap";
    private static final String TAG_ARROWS = "arrows";

    @Override
    public RelicData constructRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("receptacle", RelicAbilityEntry.builder()
                                .maxLevel(10)
                                .stat("slots", RelicAbilityStat.builder()
                                        .initialValue(2, 5)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1)
                                        .formatValue(value -> (int) Math.round(value))
                                        .build())
                                .build())
                        .ability("leap", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .maxLevel(10)
                                .active(AbilityCastType.INSTANTANEOUS, AbilityCastPredicate.builder()
                                        .predicate("target", data -> {
                                            Player player = data.getPlayer();
                                            Level level = player.level();

                                            double maxDistance = player.getBlockReach() + 1;

                                            Vec3 view = player.getViewVector(0);
                                            Vec3 eyeVec = player.getEyePosition(0);

                                            BlockHitResult ray = level.clip(new ClipContext(eyeVec, eyeVec.add(view.x * maxDistance, view.y * maxDistance,
                                                    view.z * maxDistance), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));

                                            boolean isCorrect = ray.getType() != HitResult.Type.MISS && ray.getLocation().y() < player.getEyePosition().y();

                                            return PredicateInfo.builder()
                                                    .condition(isCorrect)
                                                    .build();
                                        })
                                )
                                .stat("multiplier", RelicAbilityStat.builder()
                                        .initialValue(0.1, 0.5)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_TOTAL, 0.15)
                                        .formatValue(value -> (int) (MathUtils.round(value, 2) * 100))
                                        .build())
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(4, 6)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.1)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat("cooldown", RelicAbilityStat.builder()
                                        .initialValue(15, 12)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_TOTAL, -0.1)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability("agility", RelicAbilityEntry.builder()
                                .requiredLevel(10)
                                .requiredPoints(2)
                                .maxLevel(5)
                                .stat("modifier", RelicAbilityStat.builder()
                                        .initialValue(1, 1)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1)
                                        .formatValue(value -> (int) ((1 + MathUtils.round(value, 0))) * 100)
                                        .build())
                                .build())
                        .ability("rain", RelicAbilityEntry.builder()
                                .requiredLevel(15)
                                .maxLevel(10)
                                .active(AbilityCastType.INSTANTANEOUS, AbilityCastPredicate.builder()
                                        .predicate("arrow", data -> {
                                                    int count = 0;

                                                    for (ItemStack stack : getArrows(data.getStack()))
                                                        count += stack.getCount();

                                                    return PredicateInfo.builder()
                                                            .condition(count > 0)
                                                            .build();
                                                }
                                        )
                                )
                                .stat("radius", RelicAbilityStat.builder()
                                        .initialValue(3, 5)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.15)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat("duration", RelicAbilityStat.builder()
                                        .initialValue(10, 15)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.25)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat("delay", RelicAbilityStat.builder()
                                        .initialValue(1, 0.75)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_TOTAL, -0.15)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 20, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#eed551", "#dcbe1d")
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {
        Level level = player.getCommandSenderWorld();
        RandomSource random = level.getRandom();

        if (ability.equals("rain")) {
            if (getArrows(stack).isEmpty())
                return;

            double maxDistance = 32;

            Vec3 view = player.getViewVector(0);
            Vec3 eyeVec = player.getEyePosition(0);

            BlockHitResult ray = level.clip(new ClipContext(eyeVec, eyeVec.add(view.x * maxDistance, view.y * maxDistance,
                    view.z * maxDistance), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));

            Vec3 target = ray.getLocation();

            int duration = (int) Math.round(AbilityUtils.getAbilityValue(stack, "rain", "duration") * 20);

            ArrowRainEntity rain = new ArrowRainEntity(EntityRegistry.ARROW_RAIN.get(), level);

            rain.setDelay((int) Math.round(AbilityUtils.getAbilityValue(stack, "rain", "delay") * 20));
            rain.setRadius((float) AbilityUtils.getAbilityValue(stack, "rain", "radius"));
            rain.setQuiver(stack.copy());
            rain.setDuration(duration);
            rain.setOwner(player);
            rain.setPos(target);

            level.addFreshEntity(rain);

            AbilityUtils.setAbilityCooldown(stack, "rain", duration);
        }

        if (ability.equals("leap")) {
            double maxDistance = player.getBlockReach() + 1;

            Vec3 view = player.getViewVector(0);
            Vec3 eyeVec = player.getEyePosition(0);

            BlockHitResult ray = level.clip(new ClipContext(eyeVec, eyeVec.add(view.x * maxDistance, view.y * maxDistance,
                    view.z * maxDistance), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));

            if (ray.getType() != HitResult.Type.MISS && ray.getLocation().y() < player.getEyePosition().y()) {
                Vec3 motion = player.getLookAngle().scale(-1).normalize().scale(2F);

                for (int i = 0; i < 100; i++) {
                    level.addParticle(ParticleTypes.SPIT, player.getX(), player.getY(), player.getZ(),
                            motion.x() + MathUtils.randomFloat(random) * 0.1F,
                            motion.y() + MathUtils.randomFloat(random) * 0.25F,
                            motion.z() + MathUtils.randomFloat(random) * 0.1F);
                }

                if (!level.isClientSide()) {
                    NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayer) player);

                    NBTUtils.setInt(stack, TAG_LEAP, 0);

                    AbilityUtils.setAbilityCooldown(stack, "leap", (int) Math.round(AbilityUtils.getAbilityValue(stack, "leap", "cooldown") * 20));

                    level.playSound(null, player.blockPosition(), SoundRegistry.LEAP.get(), SoundSource.MASTER, 1F, 1F + random.nextFloat() * 0.5F);
                }
            }
        }
    }

    @Override
    public void tickActiveAbilitySelection(ItemStack stack, Player player, String ability) {
        Level level = player.getCommandSenderWorld();

        if (ability.equals("rain")) {
            double maxDistance = 32;

            Vec3 view = player.getViewVector(0);
            Vec3 eyeVec = player.getEyePosition(0);

            BlockHitResult ray = level.clip(new ClipContext(eyeVec, eyeVec.add(view.x * maxDistance, view.y * maxDistance,
                    view.z * maxDistance), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));

            Vec3 target = ray.getLocation();

            ParticleUtils.createCyl(new CircleTintData(new Color(255, 255, 255), 0.2F, 0, 1F, false),
                    target, level, AbilityUtils.getAbilityValue(stack, "rain", "radius"), 0.2F);
        }
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof Player player))
            return;

        Level level = player.level();

        if (AbilityUtils.canUseAbility(stack, "leap")) {
            int leap = NBTUtils.getInt(stack, TAG_LEAP, -1);

            if (leap >= 0) {
                if (!level.isClientSide()) {
                    NBTUtils.setInt(stack, TAG_LEAP, ++leap);

                    player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING.get(), 5, 0, false, false));

                    player.fallDistance = 0F;

                    if (leap >= 5 && (player.onGround() || player.getAbilities().flying
                            || player.isFallFlying() || player.isInWater() || player.isInLava()
                            || leap >= AbilityUtils.getAbilityValue(stack, "leap", "duration") * 20)) {
                        NBTUtils.clearTag(stack, TAG_LEAP);
                    }
                }

                if (player.isUsingItem() && player.getMainHandItem().getItem() instanceof BowItem) {
                    player.setDeltaMovement(player.getDeltaMovement().multiply(0.975F, player.getDeltaMovement().y() > 0 ? 0.9F : 0F, 0.975F));
                }
            }
        }

        if (AbilityUtils.canUseAbility(stack, "agility")) {
            if (player.isUsingItem() && player.getMainHandItem().getItem() instanceof BowItem) {
                for (int i = 0; i < 1; i++)
                    player.updatingUsingItem();
            }
        }
    }

    public static int getSlotsAmount(ItemStack stack) {
        return (int) Math.round(AbilityUtils.getAbilityValue(stack, "receptacle", "slots"));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new ArrowQuiverTooltip(getArrows(stack), getSlotsAmount(stack)));
    }

    public static List<ItemStack> getArrows(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        return tag == null ? new ArrayList<>() : tag.getList(TAG_ARROWS, 10).stream()
                .map(CompoundTag.class::cast)
                .map(ItemStack::of)
                .collect(Collectors.toList());
    }

    public static int insertStack(ItemStack stack, ItemStack arrow) {
        if (!arrow.getItem().canFitInsideContainerItems())
            return 0;

        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(TAG_ARROWS))
            tag.put(TAG_ARROWS, new ListTag());

        ListTag list = tag.getList(TAG_ARROWS, 10);

        List<CompoundTag> entries = list.stream()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .filter(nbt -> ItemStack.isSameItemSameTags(ItemStack.of(nbt), arrow))
                .filter(nbt -> {
                    ItemStack item = ItemStack.of(nbt);

                    return item.getCount() < item.getMaxStackSize();
                })
                .toList();

        int amount = 0;

        if (!entries.isEmpty()) {
            for (CompoundTag entry : entries) {
                ItemStack s = ItemStack.of(entry);

                int count = s.getCount() + arrow.getCount();

                if (count <= s.getMaxStackSize()) {
                    amount += arrow.getCount();

                    arrow.setCount(0);

                    s.grow(amount);
                    s.save(entry);

                    list.remove(entry);
                    list.add(0, entry);

                    break;
                } else {
                    int step = s.getMaxStackSize() - s.getCount();

                    amount += step;

                    arrow.shrink(step);

                    s.grow(step);
                    s.save(entry);

                    list.remove(entry);
                    list.add(0, entry);
                }
            }
        }

        if (!arrow.isEmpty()) {
            if (getSlotsAmount(stack) <= getArrows(stack).size())
                return 0;

            amount += arrow.getCount();

            CompoundTag entry = new CompoundTag();

            arrow.copy().save(entry);

            list.add(0, entry);
        }

        return amount;
    }

    public static Optional<ItemStack> takeStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(TAG_ARROWS))
            return Optional.empty();

        ListTag list = tag.getList(TAG_ARROWS, 10);

        if (list.isEmpty())
            return Optional.empty();

        ItemStack s = ItemStack.of(list.getCompound(0));

        list.remove(0);

        if (list.isEmpty())
            stack.removeTagKey(TAG_ARROWS);

        return Optional.of(s);
    }

    public static void takeArrow(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(TAG_ARROWS))
            return;

        ListTag list = tag.getList(TAG_ARROWS, 10);

        if (list.isEmpty())
            return;

        CompoundTag entry = list.getCompound(0);

        ItemStack s = ItemStack.of(entry);

        s.shrink(1);
        s.save(entry);

        list.remove(0);

        if (!s.isEmpty())
            list.add(0, entry);

        if (list.isEmpty())
            stack.removeTagKey(TAG_ARROWS);
    }

    public record ArrowQuiverTooltip(@Getter List<ItemStack> items, @Getter int maxAmount) implements TooltipComponent {

    }

    @OnlyIn(Dist.CLIENT)
    public record ClientArrowQuiverTooltip(@Getter ArrowQuiverTooltip tooltip) implements ClientTooltipComponent {
        public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/tooltip/arrow_quiver/empty_arrow.png");

        @Override
        public int getHeight() {
            return 26;
        }

        @Override
        public int getWidth(Font font) {
            return tooltip.getMaxAmount() * 11;
        }

        @Override
        public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics guiGraphics) {
            Minecraft MC = Minecraft.getInstance();
            PoseStack poseStack = guiGraphics.pose();

            poseStack.pushPose();

            poseStack.translate(0, 0, 410);

            int step = 0;

            for (ItemStack stack : tooltip.getItems()) {
                guiGraphics.renderItem(stack, mouseX + step, mouseY);

                poseStack.scale(0.5F, 0.5F, 0.5F);

                guiGraphics.drawString(MC.font, String.valueOf(stack.getCount()), ((mouseX + step) * 2) + ((16 - font.width(String.valueOf(stack.getCount()))) / 2), (mouseY + 16) * 2, 0xFFFFFF);

                poseStack.scale(2F, 2F, 2F);

                step += 10;
            }

            for (int i = step / 10; i < tooltip.getMaxAmount(); i++) {
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                RenderSystem.setShaderTexture(0, TEXTURE);

                Minecraft.getInstance().getTextureManager().getTexture(TEXTURE).bind();

                guiGraphics.blit(TEXTURE, mouseX + step, mouseY, 16, 16, 0, 0, 16, 16, 16, 16);

                step += 10;
            }

            poseStack.translate(0, 0, -410);

            poseStack.popPose();
        }
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onSlotClick(ContainerSlotClickEvent event) {
            Player player = event.getEntity();

            ItemStack heldStack = event.getHeldStack();
            ItemStack slotStack = event.getSlotStack();

            if (slotStack.getItem() != ItemRegistry.ARROW_QUIVER.get()
                    || !AbilityUtils.canUseAbility(slotStack, "receptacle"))
                return;

            if (event.getAction() == ClickAction.PRIMARY) {
                if (!(heldStack.getItem() instanceof ArrowItem))
                    return;

                int amount = insertStack(slotStack, heldStack);

                if (amount <= 0)
                    return;

                heldStack.shrink(amount);

                player.playSound(SoundEvents.BUNDLE_INSERT, 1F, 1F);

                event.setCanceled(true);
            } else {
                if (!heldStack.isEmpty())
                    return;

                takeStack(slotStack).ifPresent((stack) -> {
                    event.getContainer().setCarried(stack);

                    player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 1F, 1F);

                    event.setCanceled(true);
                });
            }
        }

        @SubscribeEvent
        public static void onArrowLoose(ArrowLooseEvent event) {
            Player player = event.getEntity();
            ItemStack relic = EntityUtils.findEquippedCurio(player, ItemRegistry.ARROW_QUIVER.get());

            if (relic.isEmpty() || !AbilityUtils.canUseAbility(relic, "receptacle")
                    || getArrows(relic).isEmpty() || player.isCreative())
                return;

            takeArrow(relic);
        }

        @SubscribeEvent
        public static void onGettingProjectile(LivingGetProjectileEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack weapon = event.getProjectileWeaponItemStack();

            if (!(weapon.getItem() instanceof BowItem) || (weapon.getItem() instanceof CrossbowItem))
                return;

            ItemStack relic = EntityUtils.findEquippedCurio(player, ItemRegistry.ARROW_QUIVER.get());

            if (relic.isEmpty() || !AbilityUtils.canUseAbility(relic, "receptacle"))
                return;

            List<ItemStack> arrows = getArrows(relic);

            if (!arrows.isEmpty())
                event.setProjectileItemStack(arrows.get(0));
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)
                    || !(arrow.getOwner() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ARROW_QUIVER.get());

            if (stack.isEmpty())
                return;

            if (AbilityUtils.canUseAbility(stack, "receptacle")) {
                int amount = (int) Math.min(10, Math.round(player.position().distanceTo(new Vec3(arrow.getX(), player.getY(), arrow.getZ())) * 0.1));

                if (amount > 0)
                    LevelingUtils.addExperience(player, stack, amount);
            }

            if (AbilityUtils.canUseAbility(stack, "leap")) {
                if (arrow.getPersistentData().contains("arrow_quiver_multiplier"))
                    event.setAmount((float) (event.getAmount() + (event.getAmount() * AbilityUtils.getAbilityValue(stack, "leap", "multiplier"))));
            }
        }

        @SubscribeEvent
        public static void onEntitySpawned(EntityJoinLevelEvent event) {
            if (event.getLevel().isClientSide()
                    || !(event.getEntity() instanceof AbstractArrow arrow)
                    || !(arrow.getOwner() instanceof Player player)
                    || arrow.position().distanceTo(player.position()) > 16
                    || arrow.life > 0)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ARROW_QUIVER.get());

            if (stack.isEmpty() || !AbilityUtils.canUseAbility(stack, "leap")
                    || NBTUtils.getInt(stack, TAG_LEAP, -1) < 0)
                return;

            NBTUtils.clearTag(stack, TAG_LEAP);

            if (!arrow.isCritArrow())
                return;

            Level level = player.getCommandSenderWorld();

            level.playSound(null, player.blockPosition(), SoundRegistry.POWERED_ARROW.get(), SoundSource.MASTER, 1F, 1F + player.getRandom().nextFloat() * 0.5F);

            arrow.getPersistentData().putBoolean("arrow_quiver_multiplier", true);
            arrow.setNoGravity(true);

            Vec3 motion = player.getLookAngle().scale(-1).normalize();

            if (!level.isClientSide())
                NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayer) player);
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (!(event.getRayTraceResult() instanceof EntityHitResult result)
                    || !(result.getEntity() instanceof LivingEntity entity)
                    || !(event.getEntity() instanceof AbstractArrow arrow)
                    || !(arrow.getOwner() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.ARROW_QUIVER.get());

            if (stack.isEmpty() || !AbilityUtils.canUseAbility(stack, "leap"))
                return;

            CompoundTag data = arrow.getPersistentData();

            if (data.contains("arrow_quiver_multiplier")) {
                if (EntityUtils.isAlliedTo(player, entity))
                    event.setCanceled(true);
                else
                    data.remove("arrow_quiver_multiplier");
            }
        }
    }
}