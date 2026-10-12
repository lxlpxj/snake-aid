# APK 打包环境与版本记录

> 记录日期：2026-10-11
> 打包方式：uni-app **离线打包**（不用 HBuilderX 云打包，全部本地命令行完成）
> 最后一次完整验证成功的打包：2026-10-10（产物 29.2MB）

---

## 一、版本总表

### 1. 运行环境

| 组件 | 版本 | 路径 / 备注 |
|---|---|---|
| Node.js | **v24.19.0** | `C:\Program Files\nodejs` |
| npm | **11.17.0** | 必须用 `npm.cmd` 调用（见坑 2） |
| JDK | **Amazon Corretto OpenJDK 17.0.12 LTS**（2024-07-16） | `C:\software\HBuilderX\plugins\amazon-corretto`（HBuilderX 自带）。**系统无全局 JAVA_HOME，每次构建前必须先设置**（见坑 11） |

### 2. uni-app 编译层（决定产物能否出 App 包）

| 组件 | 版本 | 说明 |
|---|---|---|
| **5.26 编译器**（实际使用） | `3.0.0-5020620260917001` | Vue3；在 `%TEMP%\uni-tpl-test\tpl` 模板环境中 |
| 4.15 编译器（**禁用**） | `3.0.0-4010520240507001` | 项目自带 `snake-aid-app/package.json`，= HBuilderX 4.1.5（2024-05-07）。**对 app-plus 平台会静默降级产出 H5，不能用来打 App 包** |
| vue | ^3.4.21 | 两套编译器一致 |
| vue-i18n | ^9.1.9 | 同上 |

### 3. DCloud 离线 SDK 层

| 组件 | 版本 / 值 |
|---|---|
| Android 离线打包 SDK | **Android-SDK@5.26.82680_20260914** |
| Gradle 工程 | `HBuilder-Integrate-AS`（module：`simpleDemo`） |
| appid | **`__UNI__D737C0C`** |
| dcloud_appkey | **`2f41d97b9097ae9fcc554d156891e578`**（缺它真机报"未配置 appkey"） |
| 应用包名 | **`cn.snake.aid`**（build.gradle `applicationId`） |
| 应用名称 / 版本 | 蛇伤急救 / 1.0.0（versionCode 100），来自 `manifest.json` |

### 4. Android 构建层

| 组件 | 版本 |
|---|---|
| Gradle | **8.11.1**（wrapper 分发：`mirrors.cloud.tencent.com/gradle/gradle-8.11.1-bin.zip`） |
| Android Gradle Plugin (AGP) | **8.7.3** |
| compileSdk | **35** |
| targetSdk | 33 |
| minSdk | 21 |
| Android platform | **android-35** |
| build-tools | **35.0.0** |
| Android SDK 根 | `C:\software\Android\Sdk`（写在工程 `local.properties`，注意与下载目录 `C:\software\Android\AndroidAndroidSDK` 不是同一个） |

> AGP 8.7.3 **要求 JDK 17**，用 JDK 25 会构建失败。

### 5. 签名

| 项 | 值 |
|---|---|
| 证书文件 | `HBuilder-Integrate-AS\simpleDemo\test.jks` |
| 来源 | DCloud SDK 自带的**示例证书**（2021-03-17），仅够测试 |
| keyAlias | `key0` |
| 正式发布 | 需自申请证书替换（keytool 生成，指纹填 DCloud 后台） |

---

## 二、关键路径

```
项目源码            c:\project\app\snakebite-first-aid\snake-aid-app\
  统一 API 开关      snake-aid-app\utils\api.js          ← App 的服务器地址只改这一处

模板编译环境        %TEMP%\uni-tpl-test\tpl              ← 5.26 编译器，临时目录，可能被系统清理
  源码副本           tpl\src\
  编译命令目录       tpl\
  编译产物           tpl\dist\build\app\                 ← 含 pages/ static/ app-service.js

离线 SDK 工程       C:\software\Android\AndroidAndroidSDK\Android-SDK@5.26.82680_20260914\
                      HBuilder-Integrate-AS\
  AndroidManifest    simpleDemo\src\main\AndroidManifest.xml   ← dcloud_appkey 在这
  资源目标目录       simpleDemo\src\main\assets\apps\__UNI__D737C0C\www\
  签名               simpleDemo\test.jks
  产物               simpleDemo\build\outputs\apk\release\simpleDemo-release.apk
```

---

## 三、打包流程（7 步）

**1. 同步源码到模板**（源码有改动就必须做；模板在 TEMP，可能整个丢失需重建）

