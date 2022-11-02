package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import it.unimi.dsi.fastutil.Pair;
import lombok.Builder;
import lombok.Data;

import java.util.function.Function;

@Data
@Builder
public class RelicAbilityStat {
    private Pair<Operation, Double> upgradeModifier;
    private Pair<Double, Double> initialValue;
    private Pair<Double, Double> thresholdValue;

    @Builder.Default
    private Function<Double, ? extends Number> formatValue = Double::doubleValue;

    public enum Operation {
        ADD,
        MULTIPLY_BASE,
        MULTIPLY_TOTAL;

        public static Operation getByName(String name) {
            return Operation.valueOf(name.toUpperCase());
        }
    }

    public static class RelicAbilityStatBuilder {
        public RelicAbilityStatBuilder initialValue(double min, double max) {
            initialValue = Pair.of(min, max);

            return this;
        }

        public RelicAbilityStatBuilder thresholdValue(double min, double max) {
            thresholdValue = Pair.of(min, max);

            return this;
        }

        public RelicAbilityStatBuilder upgradeModifier(Operation operation, double step) {
            upgradeModifier = Pair.of(operation, step);

            return this;
        }

        public RelicAbilityStatBuilder upgradeModifier(String operation, double step) {
            upgradeModifier = Pair.of(Operation.getByName(operation), step);

            return this;
        }
    }
}