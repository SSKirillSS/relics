package it.hurts.sskirillss.relics.client.screen.description;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.base.IHoverableWidget;
import it.hurts.sskirillss.relics.client.screen.description.widgets.relic.card.AbilityCardIconWidget;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.utils.LevelingUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.QualityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RelicDescriptionScreen extends Screen {
    private final Minecraft MC = Minecraft.getInstance();

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/gui/description/relic_background.png");
    public static final ResourceLocation WIDGETS = new ResourceLocation(Reference.MODID, "textures/gui/description/relic_widgets.png");

    public final BlockPos pos;
    public final ItemStack stack;

    public int backgroundHeight = 177;
    public int backgroundWidth = 256;

    public RelicDescriptionScreen(BlockPos pos, ItemStack stack) {
        super(Component.empty());

        this.pos = pos;
        this.stack = stack;
    }

    @Override
    protected void init() {
        if (!(stack.getItem() instanceof RelicItem relic))
            return;

        TextureManager manager = MC.getTextureManager();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        RelicAbilityData abilityData = relicData.getAbilityData();

        if (abilityData != null) {
            int step = 0;

            for (Map.Entry<String, RelicAbilityEntry> ability : abilityData.getAbilities().entrySet()) {
                this.addRenderableWidget(new AbilityCardIconWidget(((this.width - backgroundWidth) / 2) + 54 + step, ((this.height - backgroundHeight) / 2) + 124, this, ability.getKey()));

                step += 30;
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        PoseStack pPoseStack = guiGraphics.pose();
        TextureManager manager = MC.getTextureManager();

        this.renderBackground(guiGraphics);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        manager.bindForSetup(TEXTURE);

        int texWidth = 512;
        int texHeight = 512;

        int x = (this.width - backgroundWidth) / 2;
        int y = (this.height - backgroundHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, texWidth, texHeight);

        int level = LevelingUtils.getLevel(stack);
        int maxLevel = relicData.getLevelingData().getMaxLevel();

        int percentage = LevelingUtils.getExperience(stack) / (LevelingUtils.getExperienceBetweenLevels(stack, level, level + 1) / 100);

        boolean isMaxLevel = LevelingUtils.getLevel(stack) >= maxLevel;

        if (isMaxLevel)
            guiGraphics.blit(TEXTURE, x + 57, y + 89, 258, 80, 142, 12, texWidth, texHeight);
        else
            guiGraphics.blit(TEXTURE, x + 74, y + 89, 275, 80, (int) Math.ceil(percentage / 100F * 109F), 10, texWidth, texHeight);

        int xOff = 0;

        for (int i = 1; i < QualityUtils.getRelicQuality(stack) + 1; i++) {
            boolean isAliquot = i % 2 == 1;

            guiGraphics.blit(TEXTURE, x + 100 + xOff, y + 11, 258 + (isAliquot ? 0 : 5), 94, isAliquot ? 5 : 9, 9, texWidth, texHeight);

            xOff += isAliquot ? 5 : 6;
        }

        MutableComponent name = Component.literal(stack.getDisplayName().getString()
                        .replace("[", "").replace("]", ""))
                .withStyle(ChatFormatting.BOLD);

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 1F);

        guiGraphics.drawString(MC.font, name, (x * 2 + ((backgroundWidth - MC.font.width(name) / 2))), (y + 34) * 2, 0x412708, false);

        pPoseStack.popPose();

        pPoseStack.pushPose();

        MutableComponent experience = isMaxLevel ? Component.translatable("tooltip.relics.relic.max_level")
                : Component.literal(LevelingUtils.getExperience(stack) + "/" + LevelingUtils.getExperienceBetweenLevels(stack, level, level + 1) + " [" + percentage + "%]");

        pPoseStack.scale(0.5F, 0.5F, 1F);

        guiGraphics.drawString(MC.font, experience, (x + 128 - font.width(experience) / 4) * 2, (y + 85) * 2, 0xFFFFFF);

        pPoseStack.popPose();

        if (!isMaxLevel) {
            guiGraphics.drawString(MC.font, String.valueOf(level), x + 66 - MC.font.width(String.valueOf(level)) / 2, y + 91, 0xFFFFFF);
            guiGraphics.drawString(MC.font, String.valueOf(level + 1), x + 190 - MC.font.width(String.valueOf(level + 1)) / 2, y + 91, 0xFFFFFF);
        }

        String registryName = ForgeRegistries.ITEMS.getKey(relic).getPath();

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 1F);

        int yOff = 9;

        List<FormattedCharSequence> lines = MC.font.split(Component.translatable("tooltip.relics." + registryName + ".leveling"), 255);

        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(MC.font, line, x * 2 + 61 * 2 + (265 - MC.font.width(line)) / 2, (y + 43) * 2 + yOff, 0x412708, false);

            yOff += 9;
        }

        pPoseStack.popPose();

        int points = LevelingUtils.getPoints(stack);

        if (points > 0) {
            manager.bindForSetup(WIDGETS);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, WIDGETS);

            guiGraphics.blit(WIDGETS, x + backgroundWidth - 3, y + 17, 0, 0, 40, 25, texWidth, texHeight);
            guiGraphics.blit(WIDGETS, x + backgroundWidth + 16, y + 22, 0, 27, 16, 13, texWidth, texHeight);

            String value = String.valueOf(points);

            guiGraphics.drawString(MC.font, value, x + backgroundWidth + 7 - font.width(value) / 2, y + 25, 0xFFFFFF);
        }

        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        for (GuiEventListener listener : this.children()) {
            if (listener instanceof AbstractButton button && button.isHovered()
                    && button instanceof IHoverableWidget widget)
                widget.onHovered(guiGraphics, pMouseX, pMouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}