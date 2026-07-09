package com.selas.lighting.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * NeoForge client config for Selas. Registered as a {@code CLIENT} config in {@link com.selas.Selas}.
 *
 * <p>Phase 1 options are active. {@code enableColorTemperature} and {@code enableEyeAdaptation} are
 * placeholders for later phases (currently unused by the engine).
 */
public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // Dimensions
    public static final ModConfigSpec.BooleanValue DARK_OVERWORLD;
    public static final ModConfigSpec.BooleanValue DARK_NETHER;
    public static final ModConfigSpec.BooleanValue DARK_END;
    public static final ModConfigSpec.BooleanValue DARK_DEFAULT;
    public static final ModConfigSpec.BooleanValue DARK_SKYLESS;
    public static final ModConfigSpec.DoubleValue DARK_NETHER_FOG;
    public static final ModConfigSpec.DoubleValue DARK_END_FOG;

    // Behavior
    public static final ModConfigSpec.BooleanValue BLOCK_LIGHT_ONLY;
    public static final ModConfigSpec.BooleanValue IGNORE_MOON_PHASE;
    public static final ModConfigSpec.BooleanValue ENABLE_COLOR_TEMPERATURE;
    public static final ModConfigSpec.BooleanValue ENABLE_EYE_ADAPTATION;

    static {
        BUILDER.comment("Per-dimension realistic darkness").push("dimensions");
        DARK_OVERWORLD = BUILDER.comment("Make the Overworld dark at night (remove minimum light).").define("darkOverworld", true);
        DARK_NETHER = BUILDER.comment("Make the Nether dark.").define("darkNether", true);
        DARK_END = BUILDER.comment("Make the End dark.").define("darkEnd", true);
        DARK_DEFAULT = BUILDER.comment("Make other sky-light dimensions dark by default.").define("darkDefault", true);
        DARK_SKYLESS = BUILDER.comment("Make other sky-less dimensions dark by default.").define("darkSkyless", true);
        DARK_NETHER_FOG = BUILDER.comment("Nether fog brightness (0 = pitch black fog, 1 = vanilla).").defineInRange("darkNetherFog", 0.5, 0.0, 1.0);
        DARK_END_FOG = BUILDER.comment("End fog brightness (0 = pitch black fog, 1 = vanilla).").defineInRange("darkEndFog", 0.0, 0.0, 1.0);
        BUILDER.pop();

        BUILDER.comment("Behavior").push("behavior");
        BLOCK_LIGHT_ONLY = BUILDER.comment("Only affect block light; leave sky light untouched.").define("blockLightOnly", false);
        IGNORE_MOON_PHASE = BUILDER.comment("Ignore moon phase when computing night brightness.").define("ignoreMoonPhase", false);
        ENABLE_COLOR_TEMPERATURE = BUILDER.comment("[Phase 2, unused] Tint light by source color temperature.").define("enableColorTemperature", false);
        ENABLE_EYE_ADAPTATION = BUILDER.comment("[Phase 3, unused] Temporal eye adaptation.").define("enableEyeAdaptation", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
