package it.hurts.sskirillss.relics.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.api.events.common.TooltipDisplayEvent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(Screen.class)
public class MixinScreen extends AbstractContainerEventHandler {
    @Shadow(remap = false)
    private final ItemStack tooltipStack = ItemStack.EMPTY;

    @Final
    @Shadow
    private List<GuiEventListener> children;

    // FIXME 1.19.2 :: Still needed?
    @Inject(method = "renderTooltipInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;blitOffset:F", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void renderTooltipInternal(PoseStack matrix, List<ClientTooltipComponent> components, int preX, int preY, CallbackInfo info, RenderTooltipEvent.Pre pre, int width, int height, int postX, int postY) {
        TooltipDisplayEvent event = new TooltipDisplayEvent(tooltipStack, matrix, width, height, postX, postY);

        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;

        for (Map.Entry<Class<? extends Screen>, List<ParticleData>> entry : ParticleStorage.getParticlesData().entrySet()) {
            if (entry.getKey() != screen.getClass())
                continue;

            List<ParticleData> toRemove = new ArrayList<>();

            for (ParticleData data : entry.getValue()) {
                data.tick(screen);

                if (data.getLifeTime() <= 0)
                    toRemove.add(data);
                else
                    data.setLifeTime(data.getLifeTime() - 1);
            }

            List<ParticleData> particles = ParticleStorage.getParticles(screen);

            particles.removeAll(toRemove);

            ParticleStorage.getParticlesData().put(screen.getClass(), particles);
        }

        for (GuiEventListener listener : screen.children()) {
            if (listener instanceof AbstractButton button && button instanceof ITickingWidget widget)
                widget.onTick();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(PoseStack pose, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;

        for (ParticleData data : ParticleStorage.getParticles(screen))
            data.render(screen, pose, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }
}