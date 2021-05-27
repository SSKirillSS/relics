package it.hurts.sskirillss.relics.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RunicAltarRecipe implements IRecipe<RunicAltarContext> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;

    @ObjectHolder(Reference.MODID + ":" + "runic_altar")
    public static IRecipeSerializer<?> SERIALIZER = null;

    public static IRecipeType<RunicAltarRecipe> RECIPE = IRecipeType.register(new ResourceLocation(Reference.MODID, "runic_altar").toString());

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
    public boolean matches(RunicAltarContext context, World worldIn) {
        if (inputs.isEmpty()) return false;
        List<Ingredient> ingredients = new ArrayList<>(inputs);
        for (int i = 0; i < context.getInputs().size(); i++) {
            ItemStack input = context.getInputs().get(i);
            if (input.isEmpty()) break;
            int index = -1;
            for (int j = 0; j < ingredients.size(); j++) {
                if (!ingredients.get(j).test(input)) continue;
                index = j;
                break;
            }
            if (index != -1) ingredients.remove(index);
            else return false;
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
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return RECIPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RunicAltarRecipe> {
        @Override
        public RunicAltarRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            NonNullList<Ingredient> inputs = NonNullList.create();
            inputs.add(Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "input")));
            JsonArray ingredients = JSONUtils.getAsJsonArray(json, "ingredients");
            for (int i = 0; i < ingredients.size(); i++) inputs.add(Ingredient.fromJson(ingredients.get(i)));
            return new RunicAltarRecipe(recipeId, inputs, ShapedRecipe.itemFromJson(json.getAsJsonObject("result")));
        }

        @Nullable
        @Override
        public RunicAltarRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            int size = buffer.readVarInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) inputs.set(i, Ingredient.fromNetwork(buffer));
            return new RunicAltarRecipe(recipeId, inputs, buffer.readItem());
        }

        @Override
        public void toNetwork(PacketBuffer buffer, RunicAltarRecipe recipe) {
            buffer.writeVarInt(recipe.inputs.size());
            for (Ingredient ingredient : recipe.inputs) ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }
    }
}