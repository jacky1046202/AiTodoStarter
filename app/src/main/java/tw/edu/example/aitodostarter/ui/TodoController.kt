package tw.edu.example.aitodostarter.ui

import androidx.lifecycle.ViewModel
import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.data.TodoRepository

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val inputText: String = "",
)

// 繼承 ViewModel 以保留旋轉螢幕時的狀態
class TodoController(
    private val repository: TodoRepository,
) : ViewModel() {
    var state: TodoUiState = TodoUiState(todos = repository.getTodos())
        private set

    fun updateInput(text: String) {
        state = state.copy(inputText = text)
    }

    // 處理 Search 邏輯 (空白顯示全部，有字則過濾)
    fun search() {
        val keyword = state.inputText
        val allTodos = repository.getTodos()
        val filtered = if (keyword.isBlank()) {
            allTodos
        } else {
            allTodos.filter { it.title.contains(keyword, ignoreCase = true) }
        }
        state = state.copy(todos = filtered)
    }

    fun addTodo() {
        if (state.inputText.isBlank()) return
        repository.addTodo(state.inputText)
        // 新增後清空輸入框，並重新載入全部資料 (或可依需求保持搜尋狀態)
        state = state.copy(
            todos = repository.getTodos(),
            inputText = "",
        )
    }

    fun toggleTodo(id: Int) {
        repository.toggleTodo(id)
        search() // 維持當前的過濾狀態
    }

    fun deleteTodo(id: Int) {
        repository.deleteTodo(id)
        search() // 維持當前的過濾狀態
    }

    fun activeCount(): Int = repository.getTodos().count { !it.isDone }
}