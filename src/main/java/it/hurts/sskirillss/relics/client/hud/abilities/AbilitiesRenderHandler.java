package it.hurts.sskirillss.relics.client.hud.abilities;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.init.HotkeyRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.abilities.SpellCastPacket;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RenderUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(value = Dist.CLIENT)
public class AbilitiesRenderHandler {
    private static final Minecraft MC = Minecraft.getInstance();

    private static List<AbilityEntry> entries = new ArrayList<>();

    private static int selectedIndex = 0;

    private static boolean animationDown = false;
    private static int animationDelta = 0;

    private static int mouseDelta = 0;

    public static void render(PoseStack poseStack, float partialTicks) {
        if (animationDelta == 0)
            return;

        Window window = MC.getWindow();

        LocalPlayer player = MC.player;

        if (player == null || entries.isEmpty())
            return;

        int x = (window.getGuiScaledWidth()) / 2;
        int y = -40;

        poseStack.pushPose();

        RenderSystem.setShaderColor(1F, 1F, 1F, animationDelta * 0.2F);

        poseStack.translate(0, (animationDelta - (animationDelta != 5 ? partialTicks * (animationDown ? -1 : 1) : 0)) * 16, 0);

        drawAbility(poseStack, player, -2, x - 65, y, partialTicks);
        drawAbility(poseStack, player, -1, x - 34, y, partialTicks);
        drawAbility(poseStack, player, 0, x, y, partialTicks);
        drawAbility(poseStack, player, 1, x + 34, y, partialTicks);
        drawAbility(poseStack, player, 2, x + 65, y, partialTicks);

        RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/background.png"));

        RenderSystem.enableBlend();

        RenderUtils.renderTextureFromCenter(poseStack, x - 95, y, 2, 2, 256, 256, 18, 29, 1F + (mouseDelta < 0 ? Math.abs(mouseDelta) * 0.01F : 0));
        if (mouseDelta < 0)
            RenderUtils.renderTextureFromCenter(poseStack, x - 95, y, 25, 1, 256, 256, 24, 35, 1F + Math.abs(mouseDelta) * 0.01F);

        RenderUtils.renderTextureFromCenter(poseStack, x + 95, y, 2, 38, 256, 256, 18, 29, 1F + (mouseDelta > 0 ? Math.abs(mouseDelta) * 0.01F : 0));
        if (mouseDelta > 0)
            RenderUtils.renderTextureFromCenter(poseStack, x + 95, y, 25, 37, 256, 256, 24, 35, 1F + Math.abs(mouseDelta) * 0.01F);

        RenderUtils.renderTextureFromCenter(poseStack, x - 1, y - 20, 53, 2, 256, 256, 6, 11, 1F);

        RenderSystem.disableBlend();

        AbilityEntry selectedAbility = getAbilityByIndex(selectedIndex);
        ItemStack stack = AbilityUtils.getStackInCuriosSlot(player, selectedAbility.getSlot());

        MutableComponent name = Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + selectedAbility.getAbility());

        MC.font.drawShadow(poseStack, name, x - MC.font.width(name) / 2F, y - 35, 0xFFFFFF);

        poseStack.popPose();
    }

    private static void drawAbility(PoseStack poseStack, LocalPlayer player, int realIndex, float x, float y, float partialTicks) {
        AbilityEntry ability = getAbilityByIndex(getRelativeIndex(realIndex));

        if (ability == null)
            return;

        RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/cards/" + ForgeRegistries.ITEMS.getKey(AbilityUtils.getStackInCuriosSlot(player, ability.getSlot()).getItem()).getPath() + "/" + ability.getAbility() + ".png"));

        RenderSystem.enableBlend();

        poseStack.pushPose();

        int width = 20;
        int height = 29;

        float scale = (float) ((1F + Mth.clamp(Math.pow(13.5F, -Math.abs(realIndex)), 0, 0.2F)) + (realIndex == 0 ? (Math.sin((player.tickCount + partialTicks) * 0.1F) * 0.05F) : 0));

        RenderUtils.renderTextureFromCenter(poseStack, x - (1 * scale), y - (1 * scale), width, height, scale);

        RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/background.png"));

        width = 28;
        height = 37;

        RenderUtils.renderTextureFromCenter(poseStack, x, y, 66, 2, 256, 256, width, height, scale);

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

        return entries.get(Mth.clamp(index, 0, entries.size()));
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
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class Events {
        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
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

            if (player == null)
                return;

            if (mouseDelta > 0)
                mouseDelta--;
            else if (mouseDelta < 0)
                mouseDelta++;

            if (HotkeyRegistry.ABILITY_LIST.isDown()) {
                AbilityEntry ability = getAbilityByIndex(selectedIndex);

                if (ability != null) {
                    ItemStack stack = AbilityUtils.getStackInCuriosSlot(player, ability.getSlot());

                    if (stack.getItem() instanceof RelicItem relic)
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

            entries = AbilityUtils.getActiveEntries(player);

            if (selectedIndex > entries.size() || selectedIndex < 0)
                selectedIndex = 0;
        }

        @SubscribeEvent
        public static void onKeyPressed(InputEvent.MouseButton.Pre event) {
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

            ItemStack stack = AbilityUtils.getStackInCuriosSlot(player, ability.getSlot());

            if (!(stack.getItem() instanceof RelicItem relic))
                return;

            NetworkHandler.sendToServer(new SpellCastPacket(ability.getAbility(), ability.getSlot()));

            relic.castActiveAbility(stack, player, ability.getAbility());

            event.setCanceled(true);
        }
    }
}