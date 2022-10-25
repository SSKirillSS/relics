package it.hurts.sskirillss.relics.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.api.events.utils.EventDispatcher;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen extends AbstractContainerEventHandler {
    @Shadow(remap = false)
    private final ItemStack tooltipStack = ItemStack.EMPTY;

    @Final
    @Shadow
    private final List<GuiEventListener> children = Lists.newArrayList();

    @Inject(method = "renderTooltipInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;blitOffset:F", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void renderTooltipInternal(PoseStack matrix, List<ClientTooltipComponent> components, int preX, int preY, CallbackInfo info, RenderTooltipEvent.Pre pre, int width, int height, int postX, int postY) {
        EventDispatcher.onRenderTooltip(tooltipStack, matrix, width, height, postX, postY);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }
}