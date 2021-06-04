package it.hurts.sskirillss.relics.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
            for (RegistryObject<Item> object : ItemRegistry.ITEMS.getEntries()) {
                if (!object.isPresent()) continue;
                Item item = object.get();
                if (!(item instanceof RelicItem)) continue;
                RelicItem relic = (RelicItem) item;
                RelicStat stat = new RelicStat(new RelicLoot(relic.getLootChests().stream().map(ResourceLocation::toString).collect(Collectors.toList()),
                        relic.getWorldgenChance()), new RelicLevel(relic.getMaxLevel(), relic.getInitialExp(), relic.getExpRatio()));
                RelicUtils.Level.LEVEL.put(relic, stat.getLevel());
                RelicUtils.Worldgen.LOOT.put(relic, stat.getLoot());
                try (Writer writer = Files.newBufferedWriter(dir.resolve(item.getRegistryName().getPath() + ".json"))) {
                    SERIALIZER.toJson(stat, writer);
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}