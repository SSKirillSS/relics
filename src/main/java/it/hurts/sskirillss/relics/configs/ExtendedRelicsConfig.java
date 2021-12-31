package it.hurts.sskirillss.relics.configs;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.configs.data.runes.RuneConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ExtendedRelicsConfig {
    private static Date launchDate;

    public static void setupExtendedConfigs() {
        if (!RelicsConfig.ENABLE_EXTENDED_CONFIG.get())
            return;

        launchDate = new Date();

        setupRelicsConfigs();
        setupRunesConfigs();
    }

    private static void setupRelicsConfigs() {
        Path rootPath = ConfigHelper.getRootPath()
                .resolve("items")
                .resolve("relics");

        ItemRegistry.getRegisteredRelics().forEach(relic -> {
            String path = Objects.requireNonNull(relic.getRegistryName()).getPath() + ".json";

            RelicConfigData<?> data;

            try {
                data = (RelicConfigData<?>) ConfigHelper.readJSONConfig(rootPath.resolve(path), TypeToken.getParameterized(RelicConfigData.class,
                        relic.getConfigData().getStats() == null ? RelicStats.class :relic.getConfigData().getStats().getClass()).getType());
            } catch (JsonSyntaxException e) {
                data = null;
            }

            if (data == null) {
                Path sourcePath = rootPath.resolve(path);

                if (Files.exists(sourcePath)) {
                    Path backupPath = rootPath
                            .resolve("backups")
                            .resolve(new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(launchDate))
                            .resolve(rootPath.relativize(sourcePath)).getParent();

                    try {
                        Files.createDirectories(backupPath);

                        Files.move(sourcePath, backupPath.resolve(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Files.createDirectories(rootPath);

                    ConfigHelper.createJSONConfig(sourcePath, relic.getConfigData());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                data = relic.getConfigData();
            }

            relic.setConfig(data);
        });
    }

    private static void setupRunesConfigs() {
        Path rootPath = ConfigHelper.getRootPath()
                .resolve("items")
                .resolve("runes");

        ItemRegistry.getRegisteredRunes().forEach(rune -> {
            String path = Objects.requireNonNull(rune.getRegistryName()).getPath() + ".json";

            RuneConfigData data;

            try {
                data = (RuneConfigData) ConfigHelper.readJSONConfig(rootPath.resolve(path), RuneConfigData.class);
            } catch (JsonSyntaxException e) {
                data = null;
            }

            if (data == null) {
                Path sourcePath = rootPath.resolve(path);

                if (Files.exists(sourcePath)) {
                    Path backupPath = rootPath
                            .resolve("backups")
                            .resolve(new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(launchDate))
                            .resolve(rootPath.relativize(sourcePath)).getParent();

                    try {
                        Files.createDirectories(backupPath);

                        Files.move(sourcePath, backupPath.resolve(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Files.createDirectories(rootPath);

                    ConfigHelper.createJSONConfig(sourcePath, rune.getConfigData());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                data = rune.getConfigData();
            }

            rune.setConfig(data);
        });
    }
}