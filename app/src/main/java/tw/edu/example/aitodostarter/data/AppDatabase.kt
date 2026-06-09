package tw.edu.example.aitodostarter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 資料庫實體。這裡是 Room 的進入點，採取 Singleton（單例模式） 設計，確保整個 App 執行期間只會建立一個資料庫連線實例。
@Database(entities = [TodoItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // 獲取資料操作介面（DAO）的抽象工廠方法。
    abstract fun todoDao(): TodoDao

    // 相當於 Java 的 static 靜態區塊。放在這裡的變數和方法屬於類別本身，而不是類別的實例。
    companion object {
        // 保證了 INSTANCE 變數的「可見性（Visibility）」。當任何一個執行緒修改了 INSTANCE 的值，其他執行緒會立刻看到最新狀態，這能避免多個執行緒同時偵測到 INSTANCE == null 而重複建立多個資料庫實例。
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
                            // 同步化
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                )
                    .allowMainThreadQueries() // 配合 Controller 現有同步架構
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}