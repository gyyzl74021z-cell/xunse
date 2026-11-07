# Lab 色彩矫正 PC Demo（B 提供）

归档日期：2026-11-07。来源：B 通过微信提供的 `Lab矫正Demo.zip`。
本目录中的 `lab_correct.py`、`test.jpg` 与原始压缩包逐字节一致。
测试图片的来源信息同步登记在仓库 `dataset/SOURCES.md`。

## 文件说明

- `lab_correct.py`：原始 Python + OpenCV 演示源码。
- `test.jpg`：B 随代码提供的演示图片。
- `source/Lab矫正Demo.zip`：原始压缩包。
- `对接核对说明.md`：参数映射、基础运行结果和当前功能范围。
- `SHA256SUMS.txt`：本目录各文件的 SHA-256 校验值。

## 环境与运行

使用团队已有虚拟环境和依赖，无需为本 Demo 安装 PyTorch。
已进行函数级检查的环境：OpenCV 4.10.0（依赖文件为 opencv-python==4.10.0.84）、NumPy 1.26.4。
依赖版本以仓库根目录的 `requirements-base.txt` 及团队环境手册为准。

先在 GitHub Desktop 选择包含本目录的 `feat/android-week2` 分支，点击
Repository → Show in Explorer，在仓库根目录地址栏输入 `powershell` 后回车。
确认仓库根目录已有安装好基础依赖的 `.venv`，再逐行执行以下两条命令
（只复制命令，不复制代码块边框）：

```powershell
Set-Location .\demo-pc\lab-correction
..\..\.venv\Scripts\python.exe .\lab_correct.py
```

程序按当前工作目录读取 `test.jpg`，因此需要先进入上述目录。
若要演示自己的图片，先备份原图，在本地替换同名文件；提交前检查图片修改。

## 操作

| 控件 | 含义 |
| --- | --- |
| Strength | 0–30，对应实际强度 0.0–3.0，每档 0.1；初始值 10，即 1.0 |
| Mode_0Protan_1Deutan | 0 为红色弱模式，1 为绿色弱模式；初始值 0 |
| Switch_0Origin_1Correct | 0 显示原图，1 显示矫正结果；Demo 初始值为 1 |

将鼠标焦点放在演示窗口，按 `q` 退出。

## 接口与范围

函数 `lab_color_correction(img, strength=1.2, mode=0)` 接收并返回 BGR uint8 图像。
函数签名默认强度为 1.2，与 Demo 滑块初始强度 1.0 不同；后续集成应显式传入用户设置。
独立矫正开关在主循环中实现：关闭时直接显示原图，开启时调用函数。
Android 已保存对应参数，实际 Android 滤镜尚未接入。

当前仅支持固定图片、两种模式、强度调节和单窗口原图切换。
图片选择入口、原图/效果图并排显示及蓝黄色弱模式未实现。
现有检查为两种模式与 0.0、1.0、3.0 三个强度的共 6 组函数调用；
桌面交互、实际用户辨色效果和 Android 设备运行仍待验收。
