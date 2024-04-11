package it.hurts.sskirillss.relics.client.hud.abilities;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import it.hurts.sskirillss.relics.init.HotkeyRegistry;
import it.hurts.sskirillss.relics.init.SoundRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.predicate.PredicateEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.predicate.misc.PredicateData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.abilities.SpellCastPacket;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import it.hurts.sskirillss.relics.utils.data.AnimationData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(value = Dist.CLIENT)
public class AbilitiesRenderHandler {
    private static final Minecraft MC = Minecraft.getInstance();

    private static List<AbilityEntry> entries = new ArrayList<>();
    private final static Map<String, AbilityCache> cache = new HashMap<>();

    private static int selectedIndex = 0;

    private static boolean animationDown = false;
    private static int animationDelta = 0;

    private static int castShakeDelta = 0;

    private static int mouseDelta = 0;

    private static void updateCaches(Player player) {
        if (animationDelta == 0)
            return;

        entries = ActiveAbilityUtils.getActiveEntries(player);

        for (AbilityEntry entry : entries) {
            cache.putIfAbsent(entry.getAbility(), new AbilityCache());

            AnimationCache animationCache = entry.getCache().getAnimation();

            if (animationCache.iconShakeDelta > 0)
                animationCache.iconShakeDelta--;

            String abilityName = entry.ability;

            ItemStack stack = ActiveAbilityUtils.getStackInCuriosSlot(player, entry.slot);

            if (!(stack.getItem() instanceof IRelicItem relic))
                continue;

            CastPredicate predicate = relic.getAbilityCastPredicates(abilityName);

            if (predicate == null)
                continue;

            for (Map.Entry<String, PredicateEntry> data : predicate.getPredicates().entrySet()) {
                entry.getCache().predicate.info.put(data.getKey(), data.getValue().getPredicate().apply(new PredicateData(player, stack)));
            }
        }
    }

    public static void render(PoseStack poseStack, float partialTicks) {
        if (animationDelta == 0)
            return;

        Window window = MC.getWindow();

        LocalPlayer player = MC.player;

        if (player == null || entries.isEmpty())
            return;

        int x = (window.getGuiScaledWidth()) / 2;
        int y = -38;

        poseStack.pushPose();

        poseStack.translate(0, (animationDelta - (animationDelta != 5 ? partialTicks * (animationDown ? -1 : 1) : 0)) * 16, 0);

        float shakeOffset = castShakeDelta > 0 ? ((castShakeDelta - partialTicks) * 0.25F) : 0;

        drawAbility(poseStack, player, -2, x - 70 - shakeOffset, y, partialTicks);
        drawAbility(poseStack, player, -1, x - 37 - shakeOffset, y, partialTicks);
        drawAbility(poseStack, player, 0, x, y, partialTicks);
        drawAbility(poseStack, player, 1, x + 37 + shakeOffset, y, partialTicks);
        drawAbility(poseStack, player, 2, x + 70 + shakeOffset, y, partialTicks);

        RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/background.png"));

        RenderSystem.enableBlend();

        RenderUtils.renderTextureFromCenter(poseStack, x - 96 - shakeOffset, y + 2, 43, 2, 256, 256, 11, 30, 1F + (mouseDelta < 0 ? Math.abs(mouseDelta) * 0.01F : 0));
        if (mouseDelta < 0)
            RenderUtils.renderTextureFromCenter(poseStack, x - 96 - shakeOffset, y + 2, 72, 0, 256, 256, 15, 34, 1F + Math.abs(mouseDelta) * 0.01F);

        RenderUtils.renderTextureFromCenter(poseStack, x + 96 + shakeOffset, y + 2, 31, 2, 256, 256, 11, 30, 1F + (mouseDelta > 0 ? Math.abs(mouseDelta) * 0.01F : 0));
        if (mouseDelta > 0)
            RenderUtils.renderTextureFromCenter(poseStack, x + 96 + shakeOffset, y + 2, 56, 0, 256, 256, 15, 34, 1F + Math.abs(mouseDelta) * 0.01F);

        RenderSystem.disableBlend();

        AbilityEntry selectedAbility = getAbilityByIndex(selectedIndex);
        ItemStack stack = ActiveAbilityUtils.getStackInCuriosSlot(player, selectedAbility.getSlot());

        String registryName = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();

        MutableComponent name = new TranslatableComponent("tooltip.relics." + registryName + ".ability." + selectedAbility.getAbility());

        MC.font.drawShadow(poseStack, name, x - MC.font.width(name) / 2F, y - 38, 0xFFFFFF);

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate((animationDelta - (animationDelta != 5 ? partialTicks * (animationDown ? -1 : 1) : 0)) * 16, 0, 0);

        int yOff = 0;

        x = -70;
        y = 25;

        for (Map.Entry<String, Boolean> entry : cache.get(selectedAbility.getAbility()).predicate.info.entrySet()) {
            String predicateName = entry.getKey();
            boolean isCompleted = entry.getValue();

            RenderSystem.setShaderTexture(0, isCompleted ? new ResourceLocation(Reference.MODID, "textures/gui/description/icons/completed.png") : new ResourceLocation(Reference.MODID, "textures/gui/description/icons/" + registryName + "/" + predicateName + ".png"));

            RenderUtils.renderTextureFromCenter(poseStack, x, y + yOff, 0, 0, 16, 16, 16, 16, 0.5F);

            poseStack.scale(0.5F, 0.5F, 0.5F);

            MC.font.drawShadow(poseStack, new TranslatableComponent("tooltip.relics." + registryName + ".ability." + selectedAbility.ability + ".predicate." + predicateName).withStyle(isCompleted ? ChatFormatting.STRIKETHROUGH : ChatFormatting.RESET), (x + 7) * 2, (y - 2 + yOff) * 2, isCompleted ? 0xbeffb8 : 0xf17f9c);

            poseStack.scale(2F, 2F, 2F);

            yOff += 10;
        }

        poseStack.popPose();
    }

