package tw.edu.example.aitodostarter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// 資料庫操作介面。等同於在後端寫的 SQL 查詢函數。
@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items")
    fun getAllTodos(): List<TodoItem>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getTodoById(id: Int): TodoItem?

    @Insert
    fun insertTodo(todo: TodoItem): Long

    @Update
    fun updateTodo(todo: TodoItem)

    @Query("DELETE FROM todo_items WHERE id = :id")
    fun deleteTodoById(id: Int)
}