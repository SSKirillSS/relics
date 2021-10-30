package it.hurts.sskirillss.relics.configs;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RelicConfig {
    @Getter
    private static final Path rootPath = ConfigHelper.getRootPath()
            .resolve("items")
            .resolve("relics");

    @Getter
    @Setter
    private static Date launchDate;

    public static void setupEverything() {
        setLaunchDate(new Date());

        processConfigs();
    }

    private static void processConfigs() {
        ItemRegistry.getRegisteredRelics().forEach(relic -> {
            RelicConfigData data = readConfig(relic);

            if (data == null || data.getConfig() == null || data.getLoot() == null) {
                Path sourcePath = getRootPath().resolve(relic.getRegistryName().getPath() + ".json");

                if (Files.exists(sourcePath)) {
                    Path backupPath = ConfigHelper.getRootPath()
                            .resolve("backups")
                            .resolve(new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(getLaunchDate()))
                            .resolve(ConfigHelper.getRootPath().relativize(sourcePath)).getParent();

                    try {
                        Files.createDirectories(backupPath);

                        Files.move(sourcePath, backupPath.resolve(relic.getRegistryName().getPath() + ".json"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                writeDefaultConfig(relic);

                data = relic.getData().toConfigData();
            }

            syncRelicData(relic, data);
        });
    }

    @SneakyThrows
    private static void writeDefaultConfig(RelicItem relic) {
        Path path = getRootPath();

        Files.createDirectories(path);

        ConfigHelper.createJSONConfig(path.resolve(relic.getRegistryName().getPath() + ".json"),
                relic.getData().toConfigData());
    }

    @Nullable
    private static RelicConfigData readConfig(RelicItem relic) {
        Path path = getRootPath().resolve(relic.getRegistryName().getPath() + ".json");

        Object data;

        try {
            data = ConfigHelper.readJSONConfig(path, TypeToken.getParameterized(RelicConfigData.class,
                    relic.getData().getConfig()).getType());
        } catch (JsonSyntaxException e) {
            return null;
        }

        if (!(data instanceof RelicConfigData))
            return null;

        return (RelicConfigData<?>) data;
    }

    private static void syncRelicData(RelicItem relic, RelicConfigData data) {
        RelicData relicData = relic.getData();

        relicData.setConfig(data.getConfig().getClass());
        relicData.setLoot(data.getLoot());

        relic.setData(relicData);
        relic.setConfig(data.getConfig());
    }
}