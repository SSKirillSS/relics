package it.hurts.sskirillss.relics.items.relics.base;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.hurts.sskirillss.octolib.config.data.ConfigContext;
import it.hurts.sskirillss.octolib.config.data.OctoConfig;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.items.ItemBase;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.utils.ResearchUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class RelicItem extends ItemBase implements ICurioItem {
    @Getter
    @Setter
    private RelicData relicData;

    public RelicItem(Item.Properties properties) {
        super(properties);

        setRelicData(constructRelicData());
    }

    public RelicItem() {
        super(new Item.Properties()
                .rarity(Rarity.RARE)
                .stacksTo(1));

        setRelicData(constructRelicData());
    }

    public final OctoConfig getConfig() {
        return ConfigHelper.getConfig(this);
    }

    public void appendConfig(ConfigContext context) {

    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();

        RelicAttributeModifier attributes = getAttributeModifiers(stack);
        RelicSlotModifier slots = getSlotModifiers(stack);

        if (attributes != null)
            attributes.getAttributes().forEach(attribute ->
                    modifiers.put(attribute.getAttribute(), new AttributeModifier(uuid,
                            ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath() + "_" + ForgeRegistries.ATTRIBUTES.getKey(attribute.getAttribute()).getPath(),
                            attribute.getMultiplier(), attribute.getOperation())));

        if (slots != null)
            slots.getModifiers().forEach(slot ->
                    CuriosApi.addSlotModifier(modifiers, slot.getLeft(), uuid, slot.getRight(), AttributeModifier.Operation.ADDITION));

        return modifiers;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Vec3 pos = entity.position();
        RandomSource random = entity.getCommandSenderWorld().getRandom();

        if (getStyle(stack) != null) {
            String hex = getStyle(stack).getParticles();

            Color color = hex == null || hex.isEmpty() ? new Color(stack.getRarity().color.getColor()) : Color.decode(hex);

            entity.getCommandSenderWorld().addParticle(new CircleTintData(color, random.nextFloat() * 0.025F + 0.04F, 25, 0.97F, true),
                    pos.x() + MathUtils.randomFloat(random) * 0.25F, pos.y() + 0.1F,
                    pos.z() + MathUtils.randomFloat(random) * 0.25F, 0, random.nextFloat() * 0.05D, 0);
        }

        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide()) {
            for (Map.Entry<String, RelicAbilityEntry> entry : AbilityUtils.getRelicAbilityData(stack.getItem()).getAbilities().entrySet()) {
                String ability = entry.getKey();

                int cooldown = AbilityUtils.getAbilityCooldown(stack, ability);

                if (cooldown > 0)
                    AbilityUtils.addAbilityCooldown(stack, ability, -1);
            }
        }
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        return new ArrayList<>();
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
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

    public void castActiveAbility(ItemStack stack, Player player, String ability, AbilityCastType type, AbilityCastStage stage) {

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
    public RelicData constructRelicData() {
        return null;
    }
}