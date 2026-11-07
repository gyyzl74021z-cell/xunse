package com.xunse.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LabCorrectionParamsTest {
    @Test
    fun freshSettingsUseTheAgreedDefaults() {
        val settings = restoreUserSettings()

        assertEquals(UserSettings(), settings)
        assertEquals(LabCorrectionParams(0, 1.0f, 0), settings.toLabParams())
        assertFalse(settings.needsTypeConfirmation)
    }

    @Test
    fun protanAndDeutanUseTheAgreedModes() {
        assertEquals(0, UserSettings(ColorVisionType.PROTAN).toLabParams().mode)
        assertEquals(1, UserSettings(ColorVisionType.DEUTAN).toLabParams().mode)
        assertEquals(ColorVisionType.DEUTAN, restoreUserSettings(mode = 1).colorVisionType)
    }

    @Test
    fun tenthsConvertToFloatAcrossTheFullRange() {
        for (tenths in 0..30) {
            val params = UserSettings(filterStrength = tenths).toLabParams()
            assertEquals(tenths / 10f, params.strength, 0.00001f)
        }
    }

    @Test
    fun outOfRangeStrengthIsClampedBeforeConversionAndAfterRestore() {
        assertEquals(0.0f, UserSettings(filterStrength = Int.MIN_VALUE).toLabParams().strength, 0f)
        assertEquals(3.0f, UserSettings(filterStrength = Int.MAX_VALUE).toLabParams().strength, 0f)
        assertEquals(0, restoreUserSettings(mode = 0, strengthTenths = -1).filterStrength)
        assertEquals(30, restoreUserSettings(mode = 1, strengthTenths = 31).filterStrength)
    }

    @Test
    fun correctionSwitchIsIndependentOfStrengthAndVoice() {
        val enabledAtZero = UserSettings(filterStrength = 0, correctionEnabled = true)
        val disabledAtMaximum = UserSettings(filterStrength = 30, voiceEnabled = true)

        assertEquals(LabCorrectionParams(0, 0.0f, 1), enabledAtZero.toLabParams())
        assertEquals(LabCorrectionParams(0, 3.0f, 0), disabledAtMaximum.toLabParams())
    }

    @Test
    fun bothLegacyTypesAndLegacyStrengthFallBackWithoutInventingAMapping() {
        for (legacyType in listOf("RED_GREEN", "BLUE_YELLOW")) {
            for (legacyStrength in listOf(0, 10, 50, 100)) {
                val settings = restoreUserSettings(
                    legacyColorVisionType = legacyType,
                    legacyFilterStrength = legacyStrength,
                    voiceEnabled = true,
                )

                assertEquals(LabCorrectionParams(0, 1.0f, 0), settings.toLabParams())
                assertTrue(settings.needsTypeConfirmation)
                assertTrue(settings.voiceEnabled)
            }
        }
    }

    @Test
    fun legacyStrengthWithoutATypeAlsoNeedsConfirmation() {
        val settings = restoreUserSettings(legacyFilterStrength = 20)

        assertEquals(10, settings.filterStrength)
        assertEquals(ColorVisionType.PROTAN, settings.colorVisionType)
        assertTrue(settings.needsTypeConfirmation)
    }

    @Test
    fun unknownModesFallBackAndKeepCorrectionOffUntilTypeConfirmation() {
        for (unknownMode in listOf(-1, 2, Int.MAX_VALUE)) {
            val settings = restoreUserSettings(
                mode = unknownMode,
                strengthTenths = 29,
                voiceEnabled = true,
                correctionEnabled = true,
            )

            assertEquals(LabCorrectionParams(0, 1.0f, 0), settings.toLabParams())
            assertTrue(settings.needsTypeConfirmation)
            assertTrue(settings.voiceEnabled)
            assertTrue(settings.correctionEnabled)
        }
    }

    @Test
    fun explicitNewModeTakesPrecedenceOverStaleLegacyValues() {
        val settings = restoreUserSettings(
            mode = 1,
            strengthTenths = 23,
            legacyColorVisionType = "BLUE_YELLOW",
            legacyFilterStrength = 100,
        )

        assertEquals(LabCorrectionParams(1, 2.3f, 0), settings.toLabParams())
        assertFalse(settings.needsTypeConfirmation)
    }

    @Test
    fun persistedConfirmationFlagKeepsTheFallbackModeDisabled() {
        val settings = restoreUserSettings(
            mode = 0,
            correctionEnabled = true,
            needsTypeConfirmation = true,
        )

        assertTrue(settings.needsTypeConfirmation)
        assertEquals(0, settings.toLabParams().correctionEnabled)
    }
}