    private static void drawAbility(PoseStack poseStack, LocalPlayer player, int realIndex, float x, float y, float partialTicks) {
        AbilityEntry ability = getAbilityByIndex(getRelativeIndex(realIndex));

        if (ability == null)
            return;

        ItemStack stack = ActiveAbilityUtils.getStackInCuriosSlot(player, ability.getSlot());

        if (!(stack.getItem() instanceof IRelicItem relic))
            return;

        boolean isLocked = !relic.canPlayerUseActiveAbility(player, stack, ability.getAbility());

        ResourceLocation card = new ResourceLocation(Reference.MODID, "textures/gui/description/cards/" + ForgeRegistries.ITEMS.getKey(ActiveAbilityUtils.getStackInCuriosSlot(player, ability.getSlot()).getItem()).getPath() + "/" + relic.getAbilityData(ability.getAbility()).getIcon().apply(player, stack, ability.getAbility()) + ".png");

        RenderSystem.setShaderTexture(0, card);

        RenderSystem.enableBlend();

        poseStack.pushPose();

        int width = 20;
        int height = 29;

        float scale = (float) ((1F + Mth.clamp(Math.pow(13.5F, -Math.abs(realIndex)), 0F, 0.2F)) + (realIndex == 0 ? (Math.sin((player.tickCount + partialTicks) * 0.1F) * 0.05F + (castShakeDelta > 0 ? ((castShakeDelta - partialTicks) * 0.02F) : 0F)) : 0F));

        RenderUtils.renderTextureFromCenter(poseStack, x - scale, y - scale + 2, width, height, scale + 0.025F);

        int cooldown = relic.getAbilityCooldown(stack, ability.getAbility());
        int cap = relic.getAbilityCooldownCap(stack, ability.getAbility());

        String iconDescription = "";

        if (cooldown > 0) {
            RenderSystem.setShaderTexture(0, card);

            RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1F);

            float percentage = cooldown / (cap / 100F) / 100F;

            RenderUtils.renderTextureFromCenter(poseStack, x - scale, ((y + 2) - scale + (height * scale) / 2F) - (height * scale / 2F) * percentage, 0, height - height * percentage, width, height, width, height * percentage, scale + 0.025F);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }

        RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/background.png"));

        RenderUtils.renderTextureFromCenter(poseStack, x, y, 0, isLocked ? 43 : 0, 256, 256, 30, 42, scale);

