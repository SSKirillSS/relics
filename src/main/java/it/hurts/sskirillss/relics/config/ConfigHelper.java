package it.hurts.sskirillss.relics.config;

import it.hurts.sskirillss.octolib.config.data.OctoConfig;
import it.hurts.sskirillss.octolib.config.storage.ConfigStorage;
import it.hurts.sskirillss.relics.config.data.*;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
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

        // TODO: Split addon configs into separated dir
        Path path = FMLPaths.CONFIGDIR.get().resolve("relics").resolve(ForgeRegistries.ITEMS.getKey(relic.getItem()).getPath() + ".json");

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
        RelicLevelingData levelingData = relicData.getLevelingData();

        if (levelingConfig != null && levelingData != null) {
            levelingData.setMaxLevel(levelingConfig.getMaxLevel());
            levelingData.setStep(levelingConfig.getStep());
            levelingData.setInitialCost(levelingConfig.getInitialCost());
        }

        AbilitiesConfigData abilitiesConfig = relicConfig.getAbilitiesData();
        RelicAbilityData abilityData = relicData.getAbilityData();

        if (abilitiesConfig != null && abilityData != null) {
            for (Map.Entry<String, RelicAbilityEntry> abilityMapEntry : abilityData.getAbilities().entrySet()) {
                AbilityConfigData abilityConfig = abilitiesConfig.getAbilities().get(abilityMapEntry.getKey());

                if (abilityConfig == null)
                    continue;

                RelicAbilityEntry abilityEntry = abilityMapEntry.getValue();

                abilityEntry.setMaxLevel(abilityConfig.getMaxLevel());
                abilityEntry.setRequiredLevel(abilityConfig.getRequiredLevel());
                abilityEntry.setRequiredPoints(abilityConfig.getRequiredPoints());

                for (Map.Entry<String, RelicAbilityStat> statMapEntry : abilityEntry.getStats().entrySet()) {
                    StatConfigData statConfig = abilityConfig.getStats().get(statMapEntry.getKey());

                    if (statConfig == null)
                        continue;

                    RelicAbilityStat statEntry = statMapEntry.getValue();

                    statEntry.setInitialValue(Pair.of(statConfig.getMinInitialValue(), statConfig.getMaxInitialValue()));
                    statEntry.setThresholdValue(Pair.of(statConfig.getMinThresholdValue(), statConfig.getMaxThresholdValue()));
                    statEntry.setUpgradeModifier(Pair.of(statConfig.getUpgradeOperation(), statConfig.getUpgradeModifier()));
                }
            }
        }

        relic.setRelicData(relicData);
    }

    public static void constructConfigs() {
        List<RelicItem> relics = ForgeRegistries.ITEMS.getValues().stream().filter(entry -> entry instanceof IRelicItem).map(entry -> (RelicItem) entry).toList();

        if (relics.isEmpty())
            return;

        for (RelicItem relic : relics)
            constructRelicConfig(relic);
    }

    private static void constructRelicConfig(RelicItem relic) {
        RelicData relicData = relic.getRelicData();

        if (relicData == null)
            return;

        RelicConfigData relicConfig = new RelicConfigData(relic);

        RelicLevelingData levelingData = relicData.getLevelingData();

        if (levelingData != null)
            relicConfig.setLevelingData(new LevelingConfigData(levelingData.getInitialCost(), levelingData.getMaxLevel(), levelingData.getStep()));

        RelicAbilityData abilityData = relicData.getAbilityData();

        if (abilityData != null) {
            AbilitiesConfigData abilitiesConfig = new AbilitiesConfigData();

            for (Map.Entry<String, RelicAbilityEntry> abilityMapEntry : abilityData.getAbilities().entrySet()) {
                RelicAbilityEntry abilityEntry = abilityMapEntry.getValue();

                AbilityConfigData abilityConfig = new AbilityConfigData(abilityEntry.getRequiredPoints(), abilityEntry.getRequiredLevel(), abilityEntry.getMaxLevel());

                for (Map.Entry<String, RelicAbilityStat> statMapEntry : abilityEntry.getStats().entrySet()) {
                    RelicAbilityStat statEntry = statMapEntry.getValue();

                    StatConfigData statConfig = new StatConfigData(statEntry.getInitialValue().getKey(), statEntry.getInitialValue().getValue(),
                            statEntry.getThresholdValue().getKey(), statEntry.getThresholdValue().getValue(),
                            statEntry.getUpgradeModifier().getKey(), statEntry.getUpgradeModifier().getValue());

                    abilityConfig.getStats().put(statMapEntry.getKey(), statConfig);
                }

                abilitiesConfig.getAbilities().put(abilityMapEntry.getKey(), abilityConfig);
            }

            relicConfig.setAbilitiesData(abilitiesConfig);
        }

        relicConfig.setup();
    }
}