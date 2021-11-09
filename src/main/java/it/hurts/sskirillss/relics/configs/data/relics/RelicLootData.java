package it.hurts.sskirillss.relics.configs.data.relics;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelicLootData {
    @Singular("table")
    private List<String> table = new ArrayList<>();

    @Builder.Default
    private float chance = 0.05F;
}