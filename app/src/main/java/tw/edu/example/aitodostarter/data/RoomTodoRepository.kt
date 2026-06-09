package tw.edu.example.aitodostarter.data

// 抽象層。實作了 TodoRepository 介面，負責呼叫 DAO。
class RoomTodoRepository(private val todoDao: TodoDao) : TodoRepository {
    override fun getTodos(): List<TodoItem> = todoDao.getAllTodos()

    override fun addTodo(title: String): TodoItem {
        val newTodo = TodoItem(title = title)
        val insertedId = todoDao.insertTodo(newTodo).toInt()
        return newTodo.copy(id = insertedId)
    }

    override fun toggleTodo(id: Int) {
        val todo = todoDao.getTodoById(id)
        if (todo != null) {
            todoDao.updateTodo(todo.copy(isDone = !todo.isDone))
        }
    }

    override fun deleteTodo(id: Int) {
        todoDao.deleteTodoById(id)
    }
}