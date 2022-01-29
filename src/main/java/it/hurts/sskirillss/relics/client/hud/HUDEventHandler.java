package it.hurts.sskirillss.relics.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.hurts.sskirillss.relics.init.HotkeyRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketRelicAbility;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.ImmutablePair;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
public class HUDEventHandler {
    private static final List<KeyMapping> keyBindings = Arrays.asList(HotkeyRegistry.HUD_FIRST, HotkeyRegistry.HUD_SECOND,
            HotkeyRegistry.HUD_THIRD, HotkeyRegistry.HUD_FOURTH, HotkeyRegistry.HUD_FIFTH);

    static List<ImmutablePair<ItemStack, Integer>> relics = new ArrayList<>();

    static int offset = 0;
    static int slots = 5;

    static float animation = 0;
    static long prevTime = System.currentTimeMillis();
    static boolean locked = false;

    private static final ResourceLocation SLOT = new ResourceLocation(Reference.MODID, "textures/hud/slot.png");
    private static final ResourceLocation ARROW_UP = new ResourceLocation(Reference.MODID, "textures/hud/arrow_up.png");
    private static final ResourceLocation ARROW_DOWN = new ResourceLocation(Reference.MODID, "textures/hud/arrow_down.png");

    @SubscribeEvent
    public static void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.CHAT
                || !(Minecraft.getInstance().getCameraEntity() instanceof Player player)
                || relics.isEmpty())
            return;

        float multiplier = (System.currentTimeMillis() - prevTime) / 11F;

        prevTime = System.currentTimeMillis();

        if (Screen.hasAltDown()) {
            if (!locked)
                animation = Math.min(44, animation + multiplier);
        } else {
            if (locked && animation < 500)
                animation = animation + multiplier;

            if (animation >= 500)
                locked = false;

            if (!locked)
                animation = Math.max(0, animation - multiplier);
        }

        if (animation == 44)
            locked = true;

        if (animation == 0)
            return;

        int x = (int) (event.getWindow().getGuiScaledWidth() + 5 - Math.min(44, animation));
        int y = 29;

        TextureManager manager = Minecraft.getInstance().getTextureManager();
        PoseStack matrix = event.getMatrixStack();

        RenderSystem.enableBlend();

        if (relics.size() > slots) {
            RenderSystem.clearColor(1.0F, 1.0F, 1.0F, Math.min(1.0F, animation * 0.025F));
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, ARROW_UP);

            manager.bindForSetup(ARROW_UP);
            GuiComponent.blit(matrix, x - 3, y, 22, 22, 0F, 0F, 1, 1, 1, 1);

            RenderSystem.disableBlend();

            y += 29;
        }
        for (int i = offset; i < Math.min(relics.size(), slots) + offset; i++) {
            if (i >= relics.size() || i < 0)
                break;

            drawRelic(matrix, manager, relics.get(i).getKey().getItem(), keyBindings.get(i - offset), player, x, y);

            y += 33;
        }
        if (relics.size() > slots) {
            RenderSystem.enableBlend();

            RenderSystem.clearColor(1.0F, 1.0F, 1.0F, Math.min(1.0F, animation * 0.025F));
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, ARROW_DOWN);

            manager.bindForSetup(ARROW_DOWN);
            GuiComponent.blit(matrix, x - 3, y, 22, 22, 0F, 0F, 1, 1, 1, 1);

            RenderSystem.disableBlend();
        }

        manager.bindForSetup(GuiComponent.GUI_ICONS_LOCATION);
    }

    private static void drawRelic(PoseStack matrix, TextureManager manager, Item item, KeyMapping key, Player player, int x, int y) {
        String path = item.getRegistryName().getPath();

        if (path.equals(Items.AIR.getRegistryName().getPath()))
            return;

        matrix.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, Math.min(1.0F, animation * 0.025F));

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, SLOT);

        manager.bindForSetup(SLOT);
        GuiComponent.blit(matrix, x - 4, y - 4, 24, 32, 0F, 0F, 1, 1, 1, 1);

        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(new ItemStack(item), x, y);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        float yOff = y + Mth.floor(16.0F * (1.0F - player.getCooldowns().getCooldownPercent(item, Minecraft.getInstance().getFrameTime())));
        float offset = Mth.ceil(16.0F * player.getCooldowns().getCooldownPercent(item, Minecraft.getInstance().getFrameTime()));

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(x, yOff + 0, 0.0D).color(255, 255, 255, 127).endVertex();
        builder.vertex(x, yOff + offset, 0.0D).color(255, 255, 255, 127).endVertex();
        builder.vertex(x + 16, yOff + offset, 0.0D).color(255, 255, 255, 127).endVertex();
        builder.vertex(x + 16, yOff + 0, 0.0D).color(255, 255, 255, 127).endVertex();

        tesselator.end();

        matrix.scale(0.5F, 0.5F, 0.5F);
        Minecraft.getInstance().font.draw(matrix, key.getKeyModifier().getCombinedName(key.getKey(),
                () -> key.getKey().getDisplayName()), x * 2.0F - 2, (y + 21) * 2.0F, 0xFFFFFF);

        RenderSystem.disableBlend();

        matrix.popPose();
    }

    @SubscribeEvent
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof Player player))
            return;

        if (!player.getCommandSenderWorld().isClientSide() || player.tickCount % 10 != 0)
            return;

        CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(handler -> {
            relics.clear();

            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);

                if (!(stack.getItem() instanceof RelicItem))
                    continue;

                RelicItem<?> relic = (RelicItem<?>) stack.getItem();

                if (DurabilityUtils.isBroken(stack) || !relic.getData().hasAbility())
                    continue;

                relics.add(new ImmutablePair<>(stack, i));
            }

            offset = offset / slots * slots;

            if (offset == relics.size())
                offset = Math.max(0, offset - 1) / slots * slots;
        });
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return;

        if (relics.size() > slots) {
            if (HotkeyRegistry.HUD_UP.isDown()) {
                animation = 500;
                offset = offset - slots;

                if (offset < 0)
                    offset = (relics.size() - 1) / slots * slots;

                player.getCommandSenderWorld().playSound(player, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
            } else if (HotkeyRegistry.HUD_DOWN.isDown()) {
                animation = 500;
                offset = offset + slots;

                if (offset >= relics.size())
                    offset = 0;

                player.getCommandSenderWorld().playSound(player, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.MASTER, 1.0F, 1.0F);
            }
        }
        for (int i = 0; i < keyBindings.size(); i++) {
            KeyMapping key = keyBindings.get(i);

            if (!key.isDown())
                continue;

            animation = 500;
            int id = i + offset;

            if (id >= relics.size())
                continue;

            ImmutablePair<ItemStack, Integer> pair = relics.get(i + offset);
            ItemStack stack = pair.getLeft();

            if (!(stack.getItem() instanceof RelicItem<?> relic))
                continue;

            if (!relic.getData().hasAbility())
                continue;

            NetworkHandler.sendToServer(new PacketRelicAbility(pair.getRight()));
            relic.castAbility(player, stack);
        }
    }
}