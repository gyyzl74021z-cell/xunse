package com.xunse.app.data

import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LabSettingsMigrationTest {
    @Test
    fun migrationReplacesLegacyParametersAndPreservesVoiceAndOtherSettings() = runBlocking {
        val unrelatedKey = stringPreferencesKey("unrelated_setting")
        val previous = preferencesOf(
            SettingsKeys.LEGACY_COLOR_VISION_TYPE to "BLUE_YELLOW",
            SettingsKeys.LEGACY_FILTER_STRENGTH to 100,
            SettingsKeys.VOICE_ENABLED to true,
            unrelatedKey to "keep",
        )

        assertTrue(LabSettingsMigration.shouldMigrate(previous))
        val migrated = LabSettingsMigration.migrate(previous)

        assertEquals(0, migrated[SettingsKeys.LAB_MODE])
        assertEquals(10, migrated[SettingsKeys.STRENGTH_TENTHS])
        assertEquals(false, migrated[SettingsKeys.CORRECTION_ENABLED])
        assertEquals(true, migrated[SettingsKeys.NEEDS_TYPE_CONFIRMATION])
        assertEquals(true, migrated[SettingsKeys.VOICE_ENABLED])
        assertEquals("keep", migrated[unrelatedKey])
        assertNull(migrated[SettingsKeys.LEGACY_COLOR_VISION_TYPE])
        assertNull(migrated[SettingsKeys.LEGACY_FILTER_STRENGTH])
        assertEquals("BLUE_YELLOW", previous[SettingsKeys.LEGACY_COLOR_VISION_TYPE])
        assertFalse(LabSettingsMigration.shouldMigrate(migrated))
        assertEquals(migrated, LabSettingsMigration.migrate(migrated))
    }

    @Test
    fun freshInstallationUsesDefaultsWithoutAConfirmationPrompt() = runBlocking {
        val migrated = LabSettingsMigration.migrate(emptyPreferences())

        assertEquals(0, migrated[SettingsKeys.LAB_MODE])
        assertEquals(10, migrated[SettingsKeys.STRENGTH_TENTHS])
        assertEquals(false, migrated[SettingsKeys.CORRECTION_ENABLED])
        assertEquals(false, migrated[SettingsKeys.NEEDS_TYPE_CONFIRMATION])
        assertEquals(false, migrated[SettingsKeys.VOICE_ENABLED])
        assertFalse(LabSettingsMigration.shouldMigrate(migrated))
    }

    @Test
    fun migrationPreservesNewParametersWhenLegacyKeysAreAlsoPresent() = runBlocking {
        val previous = preferencesOf(
            SettingsKeys.LAB_MODE to 1,
            SettingsKeys.STRENGTH_TENTHS to 17,
            SettingsKeys.CORRECTION_ENABLED to true,
            SettingsKeys.LEGACY_COLOR_VISION_TYPE to "RED_GREEN",
            SettingsKeys.LEGACY_FILTER_STRENGTH to 75,
        )

        val migrated = LabSettingsMigration.migrate(previous)

        assertEquals(1, migrated[SettingsKeys.LAB_MODE])
        assertEquals(17, migrated[SettingsKeys.STRENGTH_TENTHS])
        assertEquals(true, migrated[SettingsKeys.CORRECTION_ENABLED])
        assertEquals(false, migrated[SettingsKeys.NEEDS_TYPE_CONFIRMATION])
    }
}
