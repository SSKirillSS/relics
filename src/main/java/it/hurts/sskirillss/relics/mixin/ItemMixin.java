package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStorage;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At(value = "TAIL"), method = "<init>")
    protected void init(Item.Properties properties, CallbackInfo ci) {
        Item item = (Item) (Object) this;

        if (item instanceof IRelicItem relic)
            RelicStorage.RELICS.put(relic, relic.getRelicData());
    }

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected, CallbackInfo ci) {
        if (level.isClientSide() || !(stack.getItem() instanceof IRelicItem relic))
            return;

        for (Map.Entry<String, AbilityData> entry : relic.getRelicData().getAbilities().getAbilities().entrySet()) {
            String ability = entry.getKey();

            if (relic.getAbilityCooldown(stack, ability) > 0)
                relic.addAbilityCooldown(stack, ability, -1);
        }
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced, CallbackInfo ci) {
        relics$processTooltip(stack, level, tooltip);
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private void relics$processTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip) {
        Item item = stack.getItem();

        if (!(item instanceof IRelicItem relic) || (level == null || !level.isClientSide()))
            return;

        LocalPlayer player = Minecraft.getInstance().player;

        tooltip.add(new TextComponent(" "));

        if (relic.isItemResearched(player)) {
            if (Screen.hasShiftDown()) {
                RelicData relicData = relic.getRelicData();

                if (relicData == null)
                    return;

                Map<String, AbilityData> abilities = relicData.getAbilities().getAbilities();

                tooltip.add(new TextComponent("▶ ").withStyle(ChatFormatting.DARK_GREEN)
                        .append(new TranslatableComponent("tooltip.relics.relic.tooltip.abilities").withStyle(ChatFormatting.GREEN)));

                for (Map.Entry<String, AbilityData> entry : abilities.entrySet()) {
                    String id = ForgeRegistries.ITEMS.getKey(item).getPath();
                    String name = entry.getKey();

                    if (!relic.canUseAbility(stack, name))
                        continue;

                    tooltip.add(new TextComponent("   ◆ ").withStyle(ChatFormatting.GREEN)
                            .append(new TranslatableComponent("tooltip.relics." + id + ".ability." + name).withStyle(ChatFormatting.YELLOW))
                            .append(new TextComponent(" - ").withStyle(ChatFormatting.WHITE))
                            .append(new TranslatableComponent("tooltip.relics." + id + ".ability." + name + ".description").withStyle(ChatFormatting.GRAY)));
                }
            } else {
                tooltip.add(new TranslatableComponent("tooltip.relics.relic.tooltip.shift").withStyle(ChatFormatting.GRAY));
            }
        } else
            tooltip.add(new TranslatableComponent("tooltip.relics.relic.tooltip.table").withStyle(ChatFormatting.GRAY));

        tooltip.add(new TextComponent(" "));
    }
}