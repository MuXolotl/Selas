package com.selas.lighting;

import com.selas.lighting.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Core lighting engine (clean-room implementation).
 *
 * <p>Minecraft renders scene brightness via a 16x16 {@link LightTexture} indexed by
 * (blockLight 0..15, skyLight 0..15). Vanilla always keeps a minimum brightness floor, so even
 * at light level 0 it is never pitch black. This engine rewrites that texture on upload so that
 * low-light cells are remapped toward black, while bright cells are left essentially intact.
 *
 * <p>The {@link #LUMINANCE} table is recomputed each time the lightmap is dirty; {@link #darken}
 * applies it to every texel just before the texture is pushed to the GPU.
 */
public final class LightmapEngine {
    /** Whether the current dimension is being made darker this frame. */
    public static boolean enabled = false;

    /** Target perceptual luminance for each (block, sky) lightmap cell. */
    private static final float[][] LUMINANCE = new float[16][16];

    private LightmapEngine() {
    }

    private static boolean isDark(Level world) {
        ResourceKey<Level> dim = world.dimension();
        if (dim == Level.OVERWORLD) return Config.DARK_OVERWORLD.get();
        if (dim == Level.NETHER) return Config.DARK_NETHER.get();
        if (dim == Level.END) return Config.DARK_END.get();
        if (world.dimensionType().hasSkyLight()) return Config.DARK_DEFAULT.get();
        return Config.DARK_SKYLESS.get();
    }

    private static float skyFactor(Level world) {
        if (!Config.BLOCK_LIGHT_ONLY.get() && isDark(world)) {
            if (world.dimensionType().hasSkyLight()) {
                float angle = world.getTimeOfDay(0);
                if (angle > 0.25f && angle < 0.75f) {
                    float oldWeight = Math.max(0, (Math.abs(angle - 0.5f) - 0.2f)) * 20;
                    float moon = Config.IGNORE_MOON_PHASE.get() ? 0 : world.getMoonBrightness();
                    return Mth.lerp(oldWeight * oldWeight * oldWeight, moon * moon, 1f);
                }
                return 1;
            }
            return 0;
        }
        return 1;
    }

    /** Effective End fog brightness factor (1.0 = vanilla when End darkening disabled). */
    public static double darkEndFog() {
        return Config.DARK_END.get() ? Config.DARK_END_FOG.get() : 1.0;
    }

    /** Effective Nether fog brightness factor (1.0 = vanilla when Nether darkening disabled). */
    public static double darkNetherFog() {
        return Config.DARK_NETHER.get() ? Config.DARK_NETHER_FOG.get() : 1.0;
    }

    /** Perceptual luminance of an sRGB-ish color. */
    public static float luminance(float r, float g, float b) {
        return r * 0.2126f + g * 0.7152f + b * 0.0722f;
    }

    /**
     * Remap a single lightmap texel toward the target luminance for its (block, sky) cell.
     * Scales RGB by {@code target / currentLuminance}, clamped to 1 (never brightens).
     */
    public static int darken(int c, int blockIndex, int skyIndex) {
        float lTarget = LUMINANCE[blockIndex][skyIndex];
        float r = (c & 0xFF) / 255f;
        float g = ((c >> 8) & 0xFF) / 255f;
        float b = ((c >> 16) & 0xFF) / 255f;
        float l = luminance(r, g, b);
        float f = l > 0 ? Math.min(1, lTarget / l) : 0;

        if (f == 1f) return c;

        return 0xFF000000
                | Math.round(f * r * 255)
                | (Math.round(f * g * 255) << 8)
                | (Math.round(f * b * 255) << 16);
    }

    /**
     * Recompute the {@link #LUMINANCE} target table for the current frame. Called only when the
     * lightmap is dirty. Disabled (and {@link #enabled} cleared) when night vision, conduit power,
     * a thunder flash, or a non-dark dimension is active.
     */
    public static void updateLuminance(Minecraft client, GameRenderer worldRenderer, float tickDelta, float prevFlicker) {
        ClientLevel world = client.level;
        if (world == null || client.player == null) {
            enabled = false;
            return;
        }

        if (!isDark(world)
                || client.player.hasEffect(MobEffects.NIGHT_VISION)
                || (client.player.hasEffect(MobEffects.CONDUIT_POWER) && client.player.getWaterVision() > 0)
                || world.getSkyFlashTime() > 0) {
            enabled = false;
            return;
        }
        enabled = true;

        float dimSkyFactor = skyFactor(world);
        float ambient = world.getSkyDarken(1.0F);
        DimensionType dim = world.dimensionType();
        boolean blockAmbient = !isDark(world);

        for (int skyIndex = 0; skyIndex < 16; ++skyIndex) {
            float skyFactor = 1f - skyIndex / 15f;
            skyFactor = 1 - skyFactor * skyFactor * skyFactor * skyFactor;
            skyFactor *= dimSkyFactor;

            float min = skyFactor * 0.05f;
            float rawAmbient = ambient * skyFactor;
            float minAmbient = rawAmbient * (1 - min) + min;
            float skyBase = LightTexture.getBrightness(dim, skyIndex) * minAmbient;

            min = 0.35f * skyFactor;
            float skyRed = skyBase * (rawAmbient * (1 - min) + min);
            float skyGreen = skyBase * (rawAmbient * (1 - min) + min);
            float skyBlue = skyBase;

            if (worldRenderer.getDarkenWorldAmount(tickDelta) > 0.0F) {
                float skyDarkness = worldRenderer.getDarkenWorldAmount(tickDelta);
                skyRed = skyRed * (1.0F - skyDarkness) + skyRed * 0.7F * skyDarkness;
                skyGreen = skyGreen * (1.0F - skyDarkness) + skyGreen * 0.6F * skyDarkness;
                skyBlue = skyBlue * (1.0F - skyDarkness) + skyBlue * 0.6F * skyDarkness;
            }

            for (int blockIndex = 0; blockIndex < 16; ++blockIndex) {
                float blockFactor = 1f;
                if (!blockAmbient) {
                    blockFactor = 1f - blockIndex / 15f;
                    blockFactor = 1 - blockFactor * blockFactor * blockFactor * blockFactor;
                }

                float blockBase = blockFactor * LightTexture.getBrightness(dim, blockIndex) * (prevFlicker * 0.1F + 1.5F);
                min = 0.4f * blockFactor;
                float blockGreen = blockBase * ((blockBase * (1 - min) + min) * (1 - min) + min);
                float blockBlue = blockBase * (blockBase * blockBase * (1 - min) + min);

                float red = skyRed + blockBase;
                float green = skyGreen + blockGreen;
                float blue = skyBlue + blockBlue;

                float f = Math.max(skyFactor, blockFactor);
                min = 0.03f * f;
                red = red * (0.99F - min) + min;
                green = green * (0.99F - min) + min;
                blue = blue * (0.99F - min) + min;

                if (world.dimension() == Level.END) {
                    red = skyFactor * 0.22F + blockBase * 0.75f;
                    green = skyFactor * 0.28F + blockGreen * 0.75f;
                    blue = skyFactor * 0.25F + blockBlue * 0.75f;
                }

                if (red > 1.0F) red = 1.0F;
                if (green > 1.0F) green = 1.0F;
                if (blue > 1.0F) blue = 1.0F;

                float gamma = client.options.gamma().get().floatValue() * f;
                float invRed = 1.0F - red;
                float invGreen = 1.0F - green;
                float invBlue = 1.0F - blue;
                invRed = 1.0F - invRed * invRed * invRed * invRed;
                invGreen = 1.0F - invGreen * invGreen * invGreen * invGreen;
                invBlue = 1.0F - invBlue * invBlue * invBlue * invBlue;
                red = red * (1.0F - gamma) + invRed * gamma;
                green = green * (1.0F - gamma) + invGreen * gamma;
                blue = blue * (1.0F - gamma) + invBlue * gamma;

                min = 0.03f * f;
                red = red * (0.99F - min) + min;
                green = green * (0.99F - min) + min;
                blue = blue * (0.99F - min) + min;

                if (red > 1.0F) red = 1.0F;
                if (green > 1.0F) green = 1.0F;
                if (blue > 1.0F) blue = 1.0F;
                if (red < 0.0F) red = 0.0F;
                if (green < 0.0F) green = 0.0F;
                if (blue < 0.0F) blue = 0.0F;

                LUMINANCE[blockIndex][skyIndex] = luminance(red, green, blue);
            }
        }
    }
}
