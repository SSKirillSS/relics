package it.hurts.sskirillss.relics.config.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.hurts.sskirillss.octolib.config.api.IOctoConfig;
import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import lombok.Data;

import java.nio.file.Path;

@Data
public class RelicConfigData implements IOctoConfig {
    @Expose
    private final transient IRelicItem relic;

    public RelicConfigData(IRelicItem relic) {
        this.relic = relic;
    }

    @Override
    public Path getPath() {
        return ConfigHelper.getPath(relic);
    }

    @SerializedName("ability")
    private AbilitiesConfigData abilitiesData;

    @SerializedName("leveling")
    private LevelingConfigData levelingData;

    @SerializedName("loot")
    private LootConfigData lootData;

    @SerializedName("style")
    private StyleConfigData styleData;
}