package dev.muxolotl.selas.client.lighting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SelasMath}. Expected values were pre-computed from the
 * reference formulas so the tuned night curve, timing, moon response, floors,
 * and color packing stay locked down across refactors.
 */
class SelasMathTest {
    private static final float EPS = 1.0e-5F;

    // Default config schedule.
    private static final int DUSK = 11800;
    private static final int FULL_START = 14000;
    private static final int FULL_END = 21600;
    private static final int DAWN = 400;

    private static float night(float tick) {
        return SelasMath.nightAmount(tick, DUSK, FULL_START, FULL_END, DAWN);
    }

    @Nested
    class Scalars {
        @Test
        void saturateClamps() {
            assertEquals(0.0F, SelasMath.saturate(-1.0F), EPS);
            assertEquals(1.0F, SelasMath.saturate(2.0F), EPS);
            assertEquals(0.5F, SelasMath.saturate(0.5F), EPS);
        }

        @Test
        void lerpEndpoints() {
            assertEquals(2.0F, SelasMath.lerp(0.0F, 2.0F, 8.0F), EPS);
            assertEquals(8.0F, SelasMath.lerp(1.0F, 2.0F, 8.0F), EPS);
            assertEquals(5.0F, SelasMath.lerp(0.5F, 2.0F, 8.0F), EPS);
        }

        @Test
        void luminanceWhiteIsOne() {
            assertEquals(1.0F, SelasMath.luminance(1.0F, 1.0F, 1.0F), EPS);
            assertEquals(SelasMath.LUMINANCE_G, SelasMath.luminance(0.0F, 1.0F, 0.0F), EPS);
        }

        @Test
        void smootherStepShape() {
            assertEquals(0.0F, SelasMath.smootherStep(0.0F), EPS);
            assertEquals(0.5F, SelasMath.smootherStep(0.5F), EPS);
            assertEquals(1.0F, SelasMath.smootherStep(1.0F), EPS);
            assertEquals(0.103516F, SelasMath.smootherStep(0.25F), 1.0e-4F);
            // Clamps out-of-range input.
            assertEquals(0.0F, SelasMath.smootherStep(-0.5F), EPS);
            assertEquals(1.0F, SelasMath.smootherStep(1.5F), EPS);
        }

        @Test
        void positiveModuloWraps() {
            assertEquals(23900.0F, SelasMath.positiveModulo(-100.0F, 24000.0F), EPS);
            assertEquals(100.0F, SelasMath.positiveModulo(24100.0F, 24000.0F), EPS);
            assertEquals(0.0F, SelasMath.positiveModulo(24000.0F, 24000.0F), EPS);
        }
    }

    @Nested
    class LightCombination {
        @Test
        void screenBlendNeverOvershoots() {
            assertEquals(0.75F, SelasMath.combineLightContributions(0.5F, 0.5F), EPS);
            assertEquals(1.0F, SelasMath.combineLightContributions(1.0F, 0.0F), EPS);
            assertEquals(0.0F, SelasMath.combineLightContributions(0.0F, 0.0F), EPS);
            assertEquals(1.0F, SelasMath.combineLightContributions(1.0F, 1.0F), EPS);
        }

        @Test
        void effectiveLightRespectsPreservationAndSkyFactor() {
            assertEquals(1.0F, SelasMath.effectiveLight(1.0F, 0.0F, 1.0F, 1.0F), EPS);
            assertEquals(1.0F, SelasMath.effectiveLight(0.0F, 1.0F, 1.0F, 1.0F), EPS);
            // Full block light survives even when sky is scaled to nothing at night.
            assertEquals(1.0F, SelasMath.effectiveLight(1.0F, 1.0F, 1.0F, 0.0F), EPS);
            // Sky halved by sky factor, no block light.
            assertEquals(0.5F, SelasMath.effectiveLight(0.0F, 1.0F, 1.0F, 0.5F), EPS);
        }
    }

    @Nested
    class TargetLuminance {
        @Test
        void fullLightReachesOne() {
            assertEquals(1.0F, SelasMath.targetLuminance(1.0F, 0.01F, 1.75F, 0.0F), EPS);
        }

        @Test
        void noLightSitsAtFloor() {
            assertEquals(0.01F, SelasMath.targetLuminance(0.0F, 0.01F, 1.75F, 0.0F), EPS);
        }

        @Test
        void ambientLiftsAndClamps() {
            assertEquals(0.11F, SelasMath.targetLuminance(0.0F, 0.01F, 1.75F, 0.10F), EPS);
            assertEquals(1.0F, SelasMath.targetLuminance(1.0F, 0.01F, 1.75F, 0.10F), EPS);
        }
    }

    @Nested
    class Weather {
        @Test
        void clearWeatherDoesNotDarken() {
            assertEquals(1.0F, SelasMath.weatherFactor(0.0F, 0.0F, 0.16F, 0.32F), EPS);
        }

