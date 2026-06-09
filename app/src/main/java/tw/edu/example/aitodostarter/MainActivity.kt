package tw.edu.example.aitodostarter

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.edu.example.aitodostarter.data.AppDatabase
import tw.edu.example.aitodostarter.data.RoomTodoRepository
import tw.edu.example.aitodostarter.data.SharedPreferencesReminderSettingsRepository
import tw.edu.example.aitodostarter.reminder.ReminderScheduler
import tw.edu.example.aitodostarter.ui.ReminderSettingsController
import tw.edu.example.aitodostarter.ui.TodoApp
import tw.edu.example.aitodostarter.ui.TodoController

class MainActivity : ComponentActivity() {
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 初始化 Room 資料庫與 Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val todoRepository = RoomTodoRepository(database.todoDao())

        // 2. 初始化 Reminder 相關服務
        val reminderRepository = SharedPreferencesReminderSettingsRepository(applicationContext)
        val reminderScheduler = ReminderScheduler(applicationContext)
        val reminderController = ReminderSettingsController(reminderRepository, reminderScheduler)

        // 3. 透過 ViewModelProvider 初始化 TodoController (防旋轉資料遺失)
        val todoController by viewModels<TodoController> {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoController(todoRepository) as T
                }
            }
        }

        // 4. 建立通知頻道與請求權限 (把遺失的邏輯加回來)
        reminderScheduler.createNotificationChannel()
        requestNotificationPermissionIfNeeded()

        setContent {
            TodoApp(
                todoController = todoController,
                reminderSettingsController = reminderController
            )
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}