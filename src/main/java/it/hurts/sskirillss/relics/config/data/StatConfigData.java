package it.hurts.sskirillss.relics.config.data;

import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatConfigData {
    private double minInitialValue;
    private double maxInitialValue;

    private RelicAbilityStat.Operation upgradeOperation;
    private double upgradeModifier;
}