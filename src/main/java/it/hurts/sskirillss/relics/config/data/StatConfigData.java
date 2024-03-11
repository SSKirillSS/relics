package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatConfigData {
    private double minInitialValue;
    private double maxInitialValue;

    private double minThresholdValue;
    private double maxThresholdValue;

    private UpgradeOperation upgradeOperation;
    private double upgradeModifier;
}