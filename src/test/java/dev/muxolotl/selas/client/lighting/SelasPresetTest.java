package dev.muxolotl.selas.client.lighting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;

/**
 * Tests for the one-click look presets. These lock down the invariants that make
 * the presets a coherent "mood ladder" and guarantee CUSTOM/BALANCED behave as
 * documented, without needing the game running.
 */
class SelasPresetTest {
    // Config ranges (mirror of defineInRange bounds) each look value must respect.
    private static final float FLOOR_MAX = 0.25F;

    @Test
    void customHasNoLook() {
        assertTrue(SelasPreset.CUSTOM.isCustom());
        assertThrows(IllegalStateException.class, SelasPreset.CUSTOM::look);
    }

    @Test
    void nonCustomPresetsExposeALook() {
        for (SelasPreset preset : EnumSet.complementOf(EnumSet.of(SelasPreset.CUSTOM))) {
            assertFalse(preset.isCustom(), preset + " should not be custom");
            assertEquals(preset.look(), preset.look(), preset + " look must be retrievable");
        }
    }

    @Test
    void balancedMatchesShippedDefaults() {
        // BALANCED must equal the config's default slider values, so switching
        // between CUSTOM (at defaults) and BALANCED looks identical.
        SelasPreset.Look b = SelasPreset.BALANCED.look();
        assertEquals(0.075F, b.moonlessNightSkyFactor());
        assertEquals(0.34F, b.fullMoonSkyFactor());
        assertEquals(0.65F, b.moonPhaseCurve());
        assertEquals(0.16F, b.rainDarkening());
        assertEquals(0.32F, b.thunderDarkening());
        assertEquals(0.010F, b.minimumLuminanceFloor());
        assertEquals(0.004F, b.caveLuminanceFloor());
        assertEquals(0.016F, b.starlightLuminanceFloor());
        assertEquals(1.75F, b.darknessCurve());
        assertEquals(1.00F, b.blockLightPreservation());
        assertEquals(0.38F, b.nightDesaturation());
        assertEquals(0.07F, b.nightCoolTint());
        assertEquals(0.03F, b.moonWarmth());
        assertEquals(0.14F, b.netherLightFactor());
        assertEquals(0.06F, b.netherWarmTint());
        assertEquals(0.09F, b.endLightFactor());
        assertEquals(0.05F, b.endCoolTint());
        assertEquals(0.10F, b.skylessDimensionLightFactor());
    }

    @Test
    void allLookValuesWithinConfigRanges() {
        for (SelasPreset preset : EnumSet.complementOf(EnumSet.of(SelasPreset.CUSTOM))) {
            SelasPreset.Look l = preset.look();
            inRange(preset, "moonless", l.moonlessNightSkyFactor(), 0F, 1F);
            inRange(preset, "fullMoon", l.fullMoonSkyFactor(), 0F, 1F);
            inRange(preset, "moonCurve", l.moonPhaseCurve(), 0.5F, 3.0F);
            inRange(preset, "rain", l.rainDarkening(), 0F, 1F);
            inRange(preset, "thunder", l.thunderDarkening(), 0F, 1F);
            inRange(preset, "minFloor", l.minimumLuminanceFloor(), 0F, FLOOR_MAX);
            inRange(preset, "caveFloor", l.caveLuminanceFloor(), 0F, FLOOR_MAX);
            inRange(preset, "starlight", l.starlightLuminanceFloor(), 0F, FLOOR_MAX);
            inRange(preset, "darkCurve", l.darknessCurve(), 0.25F, 4.0F);
            inRange(preset, "blockLight", l.blockLightPreservation(), 0F, 1.5F);
            inRange(preset, "desat", l.nightDesaturation(), 0F, 1F);
            inRange(preset, "coolTint", l.nightCoolTint(), 0F, 0.5F);
            inRange(preset, "moonWarmth", l.moonWarmth(), 0F, 0.3F);
            inRange(preset, "nether", l.netherLightFactor(), 0F, 1F);
            inRange(preset, "netherWarm", l.netherWarmTint(), 0F, 0.5F);
            inRange(preset, "end", l.endLightFactor(), 0F, 1F);
            inRange(preset, "endCool", l.endCoolTint(), 0F, 0.5F);
            inRange(preset, "skyless", l.skylessDimensionLightFactor(), 0F, 1F);
        }
    }

    @Test
    void presetsFormADarkeningLadder() {
        // From gentle to oppressive, night sky brightness should strictly fall
        // and the darkness curve should strictly rise.
        SelasPreset.Look vp = SelasPreset.VANILLA_PLUS.look();
        SelasPreset.Look ba = SelasPreset.BALANCED.look();
        SelasPreset.Look re = SelasPreset.REALISTIC.look();
        SelasPreset.Look ho = SelasPreset.HORROR.look();

        assertStrictlyDecreasing(vp.moonlessNightSkyFactor(), ba.moonlessNightSkyFactor(),
                re.moonlessNightSkyFactor(), ho.moonlessNightSkyFactor());
        assertStrictlyDecreasing(vp.fullMoonSkyFactor(), ba.fullMoonSkyFactor(),
                re.fullMoonSkyFactor(), ho.fullMoonSkyFactor());
        assertStrictlyDecreasing(vp.netherLightFactor(), ba.netherLightFactor(),
                re.netherLightFactor(), ho.netherLightFactor());
        assertStrictlyDecreasing(vp.endLightFactor(), ba.endLightFactor(),
                re.endLightFactor(), ho.endLightFactor());

        assertStrictlyIncreasing(vp.darknessCurve(), ba.darknessCurve(),
                re.darknessCurve(), ho.darknessCurve());
        assertStrictlyIncreasing(vp.nightDesaturation(), ba.nightDesaturation(),
                re.nightDesaturation(), ho.nightDesaturation());
    }

    @Test
    void cavesStayDarkerThanOpenNightInEveryPreset() {
        // The starlight floor must exceed the cave floor so sealed spaces read
        // darker than moonless open sky, in every preset.
        for (SelasPreset preset : EnumSet.complementOf(EnumSet.of(SelasPreset.CUSTOM))) {
            SelasPreset.Look l = preset.look();
            assertTrue(l.caveLuminanceFloor() < l.starlightLuminanceFloor(),
                    preset + ": cave floor must stay below starlight floor");
        }
    }

    private static void inRange(SelasPreset p, String name, float v, float lo, float hi) {
        assertTrue(v >= lo && v <= hi, p + "." + name + "=" + v + " outside [" + lo + ", " + hi + "]");
    }

    private static void assertStrictlyDecreasing(float... values) {
        for (int i = 1; i < values.length; i++) {
            assertTrue(values[i] < values[i - 1],
                    "expected strictly decreasing, but " + values[i] + " >= " + values[i - 1]);
        }
    }

    private static void assertStrictlyIncreasing(float... values) {
        for (int i = 1; i < values.length; i++) {
            assertTrue(values[i] > values[i - 1],
                    "expected strictly increasing, but " + values[i] + " <= " + values[i - 1]);
        }
    }
}
