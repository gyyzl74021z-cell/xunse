package com.xunse.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.xunse.app.data.ColorVisionType
import com.xunse.app.data.ColorLabelCatalog
import com.xunse.app.data.HistoryRepository
import com.xunse.app.data.SettingsRepository
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsScreen(
    preferences: SettingsRepository,
    history: HistoryRepository,
    writeScope: CoroutineScope,
) {
    val settings by preferences.settings.collectAsState(initial = null)
    val records by history.records.collectAsState(initial = null)
    val context = LocalContext.current.applicationContext
    val labelResult by produceState<Result<ColorLabelCatalog>?>(initialValue = null, key1 = context) {
        value = withContext(Dispatchers.IO) { runCatching { ColorLabelCatalog.load(context) } }
    }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    fun save(onFailure: () -> Unit = {}, operation: suspend () -> Unit) {
        if (saving) return
        saving = true
        message = null
        writeScope.launch {
            try {
                operation()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                android.util.Log.e("XunseStorage", "Local write failed", error)
                onFailure()
                message = "保存失败，请重试。"
            } finally {
                saving = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("设置", style = MaterialTheme.typography.headlineMedium)
        val current = settings
        if (current == null) {
            Text("正在读取偏好设置…")
        } else {
            if (current.needsTypeConfirmation) {
                Text("已更新矫正参数，请重新选择色弱类型。", color = MaterialTheme.colorScheme.error)
            }
            Text("色弱类型", style = MaterialTheme.typography.titleMedium)
            Column(Modifier.selectableGroup()) {
                ColorVisionType.entries.forEach { type ->
                    Row(
                        modifier = Modifier.fillMaxWidth().selectable(
                            selected = current.colorVisionType == type,
                            enabled = !saving,
                            role = Role.RadioButton,
                            onClick = { save { preferences.setColorVisionType(type) } },
                        ).padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(selected = current.colorVisionType == type, onClick = null)
                        Text(type.label)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("色彩矫正")
                Switch(
                    modifier = Modifier.semantics { contentDescription = "色彩矫正" },
                    checked = current.correctionEnabled,
                    enabled = !saving && !current.needsTypeConfirmation,
                    onCheckedChange = { enabled -> save { preferences.setCorrectionEnabled(enabled) } },
                )
            }
            var strength by remember(current.filterStrength) {
                mutableFloatStateOf(current.filterStrength.toFloat())
            }
            Text("滤镜强度 · ${String.format(Locale.ROOT, "%.1f", strength.roundToInt() / 10f)}")
            Slider(
                modifier = Modifier.semantics { contentDescription = "滤镜强度" },
                value = strength,
                valueRange = 0f..30f,
                steps = 29,
                enabled = !saving,
                onValueChange = { strength = it },
                onValueChangeFinished = {
                    save(onFailure = { strength = current.filterStrength.toFloat() }) {
                        preferences.setFilterStrength(strength.roundToInt())
                    }
                },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("语音提示")
                Switch(
                    modifier = Modifier.semantics { contentDescription = "语音提示" },
                    checked = current.voiceEnabled,
                    enabled = !saving,
                    onCheckedChange = { enabled -> save { preferences.setVoiceEnabled(enabled) } },
                )
            }
        }
        HorizontalDivider()
        Text("历史记录 · 存储验收", style = MaterialTheme.typography.titleMedium)
        val labels = labelResult?.getOrNull()
        val sampleLabel = labels?.findById(0)
        when {
            labelResult == null -> Text("正在读取衣物/彩妆标签…")
            labels == null -> Text("标签表读取失败，请重新进入设置页。", color = MaterialTheme.colorScheme.error)
            else -> Text("衣物/彩妆标签 · ${labels.primary.size} 个色系 / ${labels.classes.size} 个类别")
        }
        Text("下面使用标签样例验证本地记录的读写。")
        Button(
            enabled = !saving && sampleLabel != null,
            onClick = {
                sampleLabel?.let { label ->
                    save {
                        history.addRecord(
                            "衣物/彩妆标签验收",
                            "标签样例 #${label.id} · ${label.primary}系 · ${label.name} · ${label.hex}",
                        )
                    }
                }
            },
        ) {
            Text("添加标签测试记录")
        }
        message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        val savedRecords = records
        when {
            savedRecords == null -> Text("正在读取历史记录…")
            savedRecords.isEmpty() -> Text("暂无记录")
            else -> savedRecords.forEach { record ->
                val formattedTime = DateFormat.getDateTimeInstance().format(Date(record.createdAt))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(record.resultSummary)
                        Text("${record.taskType} · $formattedTime", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(
                        enabled = !saving,
                        onClick = { save { history.deleteRecord(record.id) } },
                    ) {
                        Text("删除")
                    }
                }
            }
        }
    }
}