        if (relic.isAbilityTicking(stack, ability.getAbility())) {
            CastType type = relic.getAbilityData(ability.getAbility()).getCastData().getKey();

            if (type == CastType.TOGGLEABLE) {
                RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/widgets/toggleable.png"));

                RenderSystem.enableBlend();

                RenderUtils.renderAnimatedTextureFromCenter(poseStack, x - 0.5F, y - 0.5F, 31, 473, 31, 43, scale, AnimationData.builder()
                        .frame(0, 1).frame(1, 1).frame(2, 1)
                        .frame(3, 1).frame(4, 1).frame(5, 1)
                        .frame(6, 1).frame(7, 1).frame(8, 1)
                        .frame(9, 1).frame(10, 1));

                RenderSystem.disableBlend();
            } else {
                RenderSystem.setShaderColor(1F, 1F, 1F, (float) ((Math.sin(player.tickCount * 0.25F) * 0.25F) + 0.75F));

                RenderSystem.enableBlend();

                RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/widgets/cyclical.png"));

                RenderUtils.renderTextureFromCenter(poseStack, x - scale / 2F, y - scale / 2F, 31, 43, scale);

                RenderSystem.disableBlend();

                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            }
        }

        if (realIndex == 0) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/background.png"));

            RenderUtils.renderTextureFromCenter(poseStack, x - 1, y - 21, isLocked ? 38 : 31, 33, 256, 256, 6, 11, scale);
        }

        if (cooldown > 0) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/widgets/icons/cooldown.png"));

            RenderSystem.enableBlend();

            drawAbilityStatusIcon(ability, poseStack, x - scale, y - scale, 20, 300, scale - 0.1F, AnimationData.builder()
                            .frame(0, 2).frame(1, 2).frame(2, 2)
                            .frame(3, 2).frame(4, 2).frame(5, 2)
                            .frame(6, 2).frame(7, 2).frame(8, 2)
                            .frame(9, 2).frame(10, 8).frame(11, 2)
                            .frame(12, 2).frame(13, 2).frame(14, 2),
                    cap - cooldown, partialTicks);

            RenderSystem.disableBlend();

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            iconDescription = String.valueOf(MathUtils.round(cooldown / 20D, 1));
        } else {
            PredicateCache predicateCache = ability.getCache().predicate;
            Collection<Boolean> infoEntries = predicateCache.info.values();

            int successPredicates = 0;

            for (boolean value : infoEntries) {
                if (value)
                    successPredicates++;
            }

            int failedPredicates = infoEntries.size() - successPredicates;

            if (failedPredicates > 0) {
                RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/widgets/icons/locked.png"));

                RenderSystem.enableBlend();

                drawAbilityStatusIcon(ability, poseStack, x - scale, y - scale, 20, 20, scale - 0.1F, null, player.tickCount, partialTicks);

                RenderSystem.disableBlend();

                iconDescription = successPredicates + "/" + infoEntries.size();
            }
        }

        if (!iconDescription.isEmpty()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);

            MC.font.drawShadow(poseStack, iconDescription, (x - 1) * 2F - (MC.font.width(iconDescription) / 2F), (y - 6 + scale * 15) * 2F, 0xFFFFFF);

            poseStack.scale(2F, 2F, 2F);
        }

        poseStack.popPose();
    }

    private static void drawAbilityStatusIcon(AbilityEntry ability, PoseStack poseStack, float x, float y, float texWidth, float texHeight, float scale, @Nullable AnimationData animation, long ticks, float partialTicks) {
        poseStack.pushPose();

        poseStack.translate(x, y, 0);

        AnimationCache animationCache = ability.getCache().getAnimation();

        if (animationCache.iconShakeDelta != 0) {
            float color = animationCache.iconShakeDelta * 0.04F;

            RenderSystem.setShaderColor(1, 1 - color, 1 - color, 1);

            poseStack.mulPose(Vector3f.ZP.rotation((float) Math.sin((ticks + partialTicks) * 0.75F) * 0.1F));

            scale += (animationCache.iconShakeDelta - partialTicks) * 0.025F;
        }

        if (animation != null)
            RenderUtils.renderAnimatedTextureFromCenter(poseStack, 0, 0, texWidth, texHeight, texWidth, texWidth, scale, animation, ticks);
        else
            RenderUtils.renderTextureFromCenter(poseStack, 0, 0, texWidth, texHeight, scale);

        RenderSystem.setShaderColor(1, 1, 1, 1);

        poseStack.popPose();
    }

    private static int getRelativeIndex(int offset) {
        int current = selectedIndex;
        int sum = current + offset;
        int max = entries.size() - 1;

        return sum > max ? Math.min(max, sum - (max + 1)) : sum < 0 ? Math.max(0, sum + (max + 1)) : sum;
    }

    @Nullable
    private static AbilityEntry getAbilityByIndex(int index) {
        if (entries.isEmpty())
            return null;

        return entries.get(Mth.clamp(index, 0, entries.size() - 1));
    }

    private static void applyDelta(int delta) {
        int current = selectedIndex;
        int sum = current + delta;
        int max = entries.size() - 1;

        selectedIndex = sum > max ? sum - max - 1 : sum < 0 ? max : sum;
    }

    @Data
    @AllArgsConstructor
    public static class AbilityEntry {
        private int slot;

        private String ability;

        public AbilityCache getCache() {
            return cache.get(ability);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AbilityCache {
        private AnimationCache animation = new AnimationCache();
        private PredicateCache predicate = new PredicateCache();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnimationCache {
        private int iconShakeDelta = 0;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PredicateCache {
        private Map<String, Boolean> info = new HashMap<>();
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class GeneralEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
            if (!HotkeyRegistry.ABILITY_LIST.isDown() || entries.isEmpty())
                return;

            int current = selectedIndex;

            applyDelta(event.getScrollDelta() > 0 ? -1 : 1);

            if (current != selectedIndex) {
                mouseDelta = event.getScrollDelta() > 0 ? -10 : 10;

                LocalPlayer player = Minecraft.getInstance().player;

                if (player != null)
                    player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5F, 1.5F + player.getRandom().nextFloat() * 0.25F);
            }

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.side != LogicalSide.CLIENT || event.phase != TickEvent.Phase.END)
                return;

            Player player = event.player;

            if (!(player instanceof LocalPlayer))
                return;

            if (castShakeDelta > 0)
                castShakeDelta--;

            if (mouseDelta > 0)
                mouseDelta--;
            else if (mouseDelta < 0)
                mouseDelta++;

            if (HotkeyRegistry.ABILITY_LIST.isDown()) {
                AbilityEntry ability = getAbilityByIndex(selectedIndex);

                if (ability != null) {
                    ItemStack stack = ActiveAbilityUtils.getStackInCuriosSlot(player, ability.getSlot());

                    if (stack.getItem() instanceof IRelicItem relic && relic.getAbilityData(ability.getAbility()) != null && relic.canPlayerUseActiveAbility(player, stack, ability.getAbility()))
                        relic.tickActiveAbilitySelection(stack, player, ability.getAbility());
                }

                if (animationDelta < 5)
                    animationDelta++;

                animationDown = true;
            } else {
                if (animationDelta > 0)
                    animationDelta--;

                animationDown = false;
            }

            if (animationDelta == 0)
                return;

            updateCaches(player);

            if (selectedIndex > entries.size() || selectedIndex < 0)
                selectedIndex = 0;
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class CastEvents {
        @SubscribeEvent
        public static void onKeyPressed(InputEvent.MouseInputEvent event) {
            if (animationDelta == 0 || event.getAction() != InputConstants.PRESS
                    || event.getButton() != GLFW.GLFW_MOUSE_BUTTON_1)
                return;

            Minecraft MC = Minecraft.getInstance();

            if (MC.screen != null)
                return;

            Player player = MC.player;

            if (player == null)
                return;

            AbilityEntry ability = getAbilityByIndex(selectedIndex);

            if (ability == null)
                return;

            ItemStack stack = ActiveAbilityUtils.getStackInCuriosSlot(player, ability.getSlot());

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;

            if (!relic.canPlayerUseActiveAbility(player, stack, ability.getAbility())) {
                int delta = ability.getCache().getAnimation().iconShakeDelta;

                ability.getCache().getAnimation().setIconShakeDelta(Math.min(20, delta + (delta > 0 ? 5 : 15)));

                MC.getSoundManager().play(SimpleSoundInstance.forUI(relic.isAbilityOnCooldown(stack, ability.getAbility())
                        ? SoundRegistry.ABILITY_COOLDOWN.get() : SoundRegistry.ABILITY_LOCKED.get(), 1F));

                event.setCanceled(true);

                return;
            }

            boolean isTicking = relic.isAbilityTicking(stack, ability.getAbility());

            CastType type = relic.getAbilityData(ability.getAbility()).getCastData().getKey();

            MC.getSoundManager().play(SimpleSoundInstance.forUI(SoundRegistry.ABILITY_CAST.get(), 1F));

            switch (type) {
                case INSTANTANEOUS -> {
                    NetworkHandler.sendToServer(new SpellCastPacket(CastType.INSTANTANEOUS, CastStage.END, ability.getAbility(), ability.getSlot()));

                    relic.castActiveAbility(stack, player, ability.getAbility(), type, CastStage.END);
                }
                case CYCLICAL -> {
                    NetworkHandler.sendToServer(new SpellCastPacket(CastType.CYCLICAL, CastStage.START, ability.getAbility(), ability.getSlot()));

                    relic.castActiveAbility(stack, player, ability.getAbility(), type, CastStage.START);
                }
                case INTERRUPTIBLE -> {
                    CastStage stage = isTicking ? CastStage.END : CastStage.START;

                    NetworkHandler.sendToServer(new SpellCastPacket(CastType.INTERRUPTIBLE, stage, ability.getAbility(), ability.getSlot()));

                    relic.castActiveAbility(stack, player, ability.getAbility(), type, stage);
                }
                case TOGGLEABLE -> {
                    CastStage stage = isTicking ? CastStage.END : CastStage.START;

                    NetworkHandler.sendToServer(new SpellCastPacket(CastType.TOGGLEABLE, stage, ability.getAbility(), ability.getSlot()));

                    relic.castActiveAbility(stack, player, ability.getAbility(), type, stage);
                }
            }

            castShakeDelta = 10;

            Minecraft.getInstance().mouseHandler.releaseMouse();
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.side != LogicalSide.CLIENT || event.phase != TickEvent.Phase.END)
                return;

            Player player = event.player;

            if (player == null)
                return;

            AbilityEntry ability = getAbilityByIndex(selectedIndex);

            if (ability == null)
                return;

            ItemStack stack = ActiveAbilityUtils.getStackInCuriosSlot(player, ability.getSlot());

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;

            boolean isTicking = relic.isAbilityTicking(stack, ability.getAbility());
            boolean isCasting = Minecraft.getInstance().mouseHandler.isLeftPressed();

            AbilityData entry = relic.getAbilityData(ability.getAbility());

            if (entry == null)
                return;

            CastType type = entry.getCastData().getKey();

            switch (type) {
                case CYCLICAL -> {
                    if (isTicking) {
                        if (isCasting) {
                            NetworkHandler.sendToServer(new SpellCastPacket(CastType.CYCLICAL, CastStage.TICK, ability.getAbility(), ability.getSlot()));

                            relic.castActiveAbility(stack, player, ability.getAbility(), type, CastStage.TICK);
                        } else {
                            NetworkHandler.sendToServer(new SpellCastPacket(CastType.CYCLICAL, CastStage.END, ability.getAbility(), ability.getSlot()));

                            relic.castActiveAbility(stack, player, ability.getAbility(), type, CastStage.END);
                        }
                    }
                }
                case INTERRUPTIBLE -> {
                    if (isTicking) {
                        NetworkHandler.sendToServer(new SpellCastPacket(CastType.INTERRUPTIBLE, CastStage.TICK, ability.getAbility(), ability.getSlot()));

                        relic.castActiveAbility(stack, player, ability.getAbility(), type, CastStage.TICK);
                    }
                }
                case TOGGLEABLE -> {
                    if (isTicking) {
                        NetworkHandler.sendToServer(new SpellCastPacket(CastType.TOGGLEABLE, CastStage.TICK, ability.getAbility(), ability.getSlot()));

                        relic.castActiveAbility(stack, player, ability.getAbility(), type, CastStage.TICK);
                    }
                }
            }
        }
    }
}