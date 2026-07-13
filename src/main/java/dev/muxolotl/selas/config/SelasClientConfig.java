package dev.muxolotl.selas.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SelasClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.BooleanValue SMOOTH_LIGHTMAP_UPDATES;
    public static final ModConfigSpec.BooleanValue RESPECT_NIGHT_VISION;
    public static final ModConfigSpec.BooleanValue RESPECT_LIGHTNING_FLASHES;
    public static final ModConfigSpec.BooleanValue RESPECT_GAMMA;
    public static final ModConfigSpec.BooleanValue DISABLE_WITH_SHADERS;

    public static final ModConfigSpec.BooleanValue AFFECT_OVERWORLD;
    public static final ModConfigSpec.BooleanValue AFFECT_CUSTOM_SKY_DIMENSIONS;
    public static final ModConfigSpec.BooleanValue AFFECT_SKYLESS_DIMENSIONS;
    public static final ModConfigSpec.DoubleValue SKYLESS_DIMENSION_LIGHT_FACTOR;

    public static final ModConfigSpec.DoubleValue NETHER_LIGHT_FACTOR;
    public static final ModConfigSpec.DoubleValue NETHER_WARM_TINT;
    public static final ModConfigSpec.DoubleValue END_LIGHT_FACTOR;
    public static final ModConfigSpec.DoubleValue END_COOL_TINT;

    public static final ModConfigSpec.IntValue DUSK_TRANSITION_START_TICK;
    public static final ModConfigSpec.IntValue FULL_NIGHT_START_TICK;
    public static final ModConfigSpec.IntValue FULL_NIGHT_END_TICK;
    public static final ModConfigSpec.IntValue DAWN_TRANSITION_END_TICK;

    public static final ModConfigSpec.DoubleValue MOONLESS_NIGHT_SKY_FACTOR;
    public static final ModConfigSpec.DoubleValue FULL_MOON_SKY_FACTOR;
    public static final ModConfigSpec.DoubleValue MOON_PHASE_CURVE;
    public static final ModConfigSpec.DoubleValue RAIN_DARKENING;
    public static final ModConfigSpec.DoubleValue THUNDER_DARKENING;

    public static final ModConfigSpec.DoubleValue MINIMUM_LUMINANCE_FLOOR;
    public static final ModConfigSpec.DoubleValue CAVE_LUMINANCE_FLOOR;
    public static final ModConfigSpec.DoubleValue STARLIGHT_LUMINANCE_FLOOR;
    public static final ModConfigSpec.DoubleValue DARKNESS_CURVE;
    public static final ModConfigSpec.DoubleValue BLOCK_LIGHT_PRESERVATION;

    public static final ModConfigSpec.DoubleValue NIGHT_DESATURATION;
    public static final ModConfigSpec.DoubleValue NIGHT_COOL_TINT;
    public static final ModConfigSpec.DoubleValue MOON_WARMTH;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER
                .comment("Client-side lightmap changes for darker nights and caves.")
                .translation("selas.configuration.section.natural_darkness")
                .push("natural_darkness");

        BUILDER
                .comment("General switches.")
                .translation("selas.configuration.section.general")
                .push("general");

        ENABLED = BUILDER
                .comment("Turns the Selas lightmap changes on or off.")
                .translation("selas.configuration.enabled")
                .define("enabled", true);

        SMOOTH_LIGHTMAP_UPDATES = BUILDER
                .comment("Updates the lightmap every rendered frame while Selas is active. This makes day/night transitions smoother.")
                .translation("selas.configuration.smooth_lightmap_updates")
                .define("smooth_lightmap_updates", true);

        RESPECT_NIGHT_VISION = BUILDER
                .comment("Stops Selas from darkening the view while Night Vision is active. Also covers useful underwater Conduit Power vision.")
                .translation("selas.configuration.respect_night_vision")
                .define("respect_night_vision", true);

        RESPECT_LIGHTNING_FLASHES = BUILDER
                .comment("Stops Selas from darkening lightning flashes.")
                .translation("selas.configuration.respect_lightning_flashes")
                .define("respect_lightning_flashes", true);

        RESPECT_GAMMA = BUILDER
                .comment(
                        "When true, the brightness slider lifts Selas target brightness (no double-gamma on already-baked lightmap pixels). ",
                        "When false, Selas keeps its absolute luminance targets so the slider cannot wash out nights and caves. ",
                        "A small residual slider effect can still appear in already-bright areas because vanilla applies gamma before Selas runs."
                )
                .translation("selas.configuration.respect_gamma")
                .define("respect_gamma", false);

        DISABLE_WITH_SHADERS = BUILDER
                .comment(
                        "When true, Selas skips lightmap changes while an Iris/Oculus shader pack is in use. ",
                        "Shader packs usually replace vanilla lightmap lighting, so leaving Selas on only wastes work and confuses tuning."
                )
                .translation("selas.configuration.disable_with_shaders")
                .define("disable_with_shaders", true);

        BUILDER.pop();

        BUILDER
                .comment("Where the module is active.")
                .translation("selas.configuration.section.dimensions")
                .push("dimensions");

        AFFECT_OVERWORLD = BUILDER
                .comment("Applies the effect in the Overworld.")
                .translation("selas.configuration.affect_overworld")
                .define("affect_overworld", true);

        AFFECT_CUSTOM_SKY_DIMENSIONS = BUILDER
                .comment("Applies the effect in custom dimensions that have skylight.")
                .translation("selas.configuration.affect_custom_sky_dimensions")
                .define("affect_custom_sky_dimensions", true);

        AFFECT_SKYLESS_DIMENSIONS = BUILDER
                .comment("Applies the effect in dimensions without skylight, including the Nether, the End, and modded skyless dimensions.")
                .translation("selas.configuration.affect_skyless_dimensions")
                .define("affect_skyless_dimensions", true);

        SKYLESS_DIMENSION_LIGHT_FACTOR = BUILDER
                .comment("Base ambient brightness for modded dimensions with no sun/moon that are neither the Nether nor the End.")
                .translation("selas.configuration.skyless_dimension_light_factor")
                .defineInRange("skyless_dimension_light_factor", 0.10D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER
                .comment("Per-dimension lighting for the Nether and End. These replace the generic skyless profile for those dimensions so they are no longer pitch black.")
                .translation("selas.configuration.section.dimension_lighting")
                .push("dimension_lighting");

        NETHER_LIGHT_FACTOR = BUILDER
                .comment("Base ambient brightness added across the Nether. Higher values make the Nether more playable but less moody.")
                .translation("selas.configuration.nether_light_factor")
                .defineInRange("nether_light_factor", 0.14D, 0.0D, 1.0D);

        NETHER_WARM_TINT = BUILDER
                .comment("Warm (reddish/orange) tint applied in darker Nether areas, evoking lava and fire glow.")
                .translation("selas.configuration.nether_warm_tint")
                .defineInRange("nether_warm_tint", 0.06D, 0.0D, 0.5D);

        END_LIGHT_FACTOR = BUILDER
                .comment("Base ambient brightness added across the End. Higher values make the void more visible.")
                .translation("selas.configuration.end_light_factor")
                .defineInRange("end_light_factor", 0.09D, 0.0D, 1.0D);

        END_COOL_TINT = BUILDER
                .comment("Cool (starlight blue) tint applied in darker End areas.")
                .translation("selas.configuration.end_cool_tint")
                .defineInRange("end_cool_tint", 0.05D, 0.0D, 0.5D);

        BUILDER.pop();

        BUILDER
                .comment("Day/night transition timing. Values are Minecraft day ticks. One full Minecraft day is 24000 ticks.")
                .translation("selas.configuration.section.twilight")
                .push("twilight");

        DUSK_TRANSITION_START_TICK = BUILDER
                .comment("Selas starts fading from day brightness into night brightness.")
                .translation("selas.configuration.dusk_transition_start_tick")
                .defineInRange("dusk_transition_start_tick", 11800, 0, 23999);

        FULL_NIGHT_START_TICK = BUILDER
                .comment("Full Selas night begins.")
                .translation("selas.configuration.full_night_start_tick")
                .defineInRange("full_night_start_tick", 14000, 0, 23999);

        FULL_NIGHT_END_TICK = BUILDER
                .comment("Full Selas night starts fading into dawn.")
                .translation("selas.configuration.full_night_end_tick")
                .defineInRange("full_night_end_tick", 21600, 0, 23999);

        DAWN_TRANSITION_END_TICK = BUILDER
                .comment("Dawn fade ends after midnight wraps back to day tick 0.")
                .translation("selas.configuration.dawn_transition_end_tick")
                .defineInRange("dawn_transition_end_tick", 400, 0, 23999);

        BUILDER.pop();

        BUILDER
                .comment("Moon and weather values.")
                .translation("selas.configuration.section.natural_light")
                .push("natural_light");

        MOONLESS_NIGHT_SKY_FACTOR = BUILDER
                .comment("Sky brightness at midnight during a new moon. Lower values make moonless nights darker. Keep above cave darkness for open sky.")
                .translation("selas.configuration.moonless_night_sky_factor")
                .defineInRange("moonless_night_sky_factor", 0.075D, 0.0D, 1.0D);

        FULL_MOON_SKY_FACTOR = BUILDER
                .comment("Sky brightness at midnight during a full moon. Higher values make full-moon nights brighter.")
                .translation("selas.configuration.full_moon_sky_factor")
                .defineInRange("full_moon_sky_factor", 0.34D, 0.0D, 1.0D);

        MOON_PHASE_CURVE = BUILDER
                .comment("Controls the progression between new moon and full moon. 1 is linear; lower values brighten intermediate phases.")
                .translation("selas.configuration.moon_phase_curve")
                .defineInRange("moon_phase_curve", 0.65D, 0.5D, 3.0D);

        RAIN_DARKENING = BUILDER
                .comment("Natural sky darkening when rain is at full strength.")
                .translation("selas.configuration.rain_darkening")
                .defineInRange("rain_darkening", 0.16D, 0.0D, 1.0D);

        THUNDER_DARKENING = BUILDER
                .comment("Natural sky darkening when thunder is at full strength. Rain and thunder penalties do not stack.")
                .translation("selas.configuration.thunder_darkening")
                .defineInRange("thunder_darkening", 0.32D, 0.0D, 1.0D);

        BUILDER.pop();

        BUILDER
                .comment("Brightness curve and minimum brightness.")
                .translation("selas.configuration.section.darkness")
                .push("darkness");

        MINIMUM_LUMINANCE_FLOOR = BUILDER
                .comment("Lowest general brightness Selas allows in mixed light. 0 allows true black.")
                .translation("selas.configuration.minimum_luminance_floor")
                .defineInRange("minimum_luminance_floor", 0.010D, 0.0D, 0.25D);

        CAVE_LUMINANCE_FLOOR = BUILDER
                .comment("Near-black anti-crush floor when both block light and sky light are low (deep caves / sealed spaces). Should stay below open-sky starlight.")
                .translation("selas.configuration.cave_luminance_floor")
                .defineInRange("cave_luminance_floor", 0.004D, 0.0D, 0.25D);

        STARLIGHT_LUMINANCE_FLOOR = BUILDER
                .comment(
                        "Extra luminance floor mixed in for open sky at night (starlight / airglow). ",
                        "Uses sky light presence so caves stay darker than moonless surface nights. 0 disables the extra floor."
                )
                .translation("selas.configuration.starlight_luminance_floor")
                .defineInRange("starlight_luminance_floor", 0.016D, 0.0D, 0.25D);

        DARKNESS_CURVE = BUILDER
                .comment("Controls how quickly low light falls into darkness. Higher values make low light darker.")
                .translation("selas.configuration.darkness_curve")
                .defineInRange("darkness_curve", 1.75D, 0.25D, 4.0D);

        BLOCK_LIGHT_PRESERVATION = BUILDER
                .comment("Controls how much block light resists darkening. Higher values keep torches and lava brighter.")
                .translation("selas.configuration.block_light_preservation")
                .defineInRange("block_light_preservation", 1.0D, 0.0D, 1.5D);

        BUILDER.pop();

        BUILDER
                .comment("Color changes in low light.")
                .translation("selas.configuration.section.color")
                .push("color");

        NIGHT_DESATURATION = BUILDER
                .comment("Removes color in darker areas. 0 disables this.")
                .translation("selas.configuration.night_desaturation")
                .defineInRange("night_desaturation", 0.38D, 0.0D, 1.0D);

        NIGHT_COOL_TINT = BUILDER
                .comment("Adds a small blue/cold tint in low natural light. 0 disables this.")
                .translation("selas.configuration.night_cool_tint")
                .defineInRange("night_cool_tint", 0.07D, 0.0D, 0.5D);

        MOON_WARMTH = BUILDER
                .comment(
                        "Adds a faint warm (creamy) tint in low light that scales with moon brightness, ",
                        "so full-moon nights feel slightly warmer than the plain cool starlight tint. 0 disables this."
                )
                .translation("selas.configuration.moon_warmth")
                .defineInRange("moon_warmth", 0.03D, 0.0D, 0.3D);

        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private SelasClientConfig() {
    }
}
