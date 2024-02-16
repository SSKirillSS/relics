package it.hurts.sskirillss.relics.config.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class LootConfigData {
    private Map<String, Float> entries = new HashMap<>();
}