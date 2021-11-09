package it.hurts.sskirillss.relics.configs.data.runes;

import lombok.*;
import net.minecraft.loot.LootTables;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuneLootData {
    private List<String> table = Arrays.asList(
            LootTables.UNDERWATER_RUIN_BIG.toString(),
            LootTables.UNDERWATER_RUIN_SMALL.toString(),
            LootTables.SHIPWRECK_TREASURE.toString(),
            LootTables.NETHER_BRIDGE.toString(),
            LootTables.BASTION_BRIDGE.toString(),
            LootTables.BASTION_OTHER.toString(),
            LootTables.BASTION_TREASURE.toString(),
            LootTables.BASTION_HOGLIN_STABLE.toString(),
            LootTables.RUINED_PORTAL.toString(),
            LootTables.IGLOO_CHEST.toString(),
            LootTables.DESERT_PYRAMID.toString(),
            LootTables.STRONGHOLD_CORRIDOR.toString(),
            LootTables.STRONGHOLD_CROSSING.toString(),
            LootTables.STRONGHOLD_LIBRARY.toString(),
            LootTables.ABANDONED_MINESHAFT.toString()
    );
    
    private double chance = 0.05D;
}