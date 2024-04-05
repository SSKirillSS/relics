package it.hurts.sskirillss.relics.system.casts.abilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbilityCache {
    private Map<String, Boolean> predicates = new HashMap<>();

    private int iconShakeDelta = 0;
}