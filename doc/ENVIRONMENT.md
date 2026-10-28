# 环境搭建手册 v1.0

状态：候选基线，等待团队实机安装验收。适用范围：Windows 10/11、x64 电脑的课程准备阶段。下面的版本是便于固定依赖的教学组合，不代表最新版本；本模板未在全体成员电脑安装验证。

## 1. 统一内容

| 组件 | 第一周候选基线或约定 | 用途 |
| --- | --- | --- |
| Python | 3.11.9，64 位 | 图像处理与后续训练 |
| NumPy | 1.26.4 | 图像数组 |
| opencv-python | 4.10.0.84；cv2 显示 4.10.0 | 图像读写与后续处理 |
| Pillow | 10.4.0 | 图像格式支持 |
| PyTorch | 2.5.1 CPU；运行时可能显示 2.5.1+cpu | 统一框架、后续模型实验 |
| torchvision | 0.20.1 CPU；可能带 +cpu 后缀 | 视觉工具 |
| GitHub Desktop | 组内记录并统一实际安装版 | Git 图形化协作 |
| Git for Windows | 使用命令行时另装，记录实际版本 | 命令行 Git、后续 IDE 集成 |
| Android Studio | 组内选定同一稳定版，登记完整版本与 Build 编号 | Android 开发工具 |
| Android JDK/JBR | 先用选定 Studio 的内置版本，记录实际版本 | IDE 与后续构建 |
| Android SDK | 同一选定平台、Platform-Tools、Build-Tools 版本 | 后续编译和真机调试 |

CPU 版用于先统一安装验收，无独立显卡也可以进行准备。后续如需 GPU 训练，由指定训练电脑另建环境，记录显卡、驱动、CUDA 对应关系；不要直接覆盖全队 CPU 基线。

## 2. 安装 Python

1. 打开 https://www.python.org/downloads/release/python-3119/ 。
2. 在 Files 中选择 Windows installer (64-bit)。
3. 安装时勾选 Add python.exe to PATH，保留 Python Launcher。
4. 安装完成后重新打开 PowerShell，输入：

```powershell
py -3.11 --version
```

预期：Python 3.11.9。已装其他版本的同学可以并存，不必卸载已有环境；本项目通过专用虚拟环境选择解释器。

## 3. 在仓库中创建虚拟环境

先使用 GitHub Desktop 克隆仓库，然后在资源管理器打开该仓库文件夹，在地址栏输入 powershell 回车。在仓库根目录逐行执行：

```powershell
py -3.11 -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements-base.txt
.\.venv\Scripts\python.exe -m pip install -r requirements-torch-cpu.txt
```

每条命令成功后再执行下一条。这里直接指定虚拟环境里的 Python，无需修改 PowerShell 执行策略或运行激活脚本。下载较慢时保留安装日志并检查网络；不要临时改包名、任意升级版本或在系统 Python 中重复安装。

## 4. 安装验收

以下只是依赖导入与环境检查，不涉及功能开发、算法实现或训练。

```powershell
.\.venv\Scripts\python.exe --version
.\.venv\Scripts\python.exe -m pip check
.\.venv\Scripts\python.exe -c "import numpy, cv2, PIL, torch, torchvision; print('numpy', numpy.__version__); print('cv2', cv2.__version__); print('Pillow', PIL.__version__); print('torch', torch.__version__); print('torchvision', torchvision.__version__); print('CUDA available:', torch.cuda.is_available())"
```

通过条件：Python 与表格一致；依赖能正常导入；pip check 输出 No broken requirements found。CPU 版的 CUDA available: False 是正常结果。

每人把检查输出保存到自己的环境验收记录。第一次成功安装的基准机，再导出完整的间接依赖列表：

```powershell
.\.venv\Scripts\python.exe -m pip freeze | Out-File -Encoding utf8 requirements-lock-win-cpu.txt
```

当前两个 requirements 文件只固定主要依赖，还不是完整锁文件。E 负责人检查导出清单并提交后，其他 Windows x64 成员按同一锁文件复现：先新建 .venv，再用下列命令安装（与前面的主要依赖安装流程二选一）：