        @Test
        void rainDarkens() {
            assertEquals(0.84F, SelasMath.weatherFactor(1.0F, 0.0F, 0.16F, 0.32F), EPS);
        }

        @Test
        void rainAndThunderDoNotStack() {
            // Thunder (0.32) dominates rain (0.16); they must not add up.
            assertEquals(0.68F, SelasMath.weatherFactor(1.0F, 1.0F, 0.16F, 0.32F), EPS);
        }
    }

    @Nested
    class MoonPhase {
        @Test
        void endpoints() {
            assertEquals(0.0F, SelasMath.moonPhaseProgress(0.0F, 0.65F), EPS);
            assertEquals(1.0F, SelasMath.moonPhaseProgress(1.0F, 0.65F), EPS);
        }

        @Test
        void curveBrightensIntermediatePhases() {
            // With curve < 1, mid brightness lifts above linear 0.5.
            float mid = SelasMath.moonPhaseProgress(0.5F, 0.65F);
            assertEquals(0.637280F, mid, 1.0e-4F);
            assertTrue(mid > 0.5F);
        }
    }

    @Nested
    class Twilight {
        @Test
        void validOrderAccepted() {
            assertTrue(SelasMath.isValidTwilightOrder(DUSK, FULL_START, FULL_END, DAWN));
        }

        @Test
        void invalidOrdersRejected() {
            assertFalse(SelasMath.isValidTwilightOrder(14000, 11800, 21600, 400));
            assertFalse(SelasMath.isValidTwilightOrder(11800, 14000, 21600, 12000));
        }

        @Test
        void fullDayIsZeroNight() {
            assertEquals(0.0F, night(6000.0F), EPS);
            assertEquals(0.0F, night(400.0F), EPS);
            assertEquals(0.0F, night(11800.0F), EPS);
        }

        @Test
        void fullNightIsOne() {
            assertEquals(1.0F, night(14000.0F), EPS);
            assertEquals(1.0F, night(18000.0F), EPS);
            assertEquals(1.0F, night(21600.0F), EPS);
        }

        @Test
        void duskRampsUp() {
            assertEquals(0.006526F, night(12000.0F), 1.0e-4F);
            assertEquals(0.584759F, night(13000.0F), 1.0e-4F);
        }

        @Test
        void dawnRampsDown() {
            assertEquals(0.976736F, night(22000.0F), 1.0e-4F);
            assertEquals(0.500000F, night(23000.0F), 1.0e-4F);
            // Wraps past midnight and keeps falling toward day.
            assertEquals(0.023425F, night(23999.0F), 1.0e-4F);
        }

        @Test
        void monotonicAcrossDawnWrap() {
            // Dawn ends at tick 400: brightness should have decayed to zero by then.
            assertTrue(night(23500.0F) > night(23900.0F));
            assertEquals(0.0F, night(400.0F), EPS);
        }
    }

    @Nested
    class Floors {
        @Test
        void sealedCaveUsesCaveFloor() {
            assertEquals(0.004F, SelasMath.skylitFloor(0.0F, 0.0F, 0.01F, 0.004F, 0.016F, 1.0F), EPS);
        }

        @Test
        void openSkyNightUsesStarlightFloor() {
            assertEquals(0.016F, SelasMath.skylitFloor(0.0F, 1.0F, 0.01F, 0.004F, 0.016F, 1.0F), EPS);
        }

        @Test
        void openSkyDayHasNoStarlightLift() {
            assertEquals(0.01F, SelasMath.skylitFloor(0.0F, 1.0F, 0.01F, 0.004F, 0.016F, 0.0F), EPS);
        }

        @Test
        void caveStaysDarkerThanOpenNightSky() {
            float cave = SelasMath.skylitFloor(0.0F, 0.0F, 0.01F, 0.004F, 0.016F, 1.0F);
            float open = SelasMath.skylitFloor(0.0F, 1.0F, 0.01F, 0.004F, 0.016F, 1.0F);
            assertTrue(cave < open, "sealed caves must read darker than open moonless nights");
        }
    }

    @Nested
    class ColorPacking {
        @Test
        void packsRgbaLittleEndian() {
            // Layout is 0xAABBGGRR.
            assertEquals(0xFF0000FF, SelasMath.packNativeColor(1.0F, 1.0F, 0.0F, 0.0F));
            assertEquals(0xFF00FF00, SelasMath.packNativeColor(1.0F, 0.0F, 1.0F, 0.0F));
            assertEquals(0xFFFF0000, SelasMath.packNativeColor(1.0F, 0.0F, 0.0F, 1.0F));
            assertEquals(0xFFFFFFFF, SelasMath.packNativeColor(1.0F, 1.0F, 1.0F, 1.0F));
        }

        @Test
        void clampsOutOfRangeChannels() {
            assertEquals(0xFFFFFFFF, SelasMath.packNativeColor(2.0F, 2.0F, 2.0F, 2.0F));
            assertEquals(0x00000000, SelasMath.packNativeColor(-1.0F, -1.0F, -1.0F, -1.0F));
        }
    }
}
