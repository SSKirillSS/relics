package it.hurts.sskirillss.relics.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.hurts.sskirillss.relics.configs.variables.level.RelicLevel;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONManager {
    private static final Gson SERIALIZER = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

    public static void setupLevelingConfig() {
        try {
            Path path = FMLPaths.CONFIGDIR.get().resolve("relics").resolve("stats");
            Files.createDirectories(path);
            for (RegistryObject<Item> object : ItemRegistry.ITEMS.getEntries()) {
                if (!object.isPresent()) continue;
                Item item = object.get();
                if (!(item instanceof RelicItem)) continue;
                RelicItem relic = (RelicItem) item;
                RelicLevel level = new RelicLevel(relic.getMaxLevel(), relic.getInitialExp(), relic.getExpRatio());
                RelicUtils.Level.LEVEL.put(relic, level);
                try (Writer writer = Files.newBufferedWriter(path.resolve(item.getRegistryName().getPath() + ".json"))) {
                    SERIALIZER.toJson(level, writer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}