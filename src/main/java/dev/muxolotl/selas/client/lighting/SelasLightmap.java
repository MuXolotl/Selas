package dev.muxolotl.selas.client.lighting;

import com.mojang.blaze3d.platform.NativeImage;
import dev.muxolotl.selas.Selas;
import dev.muxolotl.selas.client.compat.ShaderPackCompat;
import dev.muxolotl.selas.config.SelasClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public final class SelasLightmap {
    private static final float MINECRAFT_DAY_TICKS = SelasMath.MINECRAFT_DAY_TICKS;

    private static final int DEFAULT_DUSK_TRANSITION_START_TICK = 11800;
    private static final int DEFAULT_FULL_NIGHT_START_TICK = 14000;
    private static final int DEFAULT_FULL_NIGHT_END_TICK = 21200;
    private static final int DEFAULT_DAWN_TRANSITION_END_TICK = 400;

    // Per-channel tint response coefficients. Kept as named constants so the mood
    // of each tint is legible instead of hidden behind bare magic numbers.
    private static final float COOL_TINT_R = 0.45F;
    private static final float COOL_TINT_G = 0.12F;
    private static final float COOL_TINT_B_MUL = 0.04F;
    private static final float COOL_TINT_B_ADD = 0.030F;

    private static final float MOON_WARM_R = 0.06F;
    private static final float MOON_WARM_G = 0.02F;
    private static final float MOON_WARM_B = 0.04F;

    private static final float NETHER_TINT_R = 0.15F;
    private static final float NETHER_TINT_G = 0.30F;
    private static final float NETHER_TINT_B = 0.50F;

    private static final float END_TINT_R = 0.50F;
    private static final float END_TINT_G = 0.25F;

    private static String lastInvalidTwilightValues;

    private SelasLightmap() {
    }

    public static boolean shouldUpdateEveryFrame() {
        if (!SelasClientConfig.ENABLED.getAsBoolean() || !SelasClientConfig.SMOOTH_LIGHTMAP_UPDATES.getAsBoolean()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        return level != null && player != null && shouldAffect(level, player);
    }

    public static void transform(NativeImage pixels, float partialTick) {
        if (!SelasClientConfig.ENABLED.getAsBoolean() || pixels == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;

        if (level == null || player == null || !shouldAffect(level, player)) {
            return;
        }

        LightingContext context = LightingContext.create(level, partialTick);

        for (int blockIndex = 0; blockIndex < 16; blockIndex++) {
            for (int skyIndex = 0; skyIndex < 16; skyIndex++) {
                int original = pixels.getPixelRGBA(blockIndex, skyIndex);
                int transformed = transformPixel(original, blockIndex, skyIndex, context);
                pixels.setPixelRGBA(blockIndex, skyIndex, transformed);
            }
        }
    }

    private static boolean shouldAffect(ClientLevel level, LocalPlayer player) {
        if (SelasClientConfig.DISABLE_WITH_SHADERS.getAsBoolean() && ShaderPackCompat.isShaderPackInUse()) {
            return false;
        }

        if (SelasClientConfig.RESPECT_NIGHT_VISION.getAsBoolean()) {
            boolean hasNightVision = player.hasEffect(MobEffects.NIGHT_VISION);
            boolean hasUsefulConduitVision = player.hasEffect(MobEffects.CONDUIT_POWER) && player.getWaterVision() > 0.0F;
            if (hasNightVision || hasUsefulConduitVision) {
                return false;
            }
        }

        if (SelasClientConfig.RESPECT_LIGHTNING_FLASHES.getAsBoolean() && level.getSkyFlashTime() > 0) {
            return false;
        }

        ResourceKey<Level> dimension = level.dimension();
        if (dimension.equals(Level.OVERWORLD)) {
            return SelasClientConfig.AFFECT_OVERWORLD.getAsBoolean();
        }

        if (level.dimensionType().hasSkyLight()) {
            return SelasClientConfig.AFFECT_CUSTOM_SKY_DIMENSIONS.getAsBoolean();
        }

        return SelasClientConfig.AFFECT_SKYLESS_DIMENSIONS.getAsBoolean();
    }

    private static int transformPixel(int color, int blockIndex, int skyIndex, LightingContext context) {
        float block = blockIndex / 15.0F;
        float sky = skyIndex / 15.0F;

        float effectiveLight = SelasMath.effectiveLight(
                block, sky, context.blockLightPreservation(), context.skyFactor());

        float floor = context.floor(block, sky);
        float targetLuminance = SelasMath.targetLuminance(effectiveLight, floor, context.darknessCurve(), 0.0F);

        float r = (color & 0xFF) / 255.0F;
        float g = ((color >>> 8) & 0xFF) / 255.0F;
        float b = ((color >>> 16) & 0xFF) / 255.0F;
        float a = ((color >>> 24) & 0xFF) / 255.0F;

        float currentLuminance = SelasMath.luminance(r, g, b);
        if (currentLuminance > targetLuminance && currentLuminance > 0.0001F) {
            float scale = targetLuminance / currentLuminance;
            r *= scale;
            g *= scale;
            b *= scale;
        }

        float darkness = 1.0F - SelasMath.saturate(effectiveLight);

        float desaturation = SelasMath.desaturationAmount(darkness, context.nightDesaturation());
        if (desaturation > 0.0F) {
            float gray = SelasMath.luminance(r, g, b);
            r = Mth.lerp(desaturation, r, gray);
            g = Mth.lerp(desaturation, g, gray);
            b = Mth.lerp(desaturation, b, gray);
        }

        // Skyless dimensions get an additive ambient lift so pitch-black cells are
        // raised toward a faint glow instead of merely being capped. A screen blend
        // keeps already-lit cells untouched, so a higher ambient factor reads as
        // genuinely brighter (not just "less darkened").
        float ambient = context.baseAmbient();
        if (ambient > 0.0F) {
            r = r + (1.0F - r) * ambient;
            g = g + (1.0F - g) * ambient;
            b = b + (1.0F - b) * ambient;
        }

        float coolTint = darkness * context.nightAmount() * context.nightCoolTint();
        if (coolTint > 0.0F) {
            r *= 1.0F - coolTint * COOL_TINT_R;
            g *= 1.0F - coolTint * COOL_TINT_G;
            b = b * (1.0F - coolTint * COOL_TINT_B_MUL) + coolTint * COOL_TINT_B_ADD;
        }

        float moonWarm = darkness * context.moonWarmth();
        if (moonWarm > 0.0F) {
            r *= 1.0F + moonWarm * MOON_WARM_R;
            g *= 1.0F + moonWarm * MOON_WARM_G;
            b *= 1.0F - moonWarm * MOON_WARM_B;
        }

        float warmTint = darkness * context.warmTint();
        if (warmTint > 0.0F) {
            r *= 1.0F - NETHER_TINT_R * warmTint;
            g *= 1.0F - NETHER_TINT_G * warmTint;
            b *= 1.0F - NETHER_TINT_B * warmTint;
        }

        float endCoolTint = darkness * context.coolTint();
        if (endCoolTint > 0.0F) {
            r *= 1.0F - END_TINT_R * endCoolTint;
            g *= 1.0F - END_TINT_G * endCoolTint;
        }

        return SelasMath.packNativeColor(a, r, g, b);
    }

    private record LightingContext(
            float skyFactor,
            float nightAmount,
            boolean skyless,
            float baseAmbient,
            float warmTint,
            float coolTint,
            float moonWarmth,
            float blockLightPreservation,
            float darknessCurve,
            float nightDesaturation,
            float nightCoolTint,
            float minimumFloor,
            float caveFloor,
            float starlightFloor
    ) {
        private static LightingContext create(ClientLevel level, float partialTick) {
            SelasPreset.Look look = resolveLook();

            float blockLightPreservation = look.blockLightPreservation();
            float darknessCurve = look.darknessCurve();
            float nightDesaturation = look.nightDesaturation();
            float nightCoolTint = look.nightCoolTint();
            float minimumFloor = look.minimumLuminanceFloor();
            float caveFloor = look.caveLuminanceFloor();
            float starlightFloor = look.starlightLuminanceFloor();

            if (!level.dimensionType().hasSkyLight()) {
                ResourceKey<Level> dimension = level.dimension();
                float baseAmbient;
                float warmTint = 0.0F;
                float coolTint = 0.0F;
                if (dimension.equals(Level.NETHER)) {
                    baseAmbient = look.netherLightFactor();
                    warmTint = look.netherWarmTint();
                } else if (dimension.equals(Level.END)) {
                    baseAmbient = look.endLightFactor();
                    coolTint = look.endCoolTint();
                } else {
                    baseAmbient = look.skylessDimensionLightFactor();
                }
                return new LightingContext(
                        0.0F, 0.0F, true, baseAmbient, warmTint, coolTint, 0.0F,
                        blockLightPreservation, darknessCurve, nightDesaturation, nightCoolTint,
                        minimumFloor, caveFloor, starlightFloor);
            }

            float dayTick = SelasMath.positiveModulo(
                    (level.getDayTime() % 24000L) + partialTick, MINECRAFT_DAY_TICKS);
            float night = calculateNightAmount(dayTick);
            float moon = SelasMath.saturate(level.getMoonBrightness());
            float moonless = look.moonlessNightSkyFactor();
            float fullMoon = look.fullMoonSkyFactor();
            float moonCurve = look.moonPhaseCurve();
            float moonPhaseProgress = SelasMath.moonPhaseProgress(moon, moonCurve);
            float lunarFactor = Mth.lerp(moonPhaseProgress, moonless, fullMoon);

            float weather = SelasMath.weatherFactor(
                    level.getRainLevel(partialTick),
                    level.getThunderLevel(partialTick),
                    look.rainDarkening(),
                    look.thunderDarkening());

            float naturalSkyFactor = Mth.lerp(night, 1.0F, lunarFactor);
            float skyFactor = SelasMath.saturate(naturalSkyFactor * weather);

            float moonWarmth = moonPhaseProgress * night * weather * look.moonWarmth();

            return new LightingContext(
                    skyFactor, night, false, 0.0F, 0.0F, 0.0F, moonWarmth,
                    blockLightPreservation, darknessCurve, nightDesaturation, nightCoolTint,
                    minimumFloor, caveFloor, starlightFloor);
        }

        /**
         * Effective "look" values for this frame: a non-custom preset overrides
         * the individual sliders, while CUSTOM builds a Look from them so the rest
         * of the pipeline is preset-agnostic.
         */
        private static SelasPreset.Look resolveLook() {
            SelasPreset preset = SelasClientConfig.PRESET.get();
            if (preset != null && !preset.isCustom()) {
                return preset.look();
            }
            return new SelasPreset.Look(
                    (float) SelasClientConfig.MOONLESS_NIGHT_SKY_FACTOR.getAsDouble(),
                    (float) SelasClientConfig.FULL_MOON_SKY_FACTOR.getAsDouble(),
                    (float) SelasClientConfig.MOON_PHASE_CURVE.getAsDouble(),
                    (float) SelasClientConfig.RAIN_DARKENING.getAsDouble(),
                    (float) SelasClientConfig.THUNDER_DARKENING.getAsDouble(),
                    (float) SelasClientConfig.MINIMUM_LUMINANCE_FLOOR.getAsDouble(),
                    (float) SelasClientConfig.CAVE_LUMINANCE_FLOOR.getAsDouble(),
                    (float) SelasClientConfig.STARLIGHT_LUMINANCE_FLOOR.getAsDouble(),
                    (float) SelasClientConfig.DARKNESS_CURVE.getAsDouble(),
                    (float) SelasClientConfig.BLOCK_LIGHT_PRESERVATION.getAsDouble(),
                    (float) SelasClientConfig.NIGHT_DESATURATION.getAsDouble(),
                    (float) SelasClientConfig.NIGHT_COOL_TINT.getAsDouble(),
                    (float) SelasClientConfig.MOON_WARMTH.getAsDouble(),
                    (float) SelasClientConfig.NETHER_LIGHT_FACTOR.getAsDouble(),
                    (float) SelasClientConfig.NETHER_WARM_TINT.getAsDouble(),
                    (float) SelasClientConfig.END_LIGHT_FACTOR.getAsDouble(),
                    (float) SelasClientConfig.END_COOL_TINT.getAsDouble(),
                    (float) SelasClientConfig.SKYLESS_DIMENSION_LIGHT_FACTOR.getAsDouble());
        }

        private float floor(float block, float sky) {
            if (skyless) {
                return minimumFloor;
            }
            return SelasMath.skylitFloor(block, sky, minimumFloor, caveFloor, starlightFloor, nightAmount);
        }
    }

    private static float calculateNightAmount(float dayTick) {
        TwilightTimes twilight = getTwilightTimes();
        return SelasMath.nightAmount(
                dayTick,
                twilight.duskStart(),
                twilight.fullNightStart(),
                twilight.fullNightEnd(),
                twilight.dawnEnd());
    }

    private static TwilightTimes getTwilightTimes() {
        int duskStart = SelasClientConfig.DUSK_TRANSITION_START_TICK.get();
        int fullNightStart = SelasClientConfig.FULL_NIGHT_START_TICK.get();
        int fullNightEnd = SelasClientConfig.FULL_NIGHT_END_TICK.get();
        int dawnEnd = SelasClientConfig.DAWN_TRANSITION_END_TICK.get();

        if (SelasMath.isValidTwilightOrder(duskStart, fullNightStart, fullNightEnd, dawnEnd)) {
            return new TwilightTimes(duskStart, fullNightStart, fullNightEnd, dawnEnd);
        }

        String invalidValues = duskStart + ", " + fullNightStart + ", " + fullNightEnd + ", " + dawnEnd;
        if (!invalidValues.equals(lastInvalidTwilightValues)) {
            Selas.LOGGER.warn(
                    "Invalid Selas twilight tick order: dusk={}, fullNightStart={}, fullNightEnd={}, dawn={}. Using defaults.",
                    duskStart, fullNightStart, fullNightEnd, dawnEnd
            );
            lastInvalidTwilightValues = invalidValues;
        }

        return new TwilightTimes(
                DEFAULT_DUSK_TRANSITION_START_TICK,
                DEFAULT_FULL_NIGHT_START_TICK,
                DEFAULT_FULL_NIGHT_END_TICK,
                DEFAULT_DAWN_TRANSITION_END_TICK
        );
    }

    private record TwilightTimes(int duskStart, int fullNightStart, int fullNightEnd, int dawnEnd) {
    }
}
