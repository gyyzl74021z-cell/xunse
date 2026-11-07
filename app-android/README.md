# 寻色 Android 工程

第二周验收范围为页面框架、设置持久化和历史记录数据层。2026-11-04 在 Windows 上完成 `:app:assembleDebug` 并生成调试 APK。导航和重启后的本地存储仍待在模拟器或真机上复验。

2026-11-07 对齐 B/D 资料后再次通过 `:app:assembleDebug` 和 `:app:testDebugUnitTest`。新增 13 个参数转换与设置迁移回归测试通过，标签表的数量、ID 唯一性、色系引用、HEX 格式和源文件一致性校验通过。设备上的持久化复验仍待完成。

## 页面与数据

- Kotlin 和 Jetpack Compose 构成页面框架。底部 `NavigationBar` 切换 `NavHost` 中的五个页面，相机、识色与比对、寻色、图表、设置。各入口同时显示图标和文字。
- 设置通过 Preferences DataStore 保存。按 2026-11-07 B 交付参数，色弱类型为 `PROTAN`（红色弱，算法 mode=0）和 `DEUTAN`（绿色弱，算法 mode=1）。强度范围 0.0–3.0、默认 1.0、步长 0.1，控件与存储使用整数十分位 0–30，调用参数时除以 10。矫正开关独立保存，并映射为 0/1。B 未指定默认类型和开关，当前暂定 mode=0、矫正关闭，语音默认关闭。
- Room 历史记录包含 `id`、`createdAt`、`taskType` 和 `resultSummary`。在设置页使用历史调试区新增、查看、删除样例，供验证本地读写。
- 页面名称和顺序采用当前任务约定，待用 Figma 原型复核。B 的 mode、强度和独立开关取值已对齐，实际算法调用仍待接入。D 提供的标签表已纳入 assets，适用范围为衣物、彩妆。相机、识色、寻色和图表页使用占位内容。清单已声明相机权限，页面接入相机时再申请运行时权限。

## B / D 对接记录（2026-11-07）

### B 的矫正参数

设置通过 `UserSettings.toLabParams()` 转换为 `LabCorrectionParams`。`mode` 为 0 或 1，`strength` 为 0.0–3.0 的浮点数，`correctionEnabled` 为 0 或 1。矫正关闭时保留类型和强度，重新开启后继续使用已选值。实际 B 函数的名称和开关参数名称待结合 Demo 源码确认。

旧版的“红绿色弱 / 蓝黄色弱”和 0–100 强度无法直接推导出 B 的类型与强度。升级时保留语音偏好，将类型和强度回到暂定 mode=0、强度 1.0，并要求用户重新选择类型后开启矫正。新的类型选择会清除确认提示。

### D 的衣物、彩妆颜色标签

`app/src/main/assets/labels_clothes_cosmetic.json` 保留用户交付的 JSON，版本为 1，含 11 个一级色系、30 个细分类别，ID 为 0–29。`ColorLabelCatalog.load(context)` 校验并加载字典，`findById(id)` 按明确 ID 查找，未知编号返回 null。每类包含 `id / primary / name / hex`。`hex` 用作展示参考色值，不将它当作颜色判定阈值或像素真值。

设置页显示标签数量，并使用 ID 0 的“正红”生成明确标记为标签样例的历史记录，以便验证字典与存储对接。该字典描述仅涵盖衣物、彩妆，交通警示、图表等场景的类别需另行约定。

D 还需补充一份推理输出样例，确认返回类别编号的字段名、输出索引与字典 ID 的关系、是否返回置信度及其含义、未知颜色处理方式。如需区域识别，还需确认框或掩码的结构。本次继续使用现有历史摘要表，模型输出字段确认后再扩展结构。

## 构建与运行

工程使用 Gradle Wrapper 9.6.0、Android Gradle Plugin 9.4.0、Gradle JDK 25 和 Android SDK 37，最低支持 API 24。

用 Android Studio 打开 `app-android`，完成 Gradle Sync，并按工程配置安装所需 SDK。使用本机 SDK 路径配置 `local.properties`，或由 Android Studio 自动生成。Windows 上也可在 `app-android` 目录打开 PowerShell，使用工程自带的 Gradle Wrapper 构建调试包：

```powershell
.\gradlew.bat :app:assembleDebug
```

构建成功后，调试 APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。在 Android Studio 中选择 `app` 运行配置和目标设备，再点击 **Run**。构建结果、设备型号及 Android 版本记录到第二周台账。

## 设备复验步骤

1. 依次点击五个底部入口，检查当前选中项及页面内容与入口一致。
2. 在设置中选择红色弱或绿色弱，把增强强度调到非默认值，并切换独立矫正开关及语音提示。彻底关闭后重新打开，核对四项设置。强度显示小数，步长为 0.1。
3. 在设置的历史调试区添加标签样例，检查正红、红系和参考色值显示正确。关闭并重新打开应用，检查记录仍在。删除后再次重启，检查样例已移除。
4. 使用全新安装或已清空数据的测试环境核对默认 mode=0、强度 1.0、矫正关闭、语音关闭。升级旧版设置时检查重新选择类型提示。不要清除需要保留的验收记录。

## 源码与提交

Kotlin 源码位于 `app/src/main/java/com/xunse/app/`。`MainActivity.kt` 为应用入口，`ui/` 存放 Compose 界面及主题，`data/` 对应 `com.xunse.app.data`，存放 DataStore 设置和 Room 历史记录的数据代码。

新增页面沿用 Kotlin 和 Compose。类型及可组合函数使用 PascalCase，变量及普通函数使用 camelCase。页面通过 Repository 读写数据，字段与队友对齐后同步更新此文档。

开发分支为 `feat/android-week2`。按功能分批提交，提交说明使用 `feat:`、`fix:` 或 `docs:` 等前缀。提交工程源码、Gradle 配置和 Gradle Wrapper。将 `local.properties`、`.gradle/`、`build/`、APK 构建产物、签名文件及凭据留在本机。分支推送后发起 PR，并附上实际验收记录。
