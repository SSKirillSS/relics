package it.hurts.sskirillss.relics.compat.jei;

import it.hurts.sskirillss.relics.compat.jei.categories.RunicAltarCategory;
import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.utils.Reference;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class RelicsJEIPlugin implements IModPlugin {
    public static final ResourceLocation UID = new ResourceLocation(Reference.MODID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new RunicAltarCategory(guiHelper));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        ClientWorld world = Minecraft.getInstance().level;

        if (world == null)
            return;

        registration.addRecipes(world.getRecipeManager().getAllRecipesFor(RunicAltarRecipe.RECIPE), RunicAltarCategory.UID);
    }
}