package it.hurts.sskirillss.relics.items.relics.base;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.ResearchUtils;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class RelicItem extends Item implements ICurioItem {
    public RelicItem(Item.Properties properties) {
        super(properties);
    }

    public RelicItem() {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .rarity(Rarity.RARE)
                .stacksTo(1));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();

        if (DurabilityUtils.isBroken(stack))
            return modifiers;

        RelicAttributeModifier attributes = getAttributeModifiers(stack);
        RelicSlotModifier slots = getSlotModifiers(stack);

        if (attributes != null)
            attributes.getAttributes().forEach(attribute ->
                    modifiers.put(attribute.getAttribute(), new AttributeModifier(uuid,
                            ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + "_" + ForgeRegistries.ATTRIBUTES.getKey(attribute.getAttribute()).getPath(),
                            attribute.getMultiplier(), attribute.getOperation())));

        if (slots != null)
            slots.getModifiers().forEach(slot ->
                    CuriosApi.getCuriosHelper().addSlotModifier(modifiers, slot.getLeft(), uuid, slot.getRight(), AttributeModifier.Operation.ADDITION));

        return modifiers;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!DurabilityUtils.isBroken(stack)) {
            Vec3 pos = entity.position();
            RandomSource random = entity.getCommandSenderWorld().getRandom();

            if (getStyle(stack) != null) {
                String hex = getStyle(stack).getParticles();

                Color color = hex == null || hex.isEmpty() ? new Color(stack.getRarity().color.getColor()) : Color.decode(hex);

                entity.getCommandSenderWorld().addParticle(new CircleTintData(color, random.nextFloat() * 0.025F + 0.04F, 25, 0.97F, true),
                        pos.x() + MathUtils.randomFloat(random) * 0.25F, pos.y() + 0.1F,
                        pos.z() + MathUtils.randomFloat(random) * 0.25F, 0, random.nextFloat() * 0.05D, 0);
            }
        }

        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        for (Map.Entry<String, RelicAbilityEntry> entry : AbilityUtils.getRelicAbilityData(stack.getItem()).getAbilities().entrySet()) {
            String ability = entry.getKey();

            int cooldown = AbilityUtils.getAbilityCooldown(stack, ability);

            if (cooldown > 0)
                AbilityUtils.addAbilityCooldown(stack, ability, -1);
        }
    }

    @Override
    public boolean showAttributesTooltip(String identifier, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return !DurabilityUtils.isBroken(stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !DurabilityUtils.isBroken(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return switch (stack.getRarity()) {
            case COMMON -> 100;
            case UNCOMMON -> 150;
            case RARE -> 200;
            case EPIC -> 250;
        };
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return stack.getMaxDamage() > 0;
    }

    // Mojank moment

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) getMaxDamage(stack));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
//        Triple<String, String, String> color = getStyle(stack).getDurability();
//
//        if (color == null)
        return Mth.hsvToRgb(Math.max(0F, ((float) getMaxDamage(stack) - (float) stack.getDamageValue()) / (float) getMaxDamage(stack)) / 3F, 1F, 1F);
//
//        float percentage = stack.getDamageValue() * 100F / getMaxDamage(stack);
//
//        return Color.decode(percentage < 33.3F ? color.getLeft() : percentage < 66.6F ? color.getMiddle() : color.getRight()).getRGB();
    }

    public RelicStyleData getStyle(ItemStack stack) {
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        if (level == null || !level.isClientSide())
            return;

        LocalPlayer player = Minecraft.getInstance().player;

        tooltip.add(Component.literal(" "));

        if (ResearchUtils.isItemResearched(player, stack.getItem())) {
            if (Screen.hasShiftDown()) {
                RelicData relicData = getRelicData();

                if (relicData == null)
                    return;

                RelicAbilityData abilityData = relicData.getAbilityData();

                if (abilityData == null)
                    return;

                Map<String, RelicAbilityEntry> abilities = abilityData.getAbilities();

                tooltip.add(Component.literal("▶ ").withStyle(ChatFormatting.DARK_GREEN)
                        .append(Component.translatable("tooltip.relics.relic.tooltip.abilities").withStyle(ChatFormatting.GREEN)));

                for (Map.Entry<String, RelicAbilityEntry> entry : abilities.entrySet()) {
                    String item = ForgeRegistries.ITEMS.getKey(this).getPath();
                    String name = entry.getKey();

                    if (!AbilityUtils.canUseAbility(stack, name))
                        continue;

                    tooltip.add(Component.literal("   ◆ ").withStyle(ChatFormatting.GREEN)
                            .append(Component.translatable("tooltip.relics." + item + ".ability." + name).withStyle(ChatFormatting.YELLOW))
                            .append(Component.literal(" - ").withStyle(ChatFormatting.WHITE))
                            .append(Component.translatable("tooltip.relics." + item + ".ability." + name + ".description").withStyle(ChatFormatting.GRAY)));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.relics.relic.tooltip.shift").withStyle(ChatFormatting.GRAY));
            }
        } else
            tooltip.add(Component.translatable("tooltip.relics.relic.tooltip.table").withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal(" "));
    }

    public void castActiveAbility(ItemStack stack, Player player, String ability) {

    }

    public void tickActiveAbilitySelection(ItemStack stack, Player player, String ability) {

    }

    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return null;
    }

    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return null;
    }

    @Nullable
    public RelicData getRelicData() {
        return null;
    }
}