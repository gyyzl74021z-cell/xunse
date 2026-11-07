package com.xunse.app.data

enum class ColorVisionType(val mode: Int, val label: String) {
    PROTAN(0, "红色弱（Protan）"),
    DEUTAN(1, "绿色弱（Deutan）"),
}

data class LabCorrectionParams(
    val mode: Int,
    val strength: Float,
    val correctionEnabled: Int,
)

data class UserSettings(
    val colorVisionType: ColorVisionType = ColorVisionType.PROTAN,
    val filterStrength: Int = 10,
    val voiceEnabled: Boolean = false,
    val correctionEnabled: Boolean = false,
    val needsTypeConfirmation: Boolean = false,
) {
    fun toLabParams(): LabCorrectionParams = LabCorrectionParams(
        mode = colorVisionType.mode,
        strength = filterStrength.coerceIn(0, 30) / 10f,
        correctionEnabled = if (correctionEnabled && !needsTypeConfirmation) 1 else 0,
    )
}

internal fun restoreUserSettings(
    mode: Int? = null,
    strengthTenths: Int? = null,
    voiceEnabled: Boolean? = null,
    correctionEnabled: Boolean? = null,
    needsTypeConfirmation: Boolean? = null,
    legacyColorVisionType: String? = null,
    legacyFilterStrength: Int? = null,
): UserSettings {
    val defaults = UserSettings()
    val type = ColorVisionType.entries.firstOrNull { it.mode == mode }
    val hasLegacyParams = legacyColorVisionType != null || legacyFilterStrength != null
    // The former types and 0–100 scale have no agreed mapping to B's parameters.
    val needsFallback = (mode != null && type == null) || (mode == null && hasLegacyParams)

    return UserSettings(
        colorVisionType = type ?: defaults.colorVisionType,
        filterStrength = if (needsFallback) defaults.filterStrength else
            (strengthTenths ?: defaults.filterStrength).coerceIn(0, 30),
        voiceEnabled = voiceEnabled ?: defaults.voiceEnabled,
        correctionEnabled = correctionEnabled ?: defaults.correctionEnabled,
        needsTypeConfirmation = needsTypeConfirmation == true || needsFallback,
    )
}
