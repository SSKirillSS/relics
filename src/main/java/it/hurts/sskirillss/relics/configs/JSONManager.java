package it.hurts.sskirillss.relics.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.hurts.sskirillss.relics.configs.variables.RuneConfig;
import it.hurts.sskirillss.relics.configs.variables.crafting.RuneIngredients;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RuneLoot;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class JSONManager {
    private static final Gson SERIALIZER = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

    public static void setupRunesConfig() {
        Path dir = FMLPaths.CONFIGDIR.get().resolve("relics").resolve("items").resolve("runes");

        try {
            Files.createDirectories(dir);

            for (RegistryObject<Item> registryObject : ItemRegistry.ITEMS.getEntries()) {
                if (!registryObject.isPresent())
                    continue;

                Item item = registryObject.get();

                if (!(item instanceof RuneItem))
                    continue;

                RuneItem rune = (RuneItem) item;
                RuneIngredients defaultIngredients = new RuneIngredients(rune.getIngredients().stream().map(runeItem ->
                        runeItem.getRegistryName().getNamespace() + ":" + runeItem.getRegistryName().getPath()).collect(Collectors.toList()));
                RuneLoot defaultLoot = new RuneLoot(rune.getLootChests().stream().map(ResourceLocation::toString)
                        .collect(Collectors.toList()), rune.getWorldgenChance());
                RuneConfig defaultConfig = new RuneConfig(defaultIngredients, defaultLoot);
                Path path = dir.resolve(rune.getRegistryName().getPath() + "." + "json");

                if (!Files.exists(path)) {
                    Writer writer = Files.newBufferedWriter(path);

                    SERIALIZER.toJson(defaultConfig, writer);

                    writer.flush();
                    writer.close();
                }

                Reader reader = Files.newBufferedReader(path);
                RuneConfig config = SERIALIZER.fromJson(reader, RuneConfig.class);
                List<Item> ingredients = config.getIngredients().getIngredients().stream().map(location -> {
                    String[] pair = location.split(":");
                    return ForgeRegistries.ITEMS.getValue(new ResourceLocation(pair[0], pair[1]));
                }).collect(Collectors.toList());
                RuneLoot loot = config.getLoot();

                RelicUtils.Crafting.INGREDIENTS.put(rune, ingredients);
                RelicUtils.RunesWorldgen.LOOT.put(rune, loot);

                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}