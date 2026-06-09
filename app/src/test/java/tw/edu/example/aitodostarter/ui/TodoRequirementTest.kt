package tw.edu.example.aitodostarter.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import tw.edu.example.aitodostarter.data.InMemoryTodoRepository

class TodoRequirementTest {

    /**
     * 要求 3 - 第一點：
     * 請撰寫一個自動測試，可以新增一筆代辦事項，然後將之標記為 done，確認成功之後標記為 undone，確認也有成功
     */
    @Test
    fun add_then_markDone_then_markUndone_worksCorrectly() {
        val repository = InMemoryTodoRepository()
        val controller = TodoController(repository)
        
        // 1. 新增一筆代辦事項
        controller.updateInput("Test Task 1")
        controller.addTodo()
        val todo = controller.state.todos.first { it.title == "Test Task 1" }
        assertFalse("Initial state should be undone", todo.isDone)

        // 2. 標記為 done
        controller.toggleTodo(todo.id)
        val doneTodo = controller.state.todos.first { it.id == todo.id }
        assertTrue("Task should be marked as done", doneTodo.isDone)

        // 3. 標記為 undone
        controller.toggleTodo(todo.id)
        val undoneTodo = controller.state.todos.first { it.id == todo.id }
        assertFalse("Task should be marked as undone again", undoneTodo.isDone)
    }

    /**
     * 要求 3 - 第二點：
     * 請撰寫一個自動測試，可以新增一筆代辦事項，然後將之標記為 done，之後可以把它刪除，並確認有刪除成功
     */
    @Test
    fun add_then_markDone_then_delete_worksCorrectly() {
        val repository = InMemoryTodoRepository()
        val controller = TodoController(repository)

        // 1. 新增一筆代辦事項
        controller.updateInput("Test Task 2")
        controller.addTodo()
        val todo = controller.state.todos.first { it.title == "Test Task 2" }

        // 2. 標記為 done
        controller.toggleTodo(todo.id)
        assertTrue(controller.state.todos.first { it.id == todo.id }.isDone)

        // 3. 刪除
        controller.deleteTodo(todo.id)
        assertFalse("Task should be deleted from the list", controller.state.todos.any { it.id == todo.id })
    }

    /**
     * 要求 3 - 第三點：
     * 能夠測試如果有兩筆內容的代辦事項，刪除時只會刪除選到的那筆
     */
    @Test
    fun delete_only_removes_selected_item() {
        val repository = InMemoryTodoRepository()
        val controller = TodoController(repository)

        // 1. 新增兩筆代辦事項
        controller.updateInput("Task A")
        controller.addTodo()
        controller.updateInput("Task B")
        controller.addTodo()
        
        val idA = controller.state.todos.first { it.title == "Task A" }.id
        val idB = controller.state.todos.first { it.title == "Task B" }.id
        val initialSize = controller.state.todos.size
        assertTrue(initialSize >= 2)

        // 2. 刪除 Task A
        controller.deleteTodo(idA)

        // 3. 確認只有 Task A 被刪除，Task B 還在
        assertEquals(initialSize - 1, controller.state.todos.size)
        assertFalse("Task A should be deleted", controller.state.todos.any { it.id == idA })
        assertTrue("Task B should still exist", controller.state.todos.any { it.id == idB })
    }
}
