package it.hurts.sskirillss.relics.crafting;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.RecipeRegistry;
import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RelicOwnerRecipe extends SpecialRecipe {
    public RelicOwnerRecipe(ResourceLocation location) {
        super(location);
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
        boolean foundContract = false;
        boolean foundItem = false;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.isEmpty())
                continue;

            if (stack.getItem() == ItemRegistry.RELIC_CONTRACT.get() && NBTUtils.getInt(stack, RelicContractItem.TAG_BLOOD, 0) >= 4
                    && !RelicUtils.Owner.getOwnerUUID(stack).equals("") && !foundContract)
                foundContract = true;
            else if (!foundItem)
                foundItem = true;
            else
                return false;
        }

        return foundItem && foundContract;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingInventory inv) {
        ItemStack item = ItemStack.EMPTY;
        ItemStack contract = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.isEmpty())
                continue;

            if (contract.isEmpty() && stack.getItem() == ItemRegistry.RELIC_CONTRACT.get())
                contract = stack;
            else if (item.isEmpty())
                item = stack;
        }

        if (item.isEmpty() || contract.isEmpty())
            return ItemStack.EMPTY;

        ItemStack result = item.copy().split(1);

        RelicUtils.Owner.setOwnerUUID(result, RelicUtils.Owner.getOwnerUUID(contract));
        NBTUtils.setLong(result, RelicContractItem.TAG_DATE, 0);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > 1 || height > 1;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.RELIC_OWNER.get();
    }
}