```powershell
$s="c:\project\app\snakebite-first-aid\snake-aid-app"
$d="$env:TEMP\uni-tpl-test\tpl\src"
foreach($dir in @("pages","components","common","static","utils")){
  robocopy "$s\$dir" "$d\$dir" /E /NFL /NDL /NJH /NJS /NP | Out-Null }
foreach($file in @("App.vue","main.js","manifest.json","pages.json","uni.scss","uni.promisify.adaptor.js")){
  Copy-Item "$s\$file" "$d\$file" -Force }
```
> 只拷源码，**不要**拷项目的 `package.json` / `vite.config.js`，否则会覆盖模板编译环境。

**2. 编译 app-plus 资源**（cwd = 模板 `tpl\`）

```powershell
& "C:\Program Files\nodejs\npm.cmd" run build:custom app-plus
```
成功标志：`编译器版本：5.26（vue3）` + `DONE Build complete.`

**3. 校验产物地址**

```powershell
Select-String -Path "$env:TEMP\uni-tpl-test\tpl\dist\build\app\app-service.js" -Pattern "http://localhost:8080"
# 应命中，且与 snake-aid-app\utils\api.js 的 APP-PLUS 值一致
# （真机调试包应是电脑局域网 IP，届时按实际 IP 校验）
```

**4. 拷入 Android 工程**（/MIR 镜像，清掉旧资源）

```powershell
robocopy "$env:TEMP\uni-tpl-test\tpl\dist\build\app" `
  "C:\software\Android\...\HBuilder-Integrate-AS\simpleDemo\src\main\assets\apps\__UNI__D737C0C\www" /MIR
# robocopy 退出码 1 = 成功（0~7 都算成功，≥8 才是失败）
```

**5. Gradle 出包**（cwd = `HBuilder-Integrate-AS\`）

```powershell
$env:JAVA_HOME="C:\software\HBuilderX\plugins\amazon-corretto"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
& ".\gradlew.bat" :simpleDemo:assembleRelease --console=plain
```

**6. 产物**：`simpleDemo\build\outputs\apk\release\simpleDemo-release.apk`

**7. 验证**：aapt 查包名 `cn.snake.aid`、appkey 已写入；装机后确认接口地址与 `api.js` APP-PLUS 值一致（默认 `localhost:8080`，手机直连真机需改为电脑局域网 IP）

---

## 四、已知坑（每次打包都可能踩）

| # | 坑 | 对策 |
|---|---|---|
| 1 | 项目自带 4.15 CLI 编译 `app-plus` **静默降级成 H5**，不报错 | 必须走上面的 5.26 模板环境 |
| 2 | PowerShell 执行策略禁用 `.ps1`，直接敲 `npm` 报 `cannot be loaded` | 用 **`npm.cmd`**（cmd 形式） |
| 3 | `cmd /c` 被安全策略拦截 | 全程用 PowerShell 原生命令 |
| 4 | robocopy 退出码 1/2/3 都是成功，PowerShell 会把它当失败码 | 判 `≥8` 才算失败 |
| 5 | `App.vue` 里全局组件异步 import 在 App 端打包报错 | 改**静态 import**（已完成：SnakeAnim/FirstAidAnim/GlobalTools） |
| 6 | `manifest.json` 带 BOM 会让 appid 识别异常 | 保存为 UTF-8 **无 BOM** |
| 7 | Android 9+ 禁明文 http | 报 `CLEARTEXT_NOT_PERMITTED` 时需在 AndroidManifest 开 `usesCleartextTraffic` |
| 8 | 丢了 appkey | 真机报"未配置 appkey 或配置错误"，回 DCloud 后台取 `2f41d97b9097ae9fcc554d156891e578` |
| 9 | AndroidManifest 没有 `package="..."` 属性 | 正常，AGP 8.x 改用 build.gradle 的 `applicationId` |
| 10 | `%TEMP%` 被清理 | 模板环境（含 node_modules）会整个丢失，需重建：`npm create uni`（uni-preset-vue 模板）+ 装 5.26 依赖 |
| 11 | 新 Shell 里 `JAVA_HOME is not set`，gradle 秒失败、**看起来像"APK 没生成"** | 构建前先设 `$env:JAVA_HOME="C:\software\HBuilderX\plugins\amazon-corretto"`。**别用 Android Studio/DataGrip 的 JBR**——它们是 JDK 25，AGP 8.7.3 不认 |

---

## 五、什么时候这份记录需要更新

- 换 uni-app 编译器或 DCloud SDK 版本 → 更新一.2 / 一.3（**编译器与 SDK 大版本需配套**）
- 升 AGP / Gradle → 连带核对 JDK 版本要求
- 换签名证书（测试 → 正式）→ 更新一.5
- 服务器地址变更 → 改 `snake-aid-app\utils\api.js`（不影响版本表，但影响步骤 3 的校验值）
