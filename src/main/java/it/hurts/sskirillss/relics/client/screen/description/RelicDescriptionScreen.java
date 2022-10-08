package it.hurts.sskirillss.relics.client.screen.description;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.widgets.relic.card.AbilityCardIconWidget;
import it.hurts.sskirillss.relics.indev.RelicAbilityData;
import it.hurts.sskirillss.relics.indev.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RelicDescriptionScreen extends Screen {
    private final Minecraft MC = Minecraft.getInstance();

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/description/relic_background.png");

    public final BlockPos pos;
    public final ItemStack stack;

    public int backgroundHeight = 177;
    public int backgroundWidth = 256;

    public RelicDescriptionScreen(BlockPos pos, ItemStack stack) {
        super(TextComponent.EMPTY);

        this.pos = pos;
        this.stack = stack;
    }

    @Override
    protected void init() {
        if (!(stack.getItem() instanceof RelicItem<?> relic))
            return;

        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        RelicAbilityData abilityData = relic.getNewData().getAbilityData();

        if (abilityData != null) {
            int step = 0;

            for (Map.Entry<String, RelicAbilityEntry> ability : abilityData.getAbilities().entrySet()) {
                this.addRenderableWidget(new AbilityCardIconWidget(((this.width - backgroundWidth) / 2) + 54 + step, ((this.height - backgroundHeight) / 2) + 124, this, ability.getKey()));

                step += 30;
            }
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!(stack.getItem() instanceof RelicItem<?> relic))
            return;

        TextureManager manager = MC.getTextureManager();

        this.renderBackground(pPoseStack);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        int texWidth = 512;
        int texHeight = 512;

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        blit(pPoseStack, x, y, 0, 0, backgroundWidth, backgroundHeight, texWidth, texHeight);

        int percentage = relic.getExperience(stack) / (relic.getTotalExperienceForLevel(stack, relic.getLevel(stack) + 1) / 100);

        blit(pPoseStack, x + 66, y + 93, 388, 0, (int) Math.ceil(percentage / 100F * 124F), 7, texWidth, texHeight);

        String name = stack.getDisplayName().getString()
                .replace("[", "")
                .replace("]", "");

        MC.font.drawShadow(pPoseStack, name, x + ((backgroundWidth - MC.font.width(name)) / 2F), y + 6, 0xFFFFFF);

        pPoseStack.pushPose();

        String experience = relic.getExperience(stack) + "/" + relic.getTotalExperienceForLevel(stack, relic.getLevel(stack) + 1) + " [" + percentage + "%]";

        pPoseStack.translate((this.width - (font.width(experience) * 0.5F)) / 2F, this.height / 2F, 0);
        pPoseStack.scale(0.5F, 0.5F, 1F);

        MC.font.drawShadow(pPoseStack, experience, 0, 0, 0xFFFFFF);

        pPoseStack.popPose();

        MC.font.drawShadow(pPoseStack, String.valueOf(relic.getLevel(stack)), x + 58, y + 93, 0xFFFFFF);
        MC.font.drawShadow(pPoseStack, String.valueOf(relic.getLevel(stack) + 1), x + 193, y + 93, 0xFFFFFF);

        List<FormattedCharSequence> lines = MC.font.split(new TranslatableComponent("tooltip.relics." + stack.getItem().getRegistryName().getPath() + ".lore"), 240);

        for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence line = lines.get(i);

            pPoseStack.pushPose();

            pPoseStack.scale(0.5F, 0.5F, 0.5F);

            MC.font.draw(pPoseStack, line, x * 2 + 128 * 2 - font.width(line) * 0.5F, y * 2 + i * 8 + 44 * 2, 0x412708);

            pPoseStack.popPose();
        }

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}