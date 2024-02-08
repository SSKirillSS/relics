package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

@Data
@Builder
public class RelicAbilityStat {
    @Builder.Default
    private Pair<Operation, Double> upgradeModifier;
    @Builder.Default
    private Pair<Double, Double> initialValue;
    @Builder.Default
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
        private Pair<Operation, Double> upgradeModifier = Pair.of(Operation.ADD, 0D);
        private Pair<Double, Double> initialValue = Pair.of(0D, 0D);
        private Pair<Double, Double> thresholdValue = Pair.of(Double.MIN_VALUE, Double.MAX_VALUE);

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