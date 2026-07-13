package dev.muxolotl.selas.client.atmosphere;

import dev.muxolotl.selas.Selas;
import dev.muxolotl.selas.client.compat.ShaderPackCompat;
import dev.muxolotl.selas.config.SelasClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = Selas.MODID, value = Dist.CLIENT)
public final class SelasFog {
    private static final float LUMINANCE_R = 0.2126F;
    private static final float LUMINANCE_G = 0.7152F;
    private static final float LUMINANCE_B = 0.0722F;

    private static boolean colorInitialized;
    private static float smoothedR;
    private static float smoothedG;
    private static float smoothedB;

    private SelasFog() {
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        ClientLevel level = resolveActiveLevel();
        if (level == null) {
            colorInitialized = false;
            return;
        }

        float partialTick = (float) event.getPartialTick();

        float targetR = event.getRed();
        float targetG = event.getGreen();
        float targetB = event.getBlue();

        float skyBlend = (float) SelasClientConfig.FOG_SKY_BLEND.getAsDouble();
        if (skyBlend > 0.0F) {
            Vec3 skyColor = level.getSkyColor(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), partialTick);
            targetR = Mth.lerp(skyBlend, targetR, (float) skyColor.x);
            targetG = Mth.lerp(skyBlend, targetG, (float) skyColor.y);
            targetB = Mth.lerp(skyBlend, targetB, (float) skyColor.z);
        }

        float night = nightAmount(level);
        float rain = saturate(level.getRainLevel(partialTick));
        float thunder = saturate(level.getThunderLevel(partialTick));
        float weather = Math.max(rain, thunder);

        float nightStrength = night * (float) SelasClientConfig.NIGHT_FOG_STRENGTH.getAsDouble();
        if (nightStrength > 0.0F) {
            targetR *= 1.0F - nightStrength * 0.50F;
            targetG *= 1.0F - nightStrength * 0.35F;
            targetB *= 1.0F - nightStrength * 0.20F;
        }

        float weatherStrength = weather * (float) SelasClientConfig.WEATHER_FOG_DESATURATION.getAsDouble();
        if (weatherStrength > 0.0F) {
            float gray = luminance(targetR, targetG, targetB);
            targetR = Mth.lerp(weatherStrength, targetR, gray);
            targetG = Mth.lerp(weatherStrength, targetG, gray);
            targetB = Mth.lerp(weatherStrength, targetB, gray * 1.05F);
        }

        float smoothing = (float) SelasClientConfig.FOG_COLOR_SMOOTHING.getAsDouble();
        if (!colorInitialized) {
            smoothedR = targetR;
            smoothedG = targetG;
            smoothedB = targetB;
            colorInitialized = true;
        } else {
            smoothedR = Mth.lerp(smoothing, smoothedR, targetR);
            smoothedG = Mth.lerp(smoothing, smoothedG, targetG);
            smoothedB = Mth.lerp(smoothing, smoothedB, targetB);
        }

        event.setRed(saturate(smoothedR));
        event.setGreen(saturate(smoothedG));
        event.setBlue(saturate(smoothedB));
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getType() != FogType.NONE) {
            return;
        }

        ClientLevel level = resolveActiveLevel();
        if (level == null) {
            return;
        }

        // Scaffold only: no distance changes yet. Fog density modification arrives in a later commit.
        // Distance changes require event.setCanceled(true) to take effect.
    }

    private static float nightAmount(ClientLevel level) {
        float angle = level.getTimeOfDay(1.0F);
        if (angle <= 0.25F || angle >= 0.75F) {
            return 1.0F;
        }
        float distanceFromMidday = Math.abs(angle - 0.5F);
        return saturate(smootherStep((distanceFromMidday - 0.2F) * 20.0F));
    }

    private static float smootherStep(float value) {
        float t = saturate(value);
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }

    private static ClientLevel resolveActiveLevel() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !isEnabled()) {
            return null;
        }

        if (SelasClientConfig.ATMOSPHERE_DISABLE_WITH_SHADERS.getAsBoolean() && ShaderPackCompat.isShaderPackInUse()) {
            return null;
        }

        return shouldAffectDimension(level) ? level : null;
    }

    private static boolean isEnabled() {
        return SelasClientConfig.ATMOSPHERE_ENABLED.getAsBoolean();
    }

    private static boolean shouldAffectDimension(ClientLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        if (dimension.equals(Level.OVERWORLD)) {
            return SelasClientConfig.ATMOSPHERE_AFFECT_OVERWORLD.getAsBoolean();
        }
        if (dimension.equals(Level.NETHER)) {
            return SelasClientConfig.ATMOSPHERE_AFFECT_NETHER.getAsBoolean();
        }
        if (dimension.equals(Level.END)) {
            return SelasClientConfig.ATMOSPHERE_AFFECT_END.getAsBoolean();
        }
        return false;
    }

    private static float luminance(float r, float g, float b) {
        return r * LUMINANCE_R + g * LUMINANCE_G + b * LUMINANCE_B;
    }

    private static float saturate(float value) {
        return Mth.clamp(value, 0.0F, 1.0F);
    }
}
