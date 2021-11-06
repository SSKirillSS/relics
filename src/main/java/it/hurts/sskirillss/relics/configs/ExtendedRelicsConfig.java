package it.hurts.sskirillss.relics.configs;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import it.hurts.sskirillss.relics.configs.data.ConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import lombok.SneakyThrows;
import net.minecraftforge.fml.loading.FMLPaths;

import java.lang.reflect.Field;

public class ExtendedRelicsConfig {
    private static final FileConfig config = FileConfig.of(FMLPaths.CONFIGDIR.get().resolve("relics-extended.toml"));
    private static final ObjectConverter converter = new ObjectConverter();

    @SneakyThrows
    public static void setupConfig() {
        if (!RelicsConfig.ENABLE_EXTENDED_CONFIG.get())
            return;

        config.load();

        for (RelicItem<?> relic : ItemRegistry.getRegisteredRelics()) {
            String path = "relics." + relic.getRegistryName().getPath();
            Config entry = config.get(path);

            ConfigData<?> defaultConfig = relic.getConfigData();

            if (defaultConfig == null)
                continue;

            RelicStats stats = defaultConfig.getStats();
            ConfigData<?> data;

            if (entry == null || entry.isEmpty()) {
                data = defaultConfig;

                config.set(path, converter.toConfig(data, Config::inMemory));
            } else {
                data = converter.toObject(entry, ConfigData::new);

                for (Field field : stats.getClass().getDeclaredFields()) {
                    String target = path + ".stats." + field.getName();

                    try {
                        Class<?> type = field.getType();

                        if (type == Double.class || type == double.class) {
                            field.setDouble(stats, config.getOrElse(target, field.getDouble(stats)));
                        } else if (type == Float.class || type == float.class) {
                            field.setFloat(stats, config.getOrElse(target, field.getFloat(stats)));
                        } else {
                            field.set(stats, config.get(target));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                data.setStats(stats);
            }

            relic.setConfig(data);
        }

        config.save();
        config.close();
    }
}