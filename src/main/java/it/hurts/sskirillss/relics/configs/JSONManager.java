package it.hurts.sskirillss.relics.configs;

import com.google.gson.*;
import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.configs.variables.worldgen.RelicLoot;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class JSONManager {
    private static final Gson SERIALIZER = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

    public static void setupJSONConfig() {
        try {
            Path dir = FMLPaths.CONFIGDIR.get().resolve("relics").resolve("stats");
            Files.createDirectories(dir);
            for (RegistryObject<Item> registryObject : ItemRegistry.ITEMS.getEntries()) {
                if (!registryObject.isPresent()) continue;
                Item item = registryObject.get();
                if (!(item instanceof RelicItem)) continue;
                RelicItem relic = (RelicItem) item;
                Path path = dir.resolve(item.getRegistryName().getPath() + "." + "json");
                RelicLevel defaultLevel = new RelicLevel(relic.getMaxLevel(), relic.getInitialExp(), relic.getExpRatio());
                RelicLoot defaultLoot = new RelicLoot(relic.getLootChests().stream().map(ResourceLocation::toString)
                        .collect(Collectors.toList()), relic.getWorldgenChance());
                RelicStat stat = new RelicStat(defaultLoot, defaultLevel);
                if (!Files.exists(path)) try (Writer writer = Files.newBufferedWriter(path)) {
                    SERIALIZER.toJson(stat, writer);
                    continue;
                }
                try (Reader reader = Files.newBufferedReader(path)) {
                    stat = SERIALIZER.fromJson(reader, RelicStat.class);
                    RelicLevel level = stat.getLevel();
                    RelicLoot loot = stat.getLoot();
                    if (level == null) level = defaultLevel;
                    if (loot == null) loot = defaultLoot;
                    try (Writer writer = Files.newBufferedWriter(path)) {
                        SERIALIZER.toJson(new RelicStat(loot, level), writer);
                    }
                    RelicUtils.Level.LEVEL.put(relic, level);
                    RelicUtils.Worldgen.LOOT.put(relic, loot);
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}