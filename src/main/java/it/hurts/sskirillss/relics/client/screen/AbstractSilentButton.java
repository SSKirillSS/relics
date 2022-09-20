package it.hurts.sskirillss.relics.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSilentButton extends AbstractWidget {
    private final Minecraft MC = Minecraft.getInstance();

    public AbstractSilentButton(int x, int y, int width, int height) {
        super(x, y, width, height, TextComponent.EMPTY);
    }

    public abstract void onPress();

    @Nullable
    public List<Component> getTooltip() {
        return null;
    }

    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    @Deprecated
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        render(poseStack, mouseX, mouseY, partialTick);

        List<Component> tooltip = getTooltip();

        if (this.isHoveredOrFocused() && MC.screen != null && tooltip != null) {
            MC.screen.renderTooltip(poseStack, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible)
            return false;

        if (keyCode != 257 && keyCode != 32 && keyCode != 335)
            return false;

        this.onPress();

        return true;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}