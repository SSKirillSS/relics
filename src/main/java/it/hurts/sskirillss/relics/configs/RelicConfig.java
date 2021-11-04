package it.hurts.sskirillss.relics.configs;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import it.hurts.sskirillss.relics.configs.data.ConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import lombok.SneakyThrows;
import net.minecraftforge.fml.loading.FMLPaths;

public class RelicConfig {
    @SneakyThrows
    public static void setupConfig() {
        FileConfig config = FileConfig.of(FMLPaths.CONFIGDIR.get().resolve("relics.toml"));
        ObjectConverter converter = new ObjectConverter();

        config.load();

        for (RelicItem<?> relic : ItemRegistry.getRegisteredRelics()) {
            String path = relic.getRegistryName().getPath();
            Config entry = config.get(path);

            ConfigData<?> data;

            if (entry == null) {
                data = relic.getConfigData();

                config.set(path, converter.toConfig(data, Config::inMemory));
            } else
                data = converter.toObject(entry, ConfigData::new);

            //relic.setConfig(data);
        }

        config.save();
        config.close();
    }
}