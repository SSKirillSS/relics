package it.hurts.sskirillss.relics.items.relics.base.data;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

@Data
@Builder
public class RelicAttribute {
    @Singular("attribute")
    List<Modifier> attributes;

    @Singular("slot")
    List<MutablePair<String, Integer>> slots;

    @Data
    public static class Modifier {
        private final Attribute attribute;
        private final float multiplier;
        private final AttributeModifier.Operation operation;

        public Modifier(Attribute attribute, float multiplier, AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.multiplier = multiplier;
            this.operation = operation;
        }

        public Modifier(Attribute attribute, float multiplier) {
            this.attribute = attribute;
            this.multiplier = multiplier;
            this.operation = AttributeModifier.Operation.MULTIPLY_TOTAL;
        }
    }
}