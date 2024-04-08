package it.hurts.sskirillss.relics.config;

import it.hurts.sskirillss.octolib.config.data.OctoConfig;
import it.hurts.sskirillss.octolib.config.storage.ConfigStorage;
import it.hurts.sskirillss.relics.config.data.*;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollection;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHelper {
    public static Map<IRelicItem, Path> CACHE = new HashMap<>();

    public static OctoConfig getRelicConfig(IRelicItem relic) {
        return ConfigStorage.get(getPath(relic));
    }

    public static Path getPath(IRelicItem relic) {
        if (ConfigHelper.CACHE.containsKey(relic))
            return ConfigHelper.CACHE.get(relic);

        Path path = FMLPaths.CONFIGDIR.get().resolve(ForgeRegistries.ITEMS.getKey(relic.getItem()).getNamespace()).resolve(ForgeRegistries.ITEMS.getKey(relic.getItem()).getPath() + ".json");

        ConfigHelper.CACHE.put(relic, path);

        return path;
    }

    public static void setupConfigs() {
        ConfigHelper.constructConfigs();
        ConfigHelper.readConfigs();
    }

    public static void readConfigs() {
        List<IRelicItem> relics = ForgeRegistries.ITEMS.getValues().stream().filter(entry -> entry instanceof IRelicItem).map(entry -> (IRelicItem) entry).toList();

        if (relics.isEmpty())
            return;

        for (IRelicItem relic : relics)
            readRelicConfig(relic);
    }

    private static void readRelicConfig(IRelicItem relic) {
        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        OctoConfig config = getRelicConfig(relic);

        if (config == null || !(config.getConstructor() instanceof RelicConfigData))
            return;

        config.loadFromFile();

        RelicConfigData relicConfig = config.get("$", RelicConfigData.class);

        LevelingConfigData levelingConfig = relicConfig.getLevelingData();
        LevelingData levelingData = relicData.getLeveling();

        if (levelingConfig != null && levelingData != null) {
            levelingData.setMaxLevel(levelingConfig.getMaxLevel());
            levelingData.setStep(levelingConfig.getStep());
            levelingData.setInitialCost(levelingConfig.getInitialCost());
        }

        AbilitiesConfigData abilitiesConfig = relicConfig.getAbilitiesData();

        if (abilitiesConfig != null) {
            for (Map.Entry<String, AbilityData> abilityMapEntry : relicData.getAbilities().getAbilities().entrySet()) {
                AbilityConfigData abilityConfig = abilitiesConfig.getAbilities().get(abilityMapEntry.getKey());

                if (abilityConfig == null)
                    continue;

                AbilityData abilityEntry = abilityMapEntry.getValue();

                abilityEntry.setMaxLevel(abilityConfig.getMaxLevel());
                abilityEntry.setRequiredLevel(abilityConfig.getRequiredLevel());
                abilityEntry.setRequiredPoints(abilityConfig.getRequiredPoints());

                for (Map.Entry<String, StatData> statMapEntry : abilityEntry.getStats().entrySet()) {
                    StatConfigData statConfig = abilityConfig.getStats().get(statMapEntry.getKey());

                    if (statConfig == null)
                        continue;

                    StatData statEntry = statMapEntry.getValue();

                    statEntry.setInitialValue(Pair.of(statConfig.getMinInitialValue(), statConfig.getMaxInitialValue()));
                    statEntry.setThresholdValue(Pair.of(statConfig.getMinThresholdValue(), statConfig.getMaxThresholdValue()));
                    statEntry.setUpgradeModifier(Pair.of(statConfig.getUpgradeOperation(), statConfig.getUpgradeModifier()));
                }
            }
        }

        relicData.getLoot().setCollection(LootCollection.builder()
                .entries(relicConfig.getLootData().getEntries())
                .build());

        relicData.getStyle().setBackground(relicConfig.getStyleData().getBackground());

        relic.setRelicData(relicData);
    }

    public static void constructConfigs() {
        List<IRelicItem> relics = ForgeRegistries.ITEMS.getValues().stream().filter(entry -> entry instanceof IRelicItem).map(entry -> (IRelicItem) entry).toList();

        if (relics.isEmpty())
            return;

        for (IRelicItem relic : relics)
            constructRelicConfig(relic);
    }

    private static void constructRelicConfig(IRelicItem relic) {
        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        RelicConfigData relicConfig = new RelicConfigData(relic);

        LevelingData levelingData = relicData.getLeveling();

        if (levelingData != null)
            relicConfig.setLevelingData(new LevelingConfigData(levelingData.getInitialCost(), levelingData.getMaxLevel(), levelingData.getStep()));

        AbilitiesConfigData abilitiesConfig = new AbilitiesConfigData();

        for (Map.Entry<String, AbilityData> abilityMapEntry : relicData.getAbilities().getAbilities().entrySet()) {
            AbilityData abilityEntry = abilityMapEntry.getValue();

            AbilityConfigData abilityConfig = new AbilityConfigData(abilityEntry.getRequiredPoints(), abilityEntry.getRequiredLevel(), abilityEntry.getMaxLevel());

            for (Map.Entry<String, StatData> statMapEntry : abilityEntry.getStats().entrySet()) {
                StatData statEntry = statMapEntry.getValue();

                StatConfigData statConfig = new StatConfigData(statEntry.getInitialValue().getKey(), statEntry.getInitialValue().getValue(),
                        statEntry.getThresholdValue().getKey(), statEntry.getThresholdValue().getValue(),
                        statEntry.getUpgradeModifier().getKey(), statEntry.getUpgradeModifier().getValue());

                abilityConfig.getStats().put(statMapEntry.getKey(), statConfig);
            }

            abilitiesConfig.getAbilities().put(abilityMapEntry.getKey(), abilityConfig);
        }

        relicConfig.setAbilitiesData(abilitiesConfig);

        LootConfigData lootConfigData = new LootConfigData();

        lootConfigData.setEntries(relicData.getLoot().getCollection().getEntries());

        relicConfig.setLootData(lootConfigData);

        relicConfig.setStyleData(new StyleConfigData(relicData.getStyle().getBackground()));

        relicConfig.setup();
    }
}