package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStorage;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        relics$processTooltip(stack, context, tooltip);
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    private void relics$processTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip) {
        Item item = stack.getItem();

        if (!(item instanceof IRelicItem relic))
            return;

        LocalPlayer player = Minecraft.getInstance().player;

        tooltip.add(Component.literal(" "));

        if (relic.isItemResearched(player)) {
            if (Screen.hasShiftDown()) {
                RelicData relicData = relic.getRelicData();

                if (relicData == null)
                    return;

                Map<String, AbilityData> abilities = relicData.getAbilities().getAbilities();

                tooltip.add(Component.literal("▶ ").withStyle(ChatFormatting.DARK_GREEN)
                        .append(Component.translatable("tooltip.relics.relic.tooltip.abilities").withStyle(ChatFormatting.GREEN)));

                for (Map.Entry<String, AbilityData> entry : abilities.entrySet()) {
                    String id = BuiltInRegistries.ITEM.getKey(item).getPath();
                    String name = entry.getKey();

                    if (!relic.canUseAbility(stack, name))
                        continue;

                    tooltip.add(Component.literal("   ◆ ").withStyle(ChatFormatting.GREEN)
                            .append(Component.translatable("tooltip.relics." + id + ".ability." + name).withStyle(ChatFormatting.YELLOW))
                            .append(Component.literal(" - ").withStyle(ChatFormatting.WHITE))
                            .append(Component.translatable("tooltip.relics." + id + ".ability." + name + ".description").withStyle(ChatFormatting.GRAY)));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.relics.relic.tooltip.shift").withStyle(ChatFormatting.GRAY));
            }
        } else
            tooltip.add(Component.translatable("tooltip.relics.relic.tooltip.table").withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal(" "));
    }

    @Inject(method = "verifyComponentsAfterLoad", at = @At("HEAD"))
    public void onVerifyComponentsAfterLoad(ItemStack stack, CallbackInfo ci) {
        if (!(stack.getItem() instanceof IRelicItem relic))
            return;

        for (AbilityData abilityData : relic.getAbilitiesData().getAbilities().values()) {
            String abilityId = abilityData.getId();

            if (relic.getAbilityComponent(stack, abilityId) == null)
                relic.randomizeAbility(stack, abilityId);
            else {
                for (StatData statData : relic.getAbilityData(abilityId).getStats().values()) {
                    String statId = statData.getId();

                    if (relic.getStatComponent(stack, abilityId, statId) == null)
                        relic.randomizeStat(stack, abilityId, statId);
                }
            }
        }
    }
}