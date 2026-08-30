package dev.muxolotl.selas.client.lighting;

/**
 * Pure, engine-independent lighting math for Selas.
 *
 * <p>Nothing in this class touches Minecraft classes, so every method here is
 * deterministic and unit-testable in plain JUnit. Keeping the math separate from
 * {@link SelasLightmap} (which reads config and Minecraft state) lets the tuned
 * night curve, wrapped-range timing, moon-phase response, and color packing be
 * verified without launching the game.
 */
public final class SelasMath {
    public static final float LUMINANCE_R = 0.2126F;
    public static final float LUMINANCE_G = 0.7152F;
    public static final float LUMINANCE_B = 0.0722F;

    public static final float MINECRAFT_DAY_TICKS = 24000.0F;

    private SelasMath() {
    }

    // --- Scalars -----------------------------------------------------------

    public static float saturate(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        return Math.min(value, 1.0F);
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static float luminance(float r, float g, float b) {
        return r * LUMINANCE_R + g * LUMINANCE_G + b * LUMINANCE_B;
    }

    /**
     * Screen-style union of two independent light contributions, so stacking
     * block light and sky light never overshoots full brightness.
     */
    public static float combineLightContributions(float block, float sky) {
        return saturate(block + sky - block * sky);
    }

    public static float positiveModulo(float value, float modulo) {
        float result = value % modulo;
        return result < 0.0F ? result + modulo : result;
    }

    /** Ken Perlin's smootherStep (quintic), with zero first and second derivatives at the ends. */
    public static float smootherStep(float value) {
        float t = saturate(value);
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }

    // --- Wrapped day-tick ranges ------------------------------------------

    public static float wrappedDistance(float start, float end) {
        return positiveModulo(end - start, MINECRAFT_DAY_TICKS);
    }

    public static boolean isInWrappedRange(float value, float start, float end) {
        float length = wrappedDistance(start, end);
        if (length <= 0.0F) {
            return false;
        }
        return wrappedDistance(start, value) <= length;
    }

    public static float progressInWrappedRange(float value, float start, float end) {
        float length = wrappedDistance(start, end);
        if (length <= 0.0F) {
            return 1.0F;
        }
        return saturate(wrappedDistance(start, value) / length);
    }

    // --- Higher-level lighting curves -------------------------------------

    /**
     * Amount of "night" in [0, 1] for a given day tick, given the twilight
     * schedule. 0 is full day, 1 is full night, with smootherStep fades across
     * dusk and dawn. The schedule is assumed already validated/ordered.
     */
    public static float nightAmount(float dayTick, float duskStart, float fullNightStart,
                                    float fullNightEnd, float dawnEnd) {
        if (isInWrappedRange(dayTick, duskStart, fullNightStart)) {
            return smootherStep(progressInWrappedRange(dayTick, duskStart, fullNightStart));
        }
        if (isInWrappedRange(dayTick, fullNightStart, fullNightEnd)) {
            return 1.0F;
        }
        if (isInWrappedRange(dayTick, fullNightEnd, dawnEnd)) {
            return 1.0F - smootherStep(progressInWrappedRange(dayTick, fullNightEnd, dawnEnd));
        }
        return 0.0F;
    }

    /** True when the twilight ticks are in the order Selas expects (wraps past midnight). */
    public static boolean isValidTwilightOrder(int duskStart, int fullNightStart,
                                               int fullNightEnd, int dawnEnd) {
        return dawnEnd < duskStart && duskStart < fullNightStart && fullNightStart < fullNightEnd;
    }

    /** Maps raw moon brightness [0,1] through the tuning curve into a phase progress [0,1]. */
    public static float moonPhaseProgress(float moonBrightness, float moonCurve) {
        return saturate((float) Math.pow(saturate(moonBrightness), moonCurve));
    }

    /**
     * Combined weather darkening multiplier in [0,1]. Rain and thunder do not
     * stack; the stronger of the two wins.
     */
    public static float weatherFactor(float rainLevel, float thunderLevel,
                                      float rainDarkening, float thunderDarkening) {
        float rain = saturate(rainLevel) * rainDarkening;
        float thunder = saturate(thunderLevel) * thunderDarkening;
        return saturate(1.0F - Math.max(rain, thunder));
    }

    /** Effective perceived light at a lightmap cell after block-light preservation and sky scaling. */
    public static float effectiveLight(float block, float sky, float blockLightPreservation, float skyFactor) {
        float blockContribution = saturate(block * blockLightPreservation);
        float skyContribution = saturate(sky * skyFactor);
        return combineLightContributions(blockContribution, skyContribution);
    }

    /** Target (ceiling) luminance for a cell given its effective light and darkness curve. */
    public static float targetLuminance(float effectiveLight, float floor, float darknessCurve, float baseAmbient) {
        float shaped = floor + (1.0F - floor) * (float) Math.pow(saturate(effectiveLight), darknessCurve);
        return saturate(shaped + baseAmbient);
    }

    /**
     * Anti-crush luminance floor for an Overworld/skylit cell: near-black in
     * sealed spaces, lifting toward a faint starlight glow under open sky.
     */
    public static float skylitFloor(float block, float sky, float minimumFloor,
                                    float caveFloor, float starlightFloor, float nightAmount) {
        float totalLight = combineLightContributions(saturate(block), saturate(sky));
        float lowLight = 1.0F - totalLight;
        float base = lerp(lowLight, minimumFloor, Math.min(minimumFloor, caveFloor));

        if (starlightFloor > 0.0F && nightAmount > 0.0F) {
            float openSky = saturate(sky);
            float starlight = starlightFloor * openSky * nightAmount;
            base = Math.max(base, starlight);
        }
        return base;
    }

    /** How strongly to pull a cell toward gray, given its darkness and the desaturation setting. */
    public static float desaturationAmount(float darkness, float nightDesaturation) {
        return (float) Math.pow(saturate(darkness), 1.5F) * nightDesaturation;
    }

    // --- Color packing -----------------------------------------------------

    /**
     * Packs normalized channels into the layout NativeImage lightmap pixels use:
     * RGBA in memory, little-endian, i.e. {@code 0xAABBGGRR}.
     */
    public static int packNativeColor(float a, float r, float g, float b) {
        int ai = Math.round(saturate(a) * 255.0F);
        int ri = Math.round(saturate(r) * 255.0F);
        int gi = Math.round(saturate(g) * 255.0F);
        int bi = Math.round(saturate(b) * 255.0F);
        return (ai << 24) | ri | (gi << 8) | (bi << 16);
    }
}
