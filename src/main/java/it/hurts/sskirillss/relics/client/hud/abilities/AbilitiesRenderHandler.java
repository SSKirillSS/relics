package it.hurts.sskirillss.relics.client.hud.abilities;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.init.HotkeyRegistry;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(value = Dist.CLIENT)
public class AbilitiesRenderHandler {
    private static final Minecraft MC = Minecraft.getInstance();

    private static List<AbilityEntry> entries = new ArrayList<>();

    private static int selectedIndex = 0;

    public static void render(PoseStack poseStack, float partialTicks) {
        if (!HotkeyRegistry.ABILITY_LIST.isDown())
            return;

        Window window = MC.getWindow();

        LocalPlayer player = MC.player;

        if (player == null || entries.isEmpty())
            return;

        int x = (window.getGuiScaledWidth()) / 2;
        int y = 36;

        drawAbility(poseStack, player, -2, x - 65, y, partialTicks);
        drawAbility(poseStack, player, -1, x - 34, y, partialTicks);
        drawAbility(poseStack, player, 0, x, y, partialTicks);
        drawAbility(poseStack, player, 1, x + 34, y, partialTicks);
        drawAbility(poseStack, player, 2, x + 65, y, partialTicks);

        AbilityEntry selectedAbility = getAbilityByIndex(selectedIndex);
        ItemStack stack = AbilityUtils.getStackInCuriosSlot(player, selectedAbility.getSlot());

        MutableComponent name = Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + ".ability." + selectedAbility.getAbility());

        MC.font.drawShadow(poseStack, name, x - MC.font.width(name) / 2F, y - 33, 0xFFFFFF);
    }

    private static void drawAbility(PoseStack poseStack, LocalPlayer player, int realIndex, float x, float y, float partialTicks) {
        AbilityEntry ability = getAbilityByIndex(getRelativeIndex(realIndex));

        if (ability == null)
            return;

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/gui/description/cards/" + ForgeRegistries.ITEMS.getKey(AbilityUtils.getStackInCuriosSlot(player, ability.getSlot()).getItem()).getPath() + "/" + ability.getAbility() + ".png"));

        RenderSystem.enableBlend();

        poseStack.pushPose();

        int width = 20;
        int height = 29;

        float scale = (float) ((1F + Mth.clamp(Math.pow(13.5F, -Math.abs(realIndex)), 0, 0.2F)) + (realIndex == 0 ? (Math.sin((player.tickCount + partialTicks) * 0.1F) * 0.05F) : 0));

        RenderUtils.renderTextureFromCenter(poseStack, x - (1 * scale), y - (1 * scale), width, height, scale);

        RenderSystem.setShaderTexture(0, new ResourceLocation(Reference.MODID, "textures/hud/abilities/border.png"));

        width = 28;
        height = 37;

        RenderUtils.renderTextureFromCenter(poseStack, x, y, width, height, scale);

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

        return entries.get(index);
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
            if (!HotkeyRegistry.ABILITY_LIST.isDown())
                return;

            applyDelta(event.getScrollDelta() > 0 ? -1 : 1);

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.side != LogicalSide.CLIENT || event.phase != TickEvent.Phase.END
                    || !HotkeyRegistry.ABILITY_LIST.isDown())
                return;

            Player player = event.player;

            entries = AbilityUtils.getActiveEntries(player);

            if (selectedIndex > entries.size())
                selectedIndex = 0;
        }

        @SubscribeEvent
        public static void onKeyPressed(InputEvent.Key event) {
            Player player = Minecraft.getInstance().player;

            if (player == null)
                return;

            if (HotkeyRegistry.ABILITY_CAST.consumeClick()) {
                AbilityEntry ability = getAbilityByIndex(selectedIndex);

                if (ability != null)
                    NetworkHandler.sendToServer(new SpellCastPacket(ability.getAbility(), ability.getSlot()));
            }
        }
    }
}