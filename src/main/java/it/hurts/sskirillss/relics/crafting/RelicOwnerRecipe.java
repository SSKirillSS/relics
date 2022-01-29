package it.hurts.sskirillss.relics.crafting;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.RecipeRegistry;
import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class RelicOwnerRecipe extends CustomRecipe {
    public RelicOwnerRecipe(ResourceLocation location) {
        super(location);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean foundContract = false;
        boolean foundItem = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

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

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack item = ItemStack.EMPTY;
        ItemStack contract = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.isEmpty())
                continue;

            if (contract.isEmpty() && stack.getItem() == ItemRegistry.RELIC_CONTRACT.get())
                contract = stack;
            else if (item.isEmpty())
                item = stack;
        }

        if (item.isEmpty() || contract.isEmpty())
            return ItemStack.EMPTY;

        ItemStack result = item.copy();

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
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.RELIC_OWNER.get();
    }
}