```powershell
.\.venv\Scripts\python.exe -m pip install -r requirements-lock-win-cpu.txt --extra-index-url https://download.pytorch.org/whl/cpu
```

全部成员通过后，登记基准提交版本、Python 版本与锁文件。不同系统或 CPU 架构单独做环境记录，不宣称所有平台使用同一个二进制锁文件。requirements-lock-win-cpu.txt 要由实际成功环境导出，本模板没有虚构该文件。

## 5. Android Studio 环境准备

1. 下载入口：https://developer.android.com/studio 。先由 E 负责人选定一个稳定版，记录完整版本号、Build 编号和官方下载入口，再发给组员统一安装。
2. Windows 设备须符合所选版本官方要求。官方当前要求 Studio 至少 8 GB 内存，Studio 加模拟器至少 16 GB；内存较少的电脑优先使用真机调试，其他要求以官方安装页为准。
3. 首次启动完成 Setup Wizard。在 SDK Manager 中安装组内选定的 SDK Platform、Android SDK Platform-Tools 和 Build-Tools，登记具体版本。
4. 使用内置 JBR 运行 Studio；后续 Gradle 构建使用的 JDK 需符合项目的 AGP/Gradle 要求，不能仅凭“安装了 Java”判断一致。
5. 关于 Android 工程：本周先准备工具、SDK 和工程约定。当前 app-android 只有目录说明，没有创建功能工程。如果教师允许空白工程，可由一名负责人统一创建，再提交同一份工程和 Gradle Wrapper；其他成员拉取工程，避免每人生成不同版本配置。
6. 创建工程后，把 Android Studio、AGP、Gradle Wrapper、Kotlin、JDK、compileSdk、targetSdk、minSdk、Build-Tools 的实际选值补进环境登记。Gradle 依赖通过仓库内 Wrapper 使用，不要求每人安装一个系统 Gradle。
7. 第一周 Android 验收：Studio 能打开、SDK 安装完成、版本已登记。已有获准空白工程时，再验收 Gradle Sync 与空白页面运行；尚未创建时该项写“不适用：本周未建工程”，不能写已通过。

模板未代替团队选择 Android Studio/SDK/AGP 的完整组合。E 任务关闭前必须补齐实际选型；统一 IDE 版本之外，还要统一工程的构建配置。

## 6. Labelme 与标注协作

标注类别、形状选择和 JSON 格式由 C 负责人制定。若本周需要安装 Labelme，建议使用独立的 .venv-labelme，避免图形工具依赖改变主环境。由 C、E 共同在一台机器验证一个具体版本，再记录该版本、完整依赖清单与启动结果后推广给标注成员。这里未固定或安装 Labelme，也未交付 C 的标注规范。

## 7. 常见问题

| 现象 | 优先处理 |
| --- | --- |
| 找不到 py | 检查 Python Launcher 是否安装，安装后重新打开终端 |
| ModuleNotFoundError | 确认使用 .venv/Scripts/python.exe，且上一步安装成功 |
| numpy/OpenCV 导入冲突 | 检查是否在本项目环境；按基准锁文件新建干净环境 |
| 下载失败 | 记录失败包、错误和时间，检查网络后由 E 统一解决 |
| Studio 能打开但 Gradle 失败 | 检查同一工程的 JDK、AGP、Wrapper、SDK 和网络 |
| ARM 或其他系统电脑 | 单独登记，选择对应安装包并实测，不套用 x64 结论 |

## 官方依据

- Python：https://www.python.org/downloads/release/python-3119/
- PyTorch 2.5.1 / torchvision 0.20.1 CPU 安装：https://docs.pytorch.org/get-started/previous-versions/
- OpenCV：https://pypi.org/project/opencv-python/4.10.0.84/
- NumPy：https://pypi.org/project/numpy/1.26.4/
- Pillow：https://pypi.org/project/pillow/10.4.0/
- Android 安装与设备要求：https://developer.android.com/studio/install
- Android JDK 与构建配置：https://developer.android.com/build/jdks

核对日期：2026-10-28。正式验收版本以团队安装记录和提交的锁文件为准。
