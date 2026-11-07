package com.xunse.app.data

import android.content.Context
import org.json.JSONObject

/** D 交付的衣物、彩妆颜色类别。hex 用于展示参考色块。 */
data class ColorLabel(
    val id: Int,
    val primary: String,
    val name: String,
    val hex: String,
)

class ColorLabelCatalog private constructor(
    val version: Int,
    val primary: List<String>,
    val classes: List<ColorLabel>,
) {
    private val labelsById = classes.associateBy { it.id }

    /** 未知 ID 返回 null，由调用方显示未知类别。 */
    fun findById(id: Int): ColorLabel? = labelsById[id]

    companion object {
        fun load(context: Context): ColorLabelCatalog {
            val source = context.assets.open("labels_clothes_cosmetic.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val root = JSONObject(source)
            val version = root.getInt("version")
            require(version == 1) { "Unsupported color label version: $version" }
            val primaryJson = root.getJSONArray("primary")
            val primary = List(primaryJson.length()) { primaryJson.getString(it) }
            require(primary.isNotEmpty() && primary.distinct().size == primary.size)
            val classesJson = root.getJSONArray("classes")
            val classes = List(classesJson.length()) { index ->
                val item = classesJson.getJSONObject(index)
                ColorLabel(
                    id = item.getInt("id"),
                    primary = item.getString("primary"),
                    name = item.getString("name"),
                    hex = item.getString("hex"),
                )
            }
            require(classes.isNotEmpty() && classes.map { it.id }.distinct().size == classes.size)
            require(classes.all {
                it.id >= 0 && it.primary in primary && it.name.isNotBlank() &&
                    it.hex.matches(Regex("#[0-9A-Fa-f]{6}"))
            }) { "Invalid clothing/cosmetic color labels" }
            return ColorLabelCatalog(version, primary, classes)
        }
    }
}
