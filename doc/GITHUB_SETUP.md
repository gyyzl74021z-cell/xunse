# GitHub 建库与上传步骤

适用对象：初次使用 GitHub 的课程项目小组。建议使用自己的个人账号创建仓库，每名组员使用各自的账号协作。

## 1. 账号与所有权

你创建后，仓库地址类似 `https://github.com/你的用户名/xunse`。你是仓库所有者，组员接受邀请后成为协作者。仓库放在你的账号下不影响组员分别提交与记录贡献；README 应写明团队成员和分工。

个人仓库只有一位所有者。对本课程小组而言，个人仓库足够；如老师指定学校组织或 GitHub Classroom，则按老师指定的入口创建。每人使用自己的账号，无需共享登录密码。

## 2. 在网页创建公开仓库

1. 登录 https://github.com/ ，打开 https://github.com/new 。
2. Owner 选择自己的账号。
3. Repository name 填 `xunse`。
4. Description 填“寻色：面向色弱用户的色彩增强与图像辅助阅读 APP｜图像处理课程小组项目”。
5. 选择 Public，与本次任务要求一致。
6. 启用 Add README。Add .gitignore 和 Choose a license 暂选 None；本地初始化包已提供忽略规则，许可证由团队决定。
7. 点击 Create repository，保存网页地址。

## 3. 用 GitHub Desktop 上传初始化包

推荐此方法，无需先学习 Git 命令。下载地址：https://desktop.github.com/download/ 。

1. 安装 GitHub Desktop，登录刚才的个人账号。
2. 选择 File → Clone repository，在 GitHub.com 列表中选刚创建的 `xunse`，设置本地路径并 Clone。也可在 URL 页粘贴仓库地址。
3. 解压初始化包。进入 `xunse-starter` 文件夹，将里面的所有内容复制到刚克隆的 `xunse` 文件夹内；README.md 选择替换。
4. 注意复制的是文件夹内的内容，避免出现 `xunse/xunse-starter/dataset` 这样的多余层级。Windows 可开启“查看 → 显示 → 隐藏的项目”，确认 .gitignore 和 .gitattributes 也复制成功；保留克隆时生成的 .git 文件夹。
5. 修改 README 中的负责人、GitHub 用户名和仓库链接。
6. 回到 GitHub Desktop，在 Changes 中检查将提交的文件。Summary 填 `chore: 初始化寻色项目目录与规范`。
7. 点击 Commit to main，再点击 Push origin。Commit 是保存本地版本，Push 才会上传 GitHub。
8. 回到网页刷新，确认 dataset、model、app-android、doc、demo-pc、test-report 六个目录都能打开。

## 4. 邀请组员

1. 收集每位组员的 GitHub 用户名。
2. 仓库网页 → Settings → Collaborators → Add people。
3. 逐个搜索用户名并发出邀请。
4. 提醒组员登录自己的账号，接受网页或邮件中的邀请。
5. 让组员各自 Clone 同一个仓库。公开仓库任何人可以浏览，但直接推送需要相应写入权限。

## 5. 每人完成一次协作练习

1. 在 GitHub Desktop 切回 main，Fetch origin；如果出现 Pull origin，先拉取更新。
2. Current branch → New branch，创建自己的分支，例如 `docs/member-alice`。分支名中的 alice 替换为自己的简短标识。
3. 编辑 README 中自己负责的一行，或在 doc/TEAM_ENVIRONMENT.md 登记自己的电脑情况。
4. 提交：`docs: 登记个人分工与环境信息`。
5. Publish branch / Push origin，然后 Create Pull Request。
6. 在网页确认 base 是 main、compare 是自己的分支，说明修改内容并创建 PR。
7. 由另一位组员检查后合并。组员切回 main，再 Fetch/Pull 获取合并结果。

PR（Pull Request）就是将某个分支的修改申请合并到主分支的记录。第一周做到“每人提交一次、每个 PR 有一次同伴检查”，即可形成清晰协作证据。

## 6. 保存第一周证据

保存公开仓库链接、仓库首页截图、成员已接受邀请的记录、已合并 PR 链接、环境验收记录和台账。检查截图内容后再决定是否放进公开仓库。

## 官方参考

- 建库：https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-new-repository
- 邀请协作者：https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/repository-access-and-collaboration/inviting-collaborators-to-a-personal-repository
- 个人仓库权限：https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/repository-access-and-collaboration/permission-levels-for-a-personal-account-repository

核对日期：2026-10-28。界面语言不同，按钮名称可能显示对应中文。
