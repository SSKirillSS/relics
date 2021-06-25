package it.hurts.sskirillss.relics.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.sskirillss.relics.init.HotkeyRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketRelicAbility;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
public class HUDEventHandler {
    private static final List<KeyBinding> keyBindings = Arrays.asList(HotkeyRegistry.HUD_FIRST.getKeyBinding(), HotkeyRegistry.HUD_SECOND.getKeyBinding(),
            HotkeyRegistry.HUD_THIRD.getKeyBinding(), HotkeyRegistry.HUD_FOURTH.getKeyBinding(), HotkeyRegistry.HUD_FIFTH.getKeyBinding());

    static List<ItemStack> relics = new ArrayList<>();

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
            matrix.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, Math.min(1.0F, animation * 0.025F));
            String path = relics.get(i).getItem().getRegistryName().getPath();
            if (path.equals(Items.AIR.getRegistryName().getPath())) continue;
            ResourceLocation RELIC = new ResourceLocation(Reference.MODID, "textures/items/" + path + ".png");
            manager.bind(SLOT);
            AbstractGui.blit(matrix, x - 3, y - 3, 22, 28, 0F, 0F, 1, 1, 1, 1);
            manager.bind(RELIC);
            AbstractGui.blit(matrix, x, y, 16, 16, 0F, 0F, 1, 1, 1, 1);
            KeyBinding key = keyBindings.get(i - offset);
            matrix.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().font.draw(matrix, key.getKeyModifier().getCombinedName(key.getKey(),
                    () -> key.getKey().getDisplayName()), x * 2.0F - 3, (y + 19) * 2.0F, 0xFFFFFF);
            RenderSystem.disableBlend();
            matrix.popPose();
            y += 29;
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
                if (!relic.hasAbility()) return;
                relics.add(stack);
            }
            offset = offset / slots * slots;
            if (offset == relics.size()) offset = Math.max(0, offset - 1) / slots * slots;
        });
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (relics.size() > slots && animation >= 44) {
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
            ItemStack stack = relics.get(i + offset);
            if (!(stack.getItem() instanceof RelicItem)) return;
            RelicItem relic = (RelicItem) stack.getItem();
            if (!relic.hasAbility()) return;
            NetworkHandler.sendToServer(new PacketRelicAbility(stack));
        }
    }
}