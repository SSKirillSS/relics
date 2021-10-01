package it.hurts.sskirillss.relics.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.relics.init.HotkeyRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketRelicAbility;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
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
    private static final List<KeyBinding> keyBindings = Arrays.asList(HotkeyRegistry.HUD_FIRST.getKeyBinding(), HotkeyRegistry.HUD_SECOND.getKeyBinding(),
            HotkeyRegistry.HUD_THIRD.getKeyBinding(), HotkeyRegistry.HUD_FOURTH.getKeyBinding(), HotkeyRegistry.HUD_FIFTH.getKeyBinding());

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
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR
                || !(Minecraft.getInstance().getCameraEntity() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) Minecraft.getInstance().getCameraEntity();
        if (player == null || relics.isEmpty()) return;
        float multiplier = (System.currentTimeMillis() - prevTime) / 11F;
        prevTime = System.currentTimeMillis();
        if (Screen.hasAltDown()) {
            if (!locked) animation = Math.min(44, animation + multiplier);
        } else {
            if (locked && animation < 500) animation = animation + multiplier;
            if (animation >= 500) locked = false;
            if (!locked) animation = Math.max(0, animation - multiplier);
        }
        if (animation == 44) locked = true;
        if (animation == 0) return;
        int x = (int) (event.getWindow().getGuiScaledWidth() + 5 - Math.min(44, animation));
        int y = 29;
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        MatrixStack matrix = event.getMatrixStack();
        RenderSystem.enableBlend();
        if (relics.size() > slots) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, Math.min(1.0F, animation * 0.025F));
            manager.bind(ARROW_UP);
            AbstractGui.blit(matrix, x - 3, y, 22, 22, 0F, 0F, 1, 1, 1, 1);
            RenderSystem.disableBlend();
            y += 29;
        }
        for (int i = offset; i < Math.min(relics.size(), slots) + offset; i++) {
            if (i >= relics.size() || i < 0) break;
            drawRelic(matrix, manager, relics.get(i).getKey().getItem(), keyBindings.get(i - offset), player, x, y);
            y += 33;
        }
        if (relics.size() > slots) {
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, Math.min(1.0F, animation * 0.025F));
            manager.bind(ARROW_DOWN);
            AbstractGui.blit(matrix, x - 3, y, 22, 22, 0F, 0F, 1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
        manager.bind(AbstractGui.GUI_ICONS_LOCATION);
    }

    private static void drawRelic(MatrixStack matrix, TextureManager manager, Item item, KeyBinding key, PlayerEntity player, int x, int y) {
        String path = item.getRegistryName().getPath();
        if (path.equals(Items.AIR.getRegistryName().getPath())) return;
        ResourceLocation RELIC = new ResourceLocation(Reference.MODID, "textures/items/" + path + ".png");

        matrix.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, Math.min(1.0F, animation * 0.025F));

        manager.bind(SLOT);
        AbstractGui.blit(matrix, x - 4, y - 4, 24, 32, 0F, 0F, 1, 1, 1, 1);

        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(new ItemStack(item), x, y);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        float yOff = y + MathHelper.floor(16.0F * (1.0F - player.getCooldowns().getCooldownPercent(item, Minecraft.getInstance().getFrameTime())));
        float offset = MathHelper.ceil(16.0F * player.getCooldowns().getCooldownPercent(item, Minecraft.getInstance().getFrameTime()));
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.vertex(x, yOff + 0, 0.0D).color(255, 255, 255, 127).endVertex();
        builder.vertex(x, yOff + offset, 0.0D).color(255, 255, 255, 127).endVertex();
        builder.vertex(x + 16, yOff + offset, 0.0D).color(255, 255, 255, 127).endVertex();
        builder.vertex(x + 16, yOff + 0, 0.0D).color(255, 255, 255, 127).endVertex();
        tessellator.end();

        matrix.scale(0.5F, 0.5F, 0.5F);
        Minecraft.getInstance().font.draw(matrix, key.getKeyModifier().getCombinedName(key.getKey(),
                () -> key.getKey().getDisplayName()), x * 2.0F - 2, (y + 21) * 2.0F, 0xFFFFFF);

        RenderSystem.disableBlend();
        matrix.popPose();
    }

    @SubscribeEvent
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if (!player.getCommandSenderWorld().isClientSide() || player.tickCount % 10 != 0) return;
        CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(handler -> {
            relics.clear();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!(stack.getItem() instanceof RelicItem)) continue;
                RelicItem relic = (RelicItem) stack.getItem();
                if (!relic.hasAbility()) continue;
                relics.add(new ImmutablePair<>(stack, i));
            }
            offset = offset / slots * slots;
            if (offset == relics.size()) offset = Math.max(0, offset - 1) / slots * slots;
        });
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (relics.size() > slots) {
            if (HotkeyRegistry.HUD_UP.isDown()) {
                animation = 500;
                offset = offset - slots;
                if (offset < 0) offset = (relics.size() - 1) / slots * slots;
                player.getCommandSenderWorld().playSound(player, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
            } else if (HotkeyRegistry.HUD_DOWN.isDown()) {
                animation = 500;
                offset = offset + slots;
                if (offset >= relics.size()) offset = 0;
                player.getCommandSenderWorld().playSound(player, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
            }
        }
        for (int i = 0; i < keyBindings.size(); i++) {
            KeyBinding key = keyBindings.get(i);
            if (!key.isDown()) continue;
            animation = 500;
            int id = i + offset;
            if (id >= relics.size()) continue;
            ImmutablePair<ItemStack, Integer> pair = relics.get(i + offset);
            ItemStack stack = pair.getLeft();
            if (!(stack.getItem() instanceof RelicItem)) continue;
            RelicItem relic = (RelicItem) stack.getItem();
            if (!relic.hasAbility()) continue;
            NetworkHandler.sendToServer(new PacketRelicAbility(pair.getRight()));
            relic.castAbility(player, stack);
        }
    }
}