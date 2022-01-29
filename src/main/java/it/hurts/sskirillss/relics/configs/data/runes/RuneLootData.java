package it.hurts.sskirillss.relics.configs.data.runes;

import lombok.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuneLootData {
    private List<String> table = Arrays.asList(
            BuiltInLootTables.UNDERWATER_RUIN_BIG.toString(),
            BuiltInLootTables.UNDERWATER_RUIN_SMALL.toString(),
            BuiltInLootTables.SHIPWRECK_TREASURE.toString(),
            BuiltInLootTables.NETHER_BRIDGE.toString(),
            BuiltInLootTables.BASTION_BRIDGE.toString(),
            BuiltInLootTables.BASTION_OTHER.toString(),
            BuiltInLootTables.BASTION_TREASURE.toString(),
            BuiltInLootTables.BASTION_HOGLIN_STABLE.toString(),
            BuiltInLootTables.RUINED_PORTAL.toString(),
            BuiltInLootTables.IGLOO_CHEST.toString(),
            BuiltInLootTables.DESERT_PYRAMID.toString(),
            BuiltInLootTables.STRONGHOLD_CORRIDOR.toString(),
            BuiltInLootTables.STRONGHOLD_CROSSING.toString(),
            BuiltInLootTables.STRONGHOLD_LIBRARY.toString(),
            BuiltInLootTables.ABANDONED_MINESHAFT.toString()
    );
    
    private double chance = 0.05D;
}