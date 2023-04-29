package it.hurts.sskirillss.relics.items.relics.back;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.api.events.common.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.entities.ArrowRainEntity;
import it.hurts.sskirillss.relics.init.EntityRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.utils.*;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    private static final String TAG_ARROWS = "arrows";
    private static final String TAG_COOLDOWN = "cooldown";

    @Override
    public RelicData getRelicData() {
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
                        .ability("agility", RelicAbilityEntry.builder()
                                .requiredLevel(5)
                                .requiredPoints(2)
                                .maxLevel(5)
                                .stat("modifier", RelicAbilityStat.builder()
                                        .initialValue(1, 1)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1)
                                        .formatValue(value -> (int) ((1 + MathUtils.round(value, 0))) * 100)
                                        .build())
                                .build())
                        .ability("rain", RelicAbilityEntry.builder()
                                .requiredLevel(10)
                                .maxLevel(10)
                                .active(true)
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
    public void castActiveAbility(ItemStack stack, Player player, String ability) {
        Level level = player.getCommandSenderWorld();

        if (ability.equals("rain")) {
            if (NBTUtils.getInt(stack, TAG_COOLDOWN, 0) > 0
                    || getArrows(stack).isEmpty())
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

            NBTUtils.setInt(stack, TAG_COOLDOWN, duration / 20);
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
        if (!(livingEntity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        if (AbilityUtils.canUseAbility(stack, "agility") && player.isUsingItem() && player.getMainHandItem().getItem() instanceof BowItem)
            for (int i = 0; i < 1; i++)
                player.updatingUsingItem();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        int cooldown = NBTUtils.getInt(stack, TAG_COOLDOWN, 0);

        if (AbilityUtils.canUseAbility(stack, "rain")) {
            if (cooldown > 0 && entity.tickCount % 20 == 0)
                NBTUtils.setInt(stack, TAG_COOLDOWN, --cooldown);
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
        public void renderImage(Font font, int mouseX, int mouseY, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset) {
            poseStack.pushPose();

            poseStack.translate(0, 0, 410);
            poseStack.scale(0.5F, 0.5F, 0.5F);

            int step = 0;

            for (ItemStack stack : tooltip.getItems()) {
                font.draw(poseStack, String.valueOf(stack.getCount()), ((mouseX + step) * 2) + ((16 - font.width(String.valueOf(stack.getCount()))) / 2F), (mouseY + 16) * 2, 0xFFFFFF);

                itemRenderer.renderAndDecorateFakeItem(stack, mouseX + step, mouseY);

                step += 10;
            }

            poseStack.scale(2F, 2F, 2F);

            for (int i = step / 10; i < tooltip.getMaxAmount(); i++) {
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                RenderSystem.setShaderTexture(0, TEXTURE);

                Minecraft.getInstance().getTextureManager().getTexture(TEXTURE).bind();

                Gui.blit(poseStack, mouseX + step, mouseY, 16, 16, 0, 0, 16, 16, 16, 16);

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

            if (slotStack.getItem() != ItemRegistry.ARROW_QUIVER.get())
                return;

            ArrowQuiverItem quiver = (ArrowQuiverItem) slotStack.getItem();

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

            if (relic.isEmpty() || DurabilityUtils.isBroken(relic)
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

            if (relic.isEmpty() || DurabilityUtils.isBroken(relic))
                return;

            List<ItemStack> arrows = getArrows(relic);

            if (!arrows.isEmpty())
                event.setProjectileItemStack(arrows.get(0));
        }

        @SubscribeEvent
        public static void onProjectileImpact(LivingHurtEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)
                    || !(event.getSource().getDirectEntity() instanceof AbstractArrow arrow))
                return;

            ArrowQuiverItem item = (ArrowQuiverItem) ItemRegistry.ARROW_QUIVER.get();
            ItemStack stack = EntityUtils.findEquippedCurio(player, item);

            if (stack.isEmpty() || DurabilityUtils.isBroken(stack))
                return;

            int amount = (int) Math.min(10, Math.round(player.position().distanceTo(new Vec3(arrow.getX(), player.getY(), arrow.getZ())) * 0.1));

            if (amount > 0)
                LevelingUtils.addExperience(player, stack, amount);
        }
    }
}