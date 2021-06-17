package it.hurts.sskirillss.relics.configs;

import com.google.gson.*;
import it.hurts.sskirillss.relics.configs.variables.RuneConfig;
import it.hurts.sskirillss.relics.configs.variables.crafting.RuneIngredients;
import it.hurts.sskirillss.relics.configs.variables.durability.RelicDurability;
import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RelicLoot;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RuneLoot;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
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

    public static void setupJSONConfig() {
        setupRelicsConfig();
        setupRunesConfig();
    }

    protected static void setupRelicsConfig() {
        Path dir = FMLPaths.CONFIGDIR.get().resolve("relics").resolve("items").resolve("relics");
        try {
            Files.createDirectories(dir);
            for (RegistryObject<Item> registryObject : ItemRegistry.ITEMS.getEntries()) {
                if (!registryObject.isPresent()) continue;
                Item item = registryObject.get();
                if (!(item instanceof RelicItem)) continue;
                RelicItem<? extends RelicStats> relic = (RelicItem<? extends RelicStats>) item;
                setupRelic(dir, relic);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void setupRunesConfig() {
        Path dir = FMLPaths.CONFIGDIR.get().resolve("relics").resolve("items").resolve("runes");
        try {
            Files.createDirectories(dir);
            for (RegistryObject<Item> registryObject : ItemRegistry.ITEMS.getEntries()) {
                if (!registryObject.isPresent()) continue;
                Item item = registryObject.get();
                if (!(item instanceof RuneItem)) continue;
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

    protected static <T extends RelicStats> void setupRelic(Path dir, RelicItem<T> relic) throws IOException {
        Path path = dir.resolve(relic.getRegistryName().getPath() + "." + "json");
        RelicStats defaultStats = null;
        try {
            defaultStats = relic.getConfigClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RelicLoot defaultLoot = new RelicLoot(relic.getLootChests()
                .stream().map(ResourceLocation::toString).collect(Collectors.toList()), relic.getWorldgenChance());
        RelicLevel defaultLevel = new RelicLevel(relic.getMaxLevel(), relic.getInitialExp(), relic.getExpRatio());
        RelicDurability defaultDurability = new RelicDurability(relic.getDurability());
        SpecificRelicConfig<RelicStats> defaultConfig = new SpecificRelicConfig<>(defaultStats, defaultLoot, defaultLevel, defaultDurability);
        if (!Files.exists(path)) setupDefaultConfig(path, defaultConfig);
        SpecificRelicConfig<T> relicConfig = getConfig(path, relic);
        register(relic, relicConfig);
    }

    protected static <T extends RelicStats> SpecificRelicConfig<T> getConfig(Path path, RelicItem<T> relicItem) {
        SpecificRelicConfig<T> result = null;
        try {
            Reader reader = Files.newBufferedReader(path);
            RelicConfig config = SERIALIZER.fromJson(reader, RelicConfig.class);
            reader.close();
            T stats = SERIALIZER.fromJson(config.getStats(), relicItem.getConfigClass());
            RelicLoot loot = config.getLoot();
            RelicLevel level = config.getLevel();
            RelicDurability durability = config.getDurability();
            result = new SpecificRelicConfig<>(stats, loot, level, durability);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected static <T extends RelicStats> void setupDefaultConfig(Path path, SpecificRelicConfig<T> config) throws IOException {
        RelicStats defaultStats = config.getStats();
        RelicConfig abstractConfig = new RelicConfig((JsonObject) SERIALIZER.toJsonTree(defaultStats, defaultStats.getClass()), config.getLoot(), config.getLevel(), config.getDurability());
        Writer writer = Files.newBufferedWriter(path);
        SERIALIZER.toJson(abstractConfig, writer);
        writer.flush();
        writer.close();
    }

    protected static <T extends RelicStats> void register(RelicItem<T> relicItem, SpecificRelicConfig<T> config) {
        relicItem.setConfig(config.getStats());
        RelicUtils.Worldgen.LOOT.put(relicItem, config.getLoot());
        RelicUtils.Level.LEVEL.put(relicItem, config.getLevel());
        RelicUtils.Durability.DURABILITY.put(relicItem, config.getDurability());
    }
}