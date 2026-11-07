package com.xunse.app.data

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(
    name = "user_settings",
    produceMigrations = { listOf(LabSettingsMigration) },
)

class SettingsRepository(context: Context) {
    private val dataStore = context.applicationContext.settingsDataStore

    val settings: Flow<UserSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { it.toUserSettings() }

    suspend fun setColorVisionType(type: ColorVisionType) {
        dataStore.edit {
            it[SettingsKeys.LAB_MODE] = type.mode
            it[SettingsKeys.NEEDS_TYPE_CONFIRMATION] = false
        }
    }

    suspend fun setFilterStrength(strength: Int) {
        dataStore.edit { it[SettingsKeys.STRENGTH_TENTHS] = strength.coerceIn(0, 30) }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.VOICE_ENABLED] = enabled }
    }

    suspend fun setCorrectionEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.CORRECTION_ENABLED] = enabled }
    }
}

internal object SettingsKeys {
    val LAB_MODE = intPreferencesKey("lab_mode_v2")
    val STRENGTH_TENTHS = intPreferencesKey("lab_strength_tenths_v2")
    val CORRECTION_ENABLED = booleanPreferencesKey("lab_correction_enabled_v2")
    val NEEDS_TYPE_CONFIRMATION = booleanPreferencesKey("lab_needs_type_confirmation_v2")
    val PARAMS_VERSION = intPreferencesKey("lab_params_version")
    val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
    val LEGACY_COLOR_VISION_TYPE = stringPreferencesKey("color_vision_type")
    val LEGACY_FILTER_STRENGTH = intPreferencesKey("filter_strength")
}

private fun Preferences.toUserSettings(): UserSettings = restoreUserSettings(
    mode = this[SettingsKeys.LAB_MODE],
    strengthTenths = this[SettingsKeys.STRENGTH_TENTHS],
    voiceEnabled = this[SettingsKeys.VOICE_ENABLED],
    correctionEnabled = this[SettingsKeys.CORRECTION_ENABLED],
    needsTypeConfirmation = this[SettingsKeys.NEEDS_TYPE_CONFIRMATION],
    legacyColorVisionType = this[SettingsKeys.LEGACY_COLOR_VISION_TYPE],
    legacyFilterStrength = this[SettingsKeys.LEGACY_FILTER_STRENGTH],
)

internal object LabSettingsMigration : DataMigration<Preferences> {
    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        (currentData[SettingsKeys.PARAMS_VERSION] ?: 0) < 2

    override suspend fun migrate(currentData: Preferences): Preferences {
        val settings = currentData.toUserSettings()
        return currentData.toMutablePreferences().apply {
            this[SettingsKeys.LAB_MODE] = settings.colorVisionType.mode
            this[SettingsKeys.STRENGTH_TENTHS] = settings.filterStrength
            this[SettingsKeys.CORRECTION_ENABLED] = settings.correctionEnabled
            this[SettingsKeys.NEEDS_TYPE_CONFIRMATION] = settings.needsTypeConfirmation
            this[SettingsKeys.VOICE_ENABLED] = settings.voiceEnabled
            this[SettingsKeys.PARAMS_VERSION] = 2
            remove(SettingsKeys.LEGACY_COLOR_VISION_TYPE)
            remove(SettingsKeys.LEGACY_FILTER_STRENGTH)
        }
    }

    override suspend fun cleanUp() = Unit
}
