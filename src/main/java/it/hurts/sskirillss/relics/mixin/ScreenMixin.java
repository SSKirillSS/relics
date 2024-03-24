package it.hurts.sskirillss.relics.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.api.events.common.TooltipDisplayEvent;
import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.data.base.ParticleData;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(Screen.class)
public class ScreenMixin {
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

    @Inject(method = "renderTooltipInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;blitOffset:F", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onTooltipRender(final PoseStack pose, final List<ClientTooltipComponent> tooltip, int mouseX, int mouseY, final CallbackInfo callback, final RenderTooltipEvent.Pre event, int width, int height, int xPos, int yPos) {
        MinecraftForge.EVENT_BUS.post(new TooltipDisplayEvent(event.getItemStack(), pose, width, height, xPos, yPos));
    }
}