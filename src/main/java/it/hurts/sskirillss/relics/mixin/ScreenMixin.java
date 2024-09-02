package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.client.screen.base.ITickingWidget;
import it.hurts.sskirillss.relics.client.screen.description.general.particles.base.ParticleData;
import it.hurts.sskirillss.relics.client.screen.utils.ParticleStorage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;

        for (ParticleData data : ParticleStorage.getParticles(screen))
            data.render(screen, pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}