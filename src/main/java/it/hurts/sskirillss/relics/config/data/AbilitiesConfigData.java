package it.hurts.sskirillss.relics.config.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class AbilitiesConfigData {
    private Map<String, AbilityConfigData> abilities = new HashMap<>();
}