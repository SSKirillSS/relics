package it.hurts.sskirillss.relics.configs.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LootData {
    @Singular("table")
    private List<String> table = new ArrayList<>();

    @Builder.Default
    private double chance = 0.05D;
}