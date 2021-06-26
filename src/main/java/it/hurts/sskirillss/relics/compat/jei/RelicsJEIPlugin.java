package it.hurts.sskirillss.relics.compat.jei;

import it.hurts.sskirillss.relics.compat.jei.categories.RunicAltarCategory;
import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.RegistryObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@JeiPlugin
public class RelicsJEIPlugin implements IModPlugin {
    public static final ResourceLocation UID = new ResourceLocation(Reference.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new RunicAltarCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientWorld world = Minecraft.getInstance().level;
        if (world == null) return;
        registration.addRecipes(world.getRecipeManager().getAllRecipesFor(RunicAltarRecipe.RECIPE), RunicAltarCategory.UID);
        IIngredientType<ItemStack> type = registration.getIngredientManager().getIngredientType(ItemStack.class);
        ItemRegistry.ITEMS.getEntries().stream().filter(Objects::nonNull).map(RegistryObject::get)
                .filter(item -> item instanceof RelicItem).map(ItemStack::new).forEach(stack -> {
            RelicItem relic = (RelicItem) stack.getItem();
            List<ITextComponent> description = relic.getShiftTooltip(stack);
            if (!description.isEmpty()) registration.addIngredientInfo(stack, type, description.stream()
                    .map(component -> component.getString() + "\n").collect(Collectors.joining()));
        });
    }
}