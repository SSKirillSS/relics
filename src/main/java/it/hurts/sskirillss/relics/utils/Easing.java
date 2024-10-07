package it.hurts.sskirillss.relics.utils;

public class Easing {
    public static float linear(float progress) {
        return progress;
    }

    public static float easeInSine(float progress) {
        return (float) (1 - Math.cos((progress * Math.PI) / 2));
    }

    public static float easeOutSine(float progress) {
        return (float) Math.sin((progress * Math.PI) / 2);
    }

    public static float easeInOutSine(float progress) {
        return (float) -(Math.cos(Math.PI * progress) - 1) / 2;
    }

    public static float easeInQuad(float progress) {
        return progress * progress;
    }

    public static float easeOutQuad(float progress) {
        return 1 - (1 - progress) * (1 - progress);
    }

    public static float easeInOutQuad(float progress) {
        return progress < 0.5 ? 2 * progress * progress : 1 - (float) Math.pow(-2 * progress + 2, 2) / 2;
    }

    public static float easeInCubic(float progress) {
        return progress * progress * progress;
    }

    public static float easeOutCubic(float progress) {
        return 1 - (float) Math.pow(1 - progress, 3);
    }

    public static float easeInOutCubic(float progress) {
        return progress < 0.5 ? 4 * progress * progress * progress : 1 - (float) Math.pow(-2 * progress + 2, 3) / 2;
    }

    public static float easeInQuart(float progress) {
        return progress * progress * progress * progress;
    }

    public static float easeOutQuart(float progress) {
        return 1 - (float) Math.pow(1 - progress, 4);
    }

    public static float easeInOutQuart(float progress) {
        return progress < 0.5 ? 8 * progress * progress * progress * progress : 1 - (float) Math.pow(-2 * progress + 2, 4) / 2;
    }

    public static float easeInQuint(float progress) {
        return progress * progress * progress * progress * progress;
    }

    public static float easeOutQuint(float progress) {
        return 1 - (float) Math.pow(1 - progress, 5);
    }

    public static float easeInOutQuint(float progress) {
        return progress < 0.5 ? 16 * progress * progress * progress * progress * progress : 1 - (float) Math.pow(-2 * progress + 2, 5) / 2;
    }

    public static float easeInExpo(float progress) {
        return progress == 0 ? 0 : (float) Math.pow(2, 10 * progress - 10);
    }

    public static float easeOutExpo(float progress) {
        return progress == 1 ? 1 : 1 - (float) Math.pow(2, -10 * progress);
    }

    public static float easeInOutExpo(float progress) {
        if (progress == 0) return 0;
        if (progress == 1) return 1;
        return progress < 0.5 ? (float) Math.pow(2, 20 * progress - 10) / 2 : (2 - (float) Math.pow(2, -20 * progress + 10)) / 2;
    }

    public static float easeInCirc(float progress) {
        return 1 - (float) Math.sqrt(1 - Math.pow(progress, 2));
    }

    public static float easeOutCirc(float progress) {
        return (float) Math.sqrt(1 - Math.pow(progress - 1, 2));
    }

    public static float easeInOutCirc(float progress) {
        return progress < 0.5
                ? (1 - (float) Math.sqrt(1 - Math.pow(2 * progress, 2))) / 2
                : ((float) Math.sqrt(1 - Math.pow(-2 * progress + 2, 2)) + 1) / 2;
    }

    public static float easeInBack(float progress) {
        final float c1 = 1.70158f;
        final float c3 = c1 + 1;
        return c3 * progress * progress * progress - c1 * progress * progress;
    }

    public static float easeOutBack(float progress) {
        final float c1 = 1.70158f;
        final float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(progress - 1, 3) + c1 * (float) Math.pow(progress - 1, 2);
    }

    public static float easeInOutBack(float progress) {
        final float c1 = 1.70158f;
        final float c2 = c1 * 1.525f;
        return progress < 0.5
                ? (float) (Math.pow(2 * progress, 2) * ((c2 + 1) * 2 * progress - c2)) / 2
                : (float) (Math.pow(2 * progress - 2, 2) * ((c2 + 1) * (progress * 2 - 2) + c2) + 2) / 2;
    }

    public static float easeInElastic(float progress) {
        final float c4 = (2 * (float) Math.PI) / 3;
        return progress == 0 ? 0 : progress == 1 ? 1 : -(float) Math.pow(2, 10 * progress - 10) * (float) Math.sin((progress * 10 - 10.75) * c4);
    }

    public static float easeOutElastic(float progress) {
        final float c4 = (2 * (float) Math.PI) / 3;
        return progress == 0 ? 0 : progress == 1 ? 1 : (float) Math.pow(2, -10 * progress) * (float) Math.sin((progress * 10 - 0.75) * c4) + 1;
    }

    public static float easeInOutElastic(float progress) {
        final float c5 = (2 * (float) Math.PI) / 4.5F;
        return progress == 0 ? 0 : progress == 1 ? 1 : progress < 0.5
                ? -(float) (Math.pow(2, 20 * progress - 10) * Math.sin((20 * progress - 11.125) * c5)) / 2
                : (float) (Math.pow(2, -20 * progress + 10) * Math.sin((20 * progress - 11.125) * c5)) / 2 + 1;
    }

    public static float easeInBounce(float progress) {
        return 1 - easeOutBounce(1 - progress);
    }

    public static float easeOutBounce(float progress) {
        final float n1 = 7.5625f;
        final float d1 = 2.75f;
        if (progress < 1 / d1) {
            return n1 * progress * progress;
        } else if (progress < 2 / d1) {
            return n1 * (progress -= 1.5f / d1) * progress + 0.75f;
        } else if (progress < 2.5 / d1) {
            return n1 * (progress -= 2.25f / d1) * progress + 0.9375f;
        } else {
            return n1 * (progress -= 2.625f / d1) * progress + 0.984375f;
        }
    }

    public static float easeInOutBounce(float progress) {
        return progress < 0.5
                ? (1 - easeOutBounce(1 - 2 * progress)) / 2
                : (1 + easeOutBounce(2 * progress - 1)) / 2;
    }
}