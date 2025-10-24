import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TodoManagerTest {
    
    @Test
    fun testAddTodoWithValidTitle() = runTest {
        val todoManager = TodoManager()
        val result = todoManager.addTodo("Test Todo", "Test Description")
        
        assertTrue(result.isSuccess)
        val todo = result.getOrNull()!!
        assertEquals("Test Todo", todo.title)
        assertEquals("Test Description", todo.description)
        assertFalse(todo.isCompleted)
    }
    
    @Test
    fun testAddTodoWithBlankTitleFails() = runTest {
        val todoManager = TodoManager()
        val result = todoManager.addTodo("", "Description")
        
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
    
    @Test
    fun testToggleTodoCompletion() = runTest {
        val todoManager = TodoManager()
        val addResult = todoManager.addTodo("Test Todo")
        assertTrue(addResult.isSuccess)
        
        val todo = addResult.getOrNull()!!
        assertFalse(todo.isCompleted)
        
        val toggleResult = todoManager.toggleTodoCompletion(todo.id)
        assertTrue(toggleResult.isSuccess)
        
        val updatedTodo = toggleResult.getOrNull()!!
        assertTrue(updatedTodo.isCompleted)
        assertTrue(updatedTodo.completedAt != null)
    }
    
    @Test
    fun testUpdateTodoWithValidData() = runTest {
        val todoManager = TodoManager()
        val addResult = todoManager.addTodo("Original Title", "Original Description")
        val todo = addResult.getOrNull()!!
        
        val updateResult = todoManager.updateTodo(todo.id, "Updated Title", "Updated Description")
        assertTrue(updateResult.isSuccess)
        
        val updatedTodo = updateResult.getOrNull()!!
        assertEquals("Updated Title", updatedTodo.title)
        assertEquals("Updated Description", updatedTodo.description)
        assertEquals(todo.id, updatedTodo.id)
        assertEquals(todo.createdAt, updatedTodo.createdAt)
    }
    
    @Test
    fun testDeleteTodo() = runTest {
        val todoManager = TodoManager()
        val addResult = todoManager.addTodo("Test Todo")
        val todo = addResult.getOrNull()!!
        
        val deleteResult = todoManager.deleteTodo(todo.id)
        assertTrue(deleteResult.isSuccess)
        
        val todos = todoManager.getAllTodos().first()
        assertTrue(todos.isEmpty())
    }
    
    @Test
    fun testGetAllTodosWithMultipleTodos() = runTest {
        val todoManager = TodoManager()
        
        // Add multiple todos with small delays to ensure unique timestamps
        val firstResult = todoManager.addTodo("First Todo", "First Description")
        assertTrue(firstResult.isSuccess)
        
        //adding delays
        kotlinx.coroutines.delay(12) // Small delay to ensure different timestamps
        
        val secondResult = todoManager.addTodo("Second Todo", "Second Description")
        assertTrue(secondResult.isSuccess)
        
        kotlinx.coroutines.delay(12) // Small delay to ensure different timestamps
        
        val thirdResult = todoManager.addTodo("Third Todo", "Third Description")
        assertTrue(thirdResult.isSuccess)
        
        val todos = todoManager.getAllTodos().first()
        assertEquals(3, todos.size)
        
        // Verify all three todos are present (order may vary due to timestamp sorting)
        val todoTitles = todos.map { it.title }
        assertTrue(todoTitles.contains("First Todo"), "Should contain First Todo")
        assertTrue(todoTitles.contains("Second Todo"), "Should contain Second Todo")
        assertTrue(todoTitles.contains("Third Todo"), "Should contain Third Todo")
    }
}