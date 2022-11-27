package it.hurts.sskirillss.relics.client.screen.description;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.screen.description.widgets.relic.card.AbilityCardIconWidget;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
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
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!(stack.getItem() instanceof RelicItem relic))
            return;

        RelicData relicData = relic.getRelicData();

        if (relicData == null)
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

        int level = RelicItem.getLevel(stack);
        int maxLevel = relicData.getLevelingData().getMaxLevel();

        int percentage = RelicItem.getExperience(stack) / (RelicItem.getExperienceBetweenLevels(stack, level, level + 1) / 100);

        boolean isMaxLevel = RelicItem.getLevel(stack) >= maxLevel;

        if (isMaxLevel)
            blit(pPoseStack, x + 57, y + 89, 258, 80, 142, 12, texWidth, texHeight);
        else
            blit(pPoseStack, x + 74, y + 89, 275, 80, (int) Math.ceil(percentage / 100F * 109F), 10, texWidth, texHeight);

        boolean isHoveringExperience = (pMouseX >= x + 55
                && pMouseY >= y + 87
                && pMouseX < x + 55 + 146
                && pMouseY < y + 87 + 17);

        if (isHoveringExperience)
            blit(pPoseStack, x + 54, y + 79, 364, 0, 148, 26, texWidth, texHeight);

        MutableComponent name = Component.literal(stack.getDisplayName().getString()
                .replace("[", "").replace("]", ""))
                .append(Component.translatable("tooltip.relics.relic.level", level, maxLevel == -1 ? "∞" : maxLevel));

        MC.font.drawShadow(pPoseStack, name, x + ((backgroundWidth - MC.font.width(name)) / 2F), y + 6, 0xFFFFFF);

        pPoseStack.pushPose();

        MutableComponent experience = isMaxLevel ? Component.translatable("tooltip.relics.relic.max_level")
                : Component.literal(RelicItem.getExperience(stack) + "/" + RelicItem.getExperienceBetweenLevels(stack, level, level + 1) + " [" + percentage + "%]");

        pPoseStack.scale(0.5F, 0.5F, 1F);

        MC.font.drawShadow(pPoseStack, experience, (x + 128 - font.width(experience) / 4F) * 2, (y + 85) * 2, 0xFFFFFF);

        pPoseStack.popPose();

        if (!isMaxLevel) {
            MC.font.drawShadow(pPoseStack, String.valueOf(level), x + 66 - MC.font.width(String.valueOf(level)) / 2F, y + 91, 0xFFFFFF);
            MC.font.drawShadow(pPoseStack, String.valueOf(level + 1), x + 190 - MC.font.width(String.valueOf(level + 1)) / 2F, y + 91, 0xFFFFFF);
        }

        MutableComponent description = isHoveringExperience
                ? Component.translatable("tooltip.relics.relic.leveling.title").append(Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(relic).getPath() + ".leveling"))
                : Component.translatable("tooltip.relics." + ForgeRegistries.ITEMS.getKey(relic).getPath() + ".lore");

        List<FormattedCharSequence> lines = MC.font.split(description, 240);

        pPoseStack.pushPose();

        pPoseStack.scale(0.5F, 0.5F, 1F);

        for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence line = lines.get(i);

            MC.font.draw(pPoseStack, line, x * 2 + 128 * 2 - Math.round(font.width(line) / 2F), y * 2 + i * 9 + 31 * 2 + (40 - Math.round((lines.size() * MC.font.lineHeight) / 2F)), 0x412708);
        }

        pPoseStack.popPose();

        int points = RelicItem.getPoints(stack);

        if (points > 0) {
            manager.bindForSetup(WIDGETS);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, WIDGETS);

            blit(pPoseStack, x + backgroundWidth - 3, y + 31, 0, 0, 40, 25, texWidth, texHeight);
            blit(pPoseStack, x + backgroundWidth + 16, y + 36, 0, 27, 16, 13, texWidth, texHeight);

            String value = String.valueOf(points);

            MC.font.draw(pPoseStack, value, x + backgroundWidth + 7 - font.width(value) / 2F, y + 39, 0xFFFFFF);
        }

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}