package it.hurts.sskirillss.relics.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RunicAltarRecipe implements Recipe<RunicAltarContext> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;

    @ObjectHolder(Reference.MODID + ":" + "runic_altar")
    public static RecipeSerializer<?> SERIALIZER = null;

    public static RecipeType<RunicAltarRecipe> RECIPE = RecipeType.register(new ResourceLocation(Reference.MODID, "runic_altar").toString());

    public RunicAltarRecipe(ResourceLocation id, NonNullList<Ingredient> inputs, ItemStack output) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public boolean matches(RunicAltarContext context, Level worldIn) {
        if (inputs.isEmpty())
            return false;
        List<Ingredient> ingredients = new ArrayList<>(inputs);

        for (int i = 0; i < context.getInputs().size(); i++) {
            ItemStack input = context.getInputs().get(i);

            if (input.isEmpty())
                break;

            int index = -1;

            for (int j = 0; j < ingredients.size(); j++) {
                if (!ingredients.get(j).test(input))
                    continue;

                index = j;

                break;
            }

            if (index != -1)
                ingredients.remove(index);
            else
                return false;
        }
        return ingredients.isEmpty();
    }

    @Override
    public ItemStack assemble(RunicAltarContext context) {
        return this.output.copy();
    }

    @Override
    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RECIPE;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RunicAltarRecipe> {
        @Override
        public RunicAltarRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            NonNullList<Ingredient> inputs = NonNullList.create();

            inputs.add(Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input")));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");

            for (int i = 0; i < ingredients.size(); i++)
                inputs.add(Ingredient.fromJson(ingredients.get(i)));

            return new RunicAltarRecipe(recipeId, inputs, new ItemStack(ShapedRecipe.itemFromJson(json.getAsJsonObject("result"))));
        }

        @Nullable
        @Override
        public RunicAltarRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int size = buffer.readVarInt();

            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);

            for (int i = 0; i < size; i++)
                inputs.set(i, Ingredient.fromNetwork(buffer));

            return new RunicAltarRecipe(recipeId, inputs, buffer.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RunicAltarRecipe recipe) {
            buffer.writeVarInt(recipe.inputs.size());

            for (Ingredient ingredient : recipe.inputs)
                ingredient.toNetwork(buffer);

            buffer.writeItem(recipe.output);
        }
    }
}