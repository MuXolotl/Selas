package dev.muxolotl.selas.client.lighting;

import com.mojang.blaze3d.platform.NativeImage;
import dev.muxolotl.selas.Selas;
import dev.muxolotl.selas.config.SelasClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public final class SelasLightmap {
    private static final float LUMINANCE_R = 0.2126F;
    private static final float LUMINANCE_G = 0.7152F;
    private static final float LUMINANCE_B = 0.0722F;
    private static final float MINECRAFT_DAY_TICKS = 24000.0F;

    private static final int DEFAULT_DUSK_TRANSITION_START_TICK = 11800;
    private static final int DEFAULT_FULL_NIGHT_START_TICK = 14000;
    private static final int DEFAULT_FULL_NIGHT_END_TICK = 21200;
    private static final int DEFAULT_DAWN_TRANSITION_END_TICK = 400;

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

        float blockContribution = saturate(block * (float) SelasClientConfig.BLOCK_LIGHT_PRESERVATION.getAsDouble());
        float skyContribution = saturate(sky * context.skyFactor());
        float effectiveLight = combineLightContributions(blockContribution, skyContribution);

        float floor = context.floor(block, sky);
        float curve = (float) SelasClientConfig.DARKNESS_CURVE.getAsDouble();
        float targetLuminance = floor + (1.0F - floor) * (float) Math.pow(saturate(effectiveLight), curve);
        targetLuminance = saturate(targetLuminance);

        float r = (color & 0xFF) / 255.0F;
        float g = ((color >>> 8) & 0xFF) / 255.0F;
        float b = ((color >>> 16) & 0xFF) / 255.0F;
        float a = ((color >>> 24) & 0xFF) / 255.0F;

        float currentLuminance = luminance(r, g, b);
        if (currentLuminance > targetLuminance && currentLuminance > 0.0001F) {
            float scale = targetLuminance / currentLuminance;
            r *= scale;
            g *= scale;
            b *= scale;
        }

        float darkness = 1.0F - saturate(effectiveLight);
        float desaturation = darkness * (float) SelasClientConfig.NIGHT_DESATURATION.getAsDouble();
        if (desaturation > 0.0F) {
            float gray = luminance(r, g, b);
            r = Mth.lerp(desaturation, r, gray);
            g = Mth.lerp(desaturation, g, gray);
            b = Mth.lerp(desaturation, b, gray);
        }

        float coolTint = darkness * context.nightAmount() * (float) SelasClientConfig.NIGHT_COOL_TINT.getAsDouble();
        if (coolTint > 0.0F) {
            r *= 1.0F - coolTint * 0.55F;
            g *= 1.0F - coolTint * 0.18F;
            b = b * (1.0F - coolTint * 0.08F) + coolTint * 0.018F;
        }

        return toNativeImageColor(a, r, g, b);
    }

    private static float luminance(float r, float g, float b) {
        return r * LUMINANCE_R + g * LUMINANCE_G + b * LUMINANCE_B;
    }

    private static int toNativeImageColor(float a, float r, float g, float b) {
        int ai = Math.round(saturate(a) * 255.0F);
        int ri = Math.round(saturate(r) * 255.0F);
        int gi = Math.round(saturate(g) * 255.0F);
        int bi = Math.round(saturate(b) * 255.0F);
        return (ai << 24) | ri | (gi << 8) | (bi << 16);
    }

    private static float saturate(float value) {
        return Mth.clamp(value, 0.0F, 1.0F);
    }

    private static float combineLightContributions(float block, float sky) {
        return saturate(block + sky - block * sky);
    }

    private record LightingContext(float skyFactor, float nightAmount, boolean skyless) {
        private static LightingContext create(ClientLevel level, float partialTick) {
            if (!level.dimensionType().hasSkyLight()) {
                float factor = (float) SelasClientConfig.SKYLESS_DIMENSION_LIGHT_FACTOR.getAsDouble();
                return new LightingContext(factor, 1.0F, true);
            }

            float dayTick = positiveModulo((level.getDayTime() % 24000L) + partialTick, MINECRAFT_DAY_TICKS);
            float night = calculateNightAmount(dayTick);
            float moon = saturate(level.getMoonBrightness());
            float moonless = (float) SelasClientConfig.MOONLESS_NIGHT_SKY_FACTOR.getAsDouble();
            float fullMoon = (float) SelasClientConfig.FULL_MOON_SKY_FACTOR.getAsDouble();
            float moonCurve = (float) SelasClientConfig.MOON_PHASE_CURVE.getAsDouble();
            float moonPhaseProgress = saturate((float) Math.pow(moon, moonCurve));
            float lunarFactor = Mth.lerp(moonPhaseProgress, moonless, fullMoon);

            float rain = saturate(level.getRainLevel(partialTick));
            float thunder = saturate(level.getThunderLevel(partialTick));
            float rainDarkening = rain * (float) SelasClientConfig.RAIN_DARKENING.getAsDouble();
            float thunderDarkening = thunder * (float) SelasClientConfig.THUNDER_DARKENING.getAsDouble();

            float weather = 1.0F - Math.max(rainDarkening, thunderDarkening);
            weather = saturate(weather);

            float naturalSkyFactor = Mth.lerp(night, 1.0F, lunarFactor);
            float skyFactor = naturalSkyFactor * weather;
            return new LightingContext(saturate(skyFactor), night, false);
        }

        private float floor(float block, float sky) {
            float generalFloor = (float) SelasClientConfig.MINIMUM_LUMINANCE_FLOOR.getAsDouble();
            if (skyless) {
                return Math.max(generalFloor, generalFloor * 1.5F);
            }

            float caveFloor = (float) SelasClientConfig.CAVE_LUMINANCE_FLOOR.getAsDouble();
            float lowLight = 1.0F - combineLightContributions(saturate(block), saturate(sky));
            return Mth.lerp(lowLight, generalFloor, Math.min(generalFloor, caveFloor));
        }
    }

    private static float calculateNightAmount(float dayTick) {
        TwilightTimes twilight = getTwilightTimes();
        float duskStart = twilight.duskStart();
        float fullNightStart = twilight.fullNightStart();
        float fullNightEnd = twilight.fullNightEnd();
        float dawnEnd = twilight.dawnEnd();

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

    private static TwilightTimes getTwilightTimes() {
        int duskStart = SelasClientConfig.DUSK_TRANSITION_START_TICK.get();
        int fullNightStart = SelasClientConfig.FULL_NIGHT_START_TICK.get();
        int fullNightEnd = SelasClientConfig.FULL_NIGHT_END_TICK.get();
        int dawnEnd = SelasClientConfig.DAWN_TRANSITION_END_TICK.get();

        if (dawnEnd < duskStart && duskStart < fullNightStart && fullNightStart < fullNightEnd) {
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

    private static boolean isInWrappedRange(float value, float start, float end) {
        float length = wrappedDistance(start, end);
        if (length <= 0.0F) {
            return false;
        }
        return wrappedDistance(start, value) <= length;
    }

    private static float progressInWrappedRange(float value, float start, float end) {
        float length = wrappedDistance(start, end);
        if (length <= 0.0F) {
            return 1.0F;
        }
        return saturate(wrappedDistance(start, value) / length);
    }

    private static float wrappedDistance(float start, float end) {
        return positiveModulo(end - start, MINECRAFT_DAY_TICKS);
    }

    private static float smootherStep(float value) {
        float t = saturate(value);
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }

    private static float positiveModulo(float value, float modulo) {
        float result = value % modulo;
        return result < 0.0F ? result + modulo : result;
    }
}
