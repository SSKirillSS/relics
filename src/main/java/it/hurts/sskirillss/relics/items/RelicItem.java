package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RelicItem extends Item {
    public RelicItem(Rarity rarity) {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(1)
                .rarity(rarity));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!(entityIn instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entityIn;
        if (RelicsConfig.RelicsGeneral.STORE_RELIC_OWNER.get()) {
            if (RelicUtils.Owner.getOwner(stack, worldIn) == null) RelicUtils.Owner.setOwnerUUID(stack, player.getUUID());
            PlayerEntity owner = RelicUtils.Owner.getOwner(stack, worldIn);
            if (RelicsConfig.RelicsGeneral.DAMAGE_NON_RELIC_OWNER_AMOUNT.get() > 0 && player.tickCount % 20 == 0 && owner != player)
                entityIn.hurt(owner != null ? DamageSource.playerAttack(owner) : DamageSource.GENERIC,
                        RelicsConfig.RelicsGeneral.DAMAGE_NON_RELIC_OWNER_AMOUNT.get().floatValue());
        }
        if (RelicUtils.Rarity.getRarity(stack) == -1)
            RelicUtils.Rarity.setRarity(stack, RelicUtils.Rarity.calculateRandomRarity(worldIn.getRandom()));
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null) return;
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, worldIn);
        if (RelicsConfig.RelicsGeneral.STORE_RELIC_OWNER.get()) tooltip.add(new TranslationTextComponent("tooltip.relics.owner",
                owner != null ? owner.getDisplayName() : new TranslationTextComponent("tooltip.relics.owner.unknown")));
        int rarity = RelicUtils.Rarity.getRarity(stack);
        tooltip.add(new TranslationTextComponent("tooltip.relics.rarity",
                rarity != -1 ? rarity : new TranslationTextComponent("tooltip.relics.rarity.unknown")));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}