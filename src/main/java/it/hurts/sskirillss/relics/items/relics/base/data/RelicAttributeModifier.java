package it.hurts.sskirillss.relics.items.relics.base.data;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

@Data
@Builder
public class RelicAttributeModifier {
    @Singular("attribute")
    List<Modifier> attributes;

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