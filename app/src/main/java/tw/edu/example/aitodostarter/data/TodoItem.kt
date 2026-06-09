package tw.edu.example.aitodostarter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 資料表定義
@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isDone: Boolean = false,
)