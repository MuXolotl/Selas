package dev.muxolotl.selas.client.lighting;

/**
 * Named bundles of the "look" settings, so players can pick a mood in one click
 * instead of tuning ~20 sliders.
 *
 * <p>{@link #CUSTOM} means "use the individual slider values from the config".
 * Every other preset overrides only the visual look values (brightness, color,
 * weather, and per-dimension ambient); it deliberately does NOT touch day/night
 * timing or the compatibility toggles, which stay under manual control.
 *
 * <p>This class is engine-independent (no Minecraft/NeoForge types), so the
 * preset values and resolution logic are unit-testable in plain JUnit.
 * {@link #BALANCED} intentionally matches the mod's default slider values.
 */
public enum SelasPreset {
    /** Use the individual config sliders as-is. */
    CUSTOM(null),

    /** Gentle: closest to vanilla, just a touch moodier. Easiest to see by. */
    VANILLA_PLUS(new Look(
            0.13F, 0.45F, 0.70F, 0.12F, 0.24F,
            0.015F, 0.010F, 0.028F, 1.35F, 1.10F,
            0.22F, 0.05F, 0.04F,
            0.20F, 0.05F, 0.14F, 0.04F, 0.16F)),

    /** The tuned default look (equal to the shipped slider defaults). */
    BALANCED(new Look(
            0.075F, 0.34F, 0.65F, 0.16F, 0.32F,
            0.010F, 0.004F, 0.016F, 1.75F, 1.00F,
            0.38F, 0.07F, 0.03F,
            0.14F, 0.06F, 0.09F, 0.05F, 0.10F)),

    /** Darker, cooler, stormier: nights and caves demand a light source. */
    REALISTIC(new Look(
            0.055F, 0.30F, 0.60F, 0.18F, 0.36F,
            0.008F, 0.0025F, 0.014F, 2.05F, 0.95F,
            0.48F, 0.10F, 0.03F,
            0.11F, 0.07F, 0.075F, 0.06F, 0.08F)),

    /** Oppressive: near-black sealed spaces and heavy, desaturated gloom. */
    HORROR(new Look(
            0.03F, 0.17F, 0.55F, 0.22F, 0.42F,
            0.004F, 0.0008F, 0.008F, 2.70F, 0.85F,
            0.70F, 0.16F, 0.02F,
            0.07F, 0.08F, 0.045F, 0.08F, 0.05F));

    private final Look look;

    SelasPreset(Look look) {
        this.look = look;
    }

    /** True for {@link #CUSTOM}, where individual sliders drive the look. */
    public boolean isCustom() {
        return look == null;
    }

    /**
     * The look values this preset forces. Never call for {@link #CUSTOM}
     * (guard with {@link #isCustom()} first).
     */
    public Look look() {
        if (look == null) {
            throw new IllegalStateException("CUSTOM has no preset look; read individual config values instead");
        }
        return look;
    }

    /**
     * The frozen "look" values a non-custom preset applies. Field order and units
     * mirror the corresponding config entries so a preset maps one-to-one onto
     * them. Timing and compatibility settings are intentionally absent.
     */
    public record Look(
            float moonlessNightSkyFactor,
            float fullMoonSkyFactor,
            float moonPhaseCurve,
            float rainDarkening,
            float thunderDarkening,
            float minimumLuminanceFloor,
            float caveLuminanceFloor,
            float starlightLuminanceFloor,
            float darknessCurve,
            float blockLightPreservation,
            float nightDesaturation,
            float nightCoolTint,
            float moonWarmth,
            float netherLightFactor,
            float netherWarmTint,
            float endLightFactor,
            float endCoolTint,
            float skylessDimensionLightFactor
    ) {
    }
}
