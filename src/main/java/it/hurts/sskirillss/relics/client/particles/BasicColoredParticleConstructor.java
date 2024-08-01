package it.hurts.sskirillss.relics.client.particles;

import lombok.Builder;
import lombok.Data;

import java.awt.*;

@Data
@Builder
public class BasicColoredParticleConstructor {
    @Builder.Default
    private Color color;

    @Builder.Default
    private float diameter = 1F;

    @Builder.Default
    private float roll = 0F;

    @Builder.Default
    private boolean physical = true;

    @Builder.Default
    private int lifetime = 20;

    @Builder.Default
    private float scaleModifier = 1F;

    public static class BasicColoredParticleConstructorBuilder {
        private Color color = new Color(0xFFFFFFFF, true);

        public BasicColoredParticleConstructorBuilder color(int color) {
            this.color = new Color(color, true);

            return this;
        }

        public BasicColoredParticleConstructorBuilder color(float r, float g, float b, float a) {
            return this.color(new Color(r, g, b, a).getRGB());
        }

        public BasicColoredParticleConstructorBuilder color(float r, float g, float b) {
            return this.color(r, g, b, 1F);
        }

        public BasicColoredParticleConstructorBuilder color(int r, int g, int b, int a) {
            return this.color(r / 255F, g / 255F, b / 255F, a / 255F);
        }

        public BasicColoredParticleConstructorBuilder color(int r, int g, int b) {
            return this.color(r, g, b, 0xFF);
        }
    }
}
