# AI Todo Starter

一個使用 **Kotlin + Jetpack Compose + Room** 開發的 Android 待辦事項範例專案，包含：
- 待辦新增、搜尋、完成/取消完成、刪除
- 本機資料持久化（Room）
- 每日提醒設定與測試通知

## 功能總覽

- **待辦管理**
  - 新增待辦事項
  - 以關鍵字搜尋待辦事項（不分大小寫）
  - 勾選切換 Done / Undone
  - 刪除指定待辦事項
- **提醒功能**
  - 設定每日提醒時間
  - 可立即發送測試通知
  - App 啟動時建立通知頻道並請求通知權限（Android 13+）

## 技術棧

- Kotlin
- Android Jetpack Compose (Material 3)
- Room Database
- SharedPreferences（儲存提醒時間）
- AlarmManager + BroadcastReceiver（每日提醒）
- JUnit4（單元測試）

## 專案結構

```text
app/src/main/java/tw/edu/example/aitodostarter
├── data        # Room、Repository、Model
├── reminder    # 通知排程與接收器
├── ui          # Controller 與 Compose UI
└── MainActivity.kt
```

## 環境需求

- Android Studio（建議使用最新穩定版）
- JDK 17+
- Android SDK（`compileSdk = 36`, `minSdk = 26`）

## 如何執行

1. 開啟 Android Studio，選擇此專案資料夾。
2. 等待 Gradle Sync 完成。
3. 使用模擬器或實機執行 `app` 模組。

## 常用指令（CLI）

在專案根目錄執行：

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew lint
```

## 主要測試

- `TodoControllerTest`
- `TodoRequirementTest`

測試涵蓋新增、切換完成狀態、刪除與刪除目標正確性等核心行為。
