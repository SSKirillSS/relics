package it.hurts.sskirillss.relics.config.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.hurts.sskirillss.octolib.config.api.IOctoConfig;
import it.hurts.sskirillss.relics.config.ConfigHelper;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import lombok.Data;

import java.nio.file.Path;

@Data
public class RelicConfigData implements IOctoConfig {
    @Expose
    private final transient RelicItem relic;

    public RelicConfigData(RelicItem relic) {
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
}