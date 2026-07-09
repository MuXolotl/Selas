package dev.muxolotl.selas.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SelasClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.BooleanValue AFFECT_OVERWORLD;
    public static final ModConfigSpec.BooleanValue AFFECT_CUSTOM_SKY_DIMENSIONS;
    public static final ModConfigSpec.BooleanValue AFFECT_SKYLESS_DIMENSIONS;
    public static final ModConfigSpec.BooleanValue RESPECT_NIGHT_VISION;
    public static final ModConfigSpec.BooleanValue RESPECT_LIGHTNING_FLASHES;
    public static final ModConfigSpec.BooleanValue SMOOTH_LIGHTMAP_UPDATES;

    public static final ModConfigSpec.IntValue DUSK_TRANSITION_START_TICK;
    public static final ModConfigSpec.IntValue FULL_NIGHT_START_TICK;
    public static final ModConfigSpec.IntValue FULL_NIGHT_END_TICK;
    public static final ModConfigSpec.IntValue DAWN_TRANSITION_END_TICK;

    public static final ModConfigSpec.DoubleValue MOONLESS_NIGHT_SKY_FACTOR;
    public static final ModConfigSpec.DoubleValue FULL_MOON_SKY_FACTOR;
    public static final ModConfigSpec.DoubleValue SKYLESS_DIMENSION_LIGHT_FACTOR;
    public static final ModConfigSpec.DoubleValue MINIMUM_LUMINANCE_FLOOR;
    public static final ModConfigSpec.DoubleValue CAVE_LUMINANCE_FLOOR;
    public static final ModConfigSpec.DoubleValue DARKNESS_CURVE;
    public static final ModConfigSpec.DoubleValue BLOCK_LIGHT_PRESERVATION;
    public static final ModConfigSpec.DoubleValue RAIN_DARKENING;
    public static final ModConfigSpec.DoubleValue THUNDER_DARKENING;
    public static final ModConfigSpec.DoubleValue NIGHT_DESATURATION;
    public static final ModConfigSpec.DoubleValue NIGHT_COOL_TINT;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("natural_darkness");

        ENABLED = BUILDER
                .comment("Master switch for Selas natural darkness.")
                .translation("selas.configuration.enabled")
                .define("enabled", true);

        AFFECT_OVERWORLD = BUILDER
                .comment("Apply natural darkness in the Overworld.")
                .translation("selas.configuration.affect_overworld")
                .define("affect_overworld", true);

        AFFECT_CUSTOM_SKY_DIMENSIONS = BUILDER
                .comment("Apply natural darkness in custom dimensions that have skylight.")
                .translation("selas.configuration.affect_custom_sky_dimensions")
                .define("affect_custom_sky_dimensions", true);

        AFFECT_SKYLESS_DIMENSIONS = BUILDER
                .comment("Apply natural darkness in dimensions without skylight, such as the Nether-like custom dimensions.")
                .translation("selas.configuration.affect_skyless_dimensions")
                .define("affect_skyless_dimensions", true);

        RESPECT_NIGHT_VISION = BUILDER
                .comment("Disable Selas darkness while the local player has Night Vision or useful Conduit Power underwater vision.")
                .translation("selas.configuration.respect_night_vision")
                .define("respect_night_vision", true);

        RESPECT_LIGHTNING_FLASHES = BUILDER
                .comment("Disable Selas darkness during lightning sky flashes.")
                .translation("selas.configuration.respect_lightning_flashes")
                .define("respect_lightning_flashes", true);

        SMOOTH_LIGHTMAP_UPDATES = BUILDER
                .comment("Force the tiny vanilla lightmap texture to update every rendered frame while Selas is active. This makes dusk and dawn transitions smoother.")
                .translation("selas.configuration.smooth_lightmap_updates")
                .define("smooth_lightmap_updates", true);

        DUSK_TRANSITION_START_TICK = BUILDER
                .comment("Minecraft day tick when Selas begins fading from daylight into natural night. Vanilla sunset is around 12000.")
                .translation("selas.configuration.dusk_transition_start_tick")
                .defineInRange("dusk_transition_start_tick", 11800, 0, 23999);

        FULL_NIGHT_START_TICK = BUILDER
                .comment("Minecraft day tick when the night reaches full Selas darkness after twilight.")
                .translation("selas.configuration.full_night_start_tick")
                .defineInRange("full_night_start_tick", 14000, 0, 23999);

        FULL_NIGHT_END_TICK = BUILDER
                .comment("Minecraft day tick when full Selas darkness starts fading into dawn.")
                .translation("selas.configuration.full_night_end_tick")
                .defineInRange("full_night_end_tick", 21200, 0, 23999);

        DAWN_TRANSITION_END_TICK = BUILDER
                .comment("Minecraft day tick when dawn finishes and Selas returns to daylight. This value may be lower than full_night_end_tick because dawn crosses midnight.")
                .translation("selas.configuration.dawn_transition_end_tick")
                .defineInRange("dawn_transition_end_tick", 400, 0, 23999);

        MOONLESS_NIGHT_SKY_FACTOR = BUILDER
                .comment("Relative natural sky brightness at midnight during a moonless night. Lower values are darker.")
                .translation("selas.configuration.moonless_night_sky_factor")
                .defineInRange("moonless_night_sky_factor", 0.018D, 0.0D, 1.0D);

        FULL_MOON_SKY_FACTOR = BUILDER
                .comment("Relative natural sky brightness at midnight during a full moon. Lower values are darker.")
                .translation("selas.configuration.full_moon_sky_factor")
                .defineInRange("full_moon_sky_factor", 0.105D, 0.0D, 1.0D);

        SKYLESS_DIMENSION_LIGHT_FACTOR = BUILDER
                .comment("Light factor used in dimensions without skylight. This keeps such dimensions dim without assuming an actual sun or moon.")
                .translation("selas.configuration.skyless_dimension_light_factor")
                .defineInRange("skyless_dimension_light_factor", 0.09D, 0.0D, 1.0D);

        MINIMUM_LUMINANCE_FLOOR = BUILDER
                .comment("Absolute minimum visible luminance floor. Set to 0 for true black; small values keep silhouettes barely visible.")
                .translation("selas.configuration.minimum_luminance_floor")
                .defineInRange("minimum_luminance_floor", 0.006D, 0.0D, 0.25D);

        CAVE_LUMINANCE_FLOOR = BUILDER
                .comment("Minimum luminance floor when both block light and sky light are low.")
                .translation("selas.configuration.cave_luminance_floor")
                .defineInRange("cave_luminance_floor", 0.003D, 0.0D, 0.25D);

        DARKNESS_CURVE = BUILDER
                .comment("Perceptual darkness curve. Higher values make low light fall off more harshly while preserving strong light sources.")
                .translation("selas.configuration.darkness_curve")
                .defineInRange("darkness_curve", 1.72D, 0.25D, 4.0D);

        BLOCK_LIGHT_PRESERVATION = BUILDER
                .comment("How strongly block light resists Selas darkening. 1 keeps torches and other strong block lights readable.")
                .translation("selas.configuration.block_light_preservation")
                .defineInRange("block_light_preservation", 1.0D, 0.0D, 1.5D);

        RAIN_DARKENING = BUILDER
                .comment("Extra sky darkening at full rain strength.")
                .translation("selas.configuration.rain_darkening")
                .defineInRange("rain_darkening", 0.16D, 0.0D, 1.0D);

        THUNDER_DARKENING = BUILDER
                .comment("Extra sky darkening at full thunder strength.")
                .translation("selas.configuration.thunder_darkening")
                .defineInRange("thunder_darkening", 0.32D, 0.0D, 1.0D);

        NIGHT_DESATURATION = BUILDER
                .comment("Desaturates colors in darker conditions. 0 disables desaturation, 1 is grayscale in deep darkness.")
                .translation("selas.configuration.night_desaturation")
                .defineInRange("night_desaturation", 0.34D, 0.0D, 1.0D);

        NIGHT_COOL_TINT = BUILDER
                .comment("Adds a subtle cool tint in low natural light. 0 disables it.")
                .translation("selas.configuration.night_cool_tint")
                .defineInRange("night_cool_tint", 0.055D, 0.0D, 0.5D);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private SelasClientConfig() {
    }
}
