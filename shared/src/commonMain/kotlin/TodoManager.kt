import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class TodoManager(private val repository: TodoRepository = InMemoryTodoRepository()) {
    
    fun getAllTodos(): Flow<List<TodoItem>> = repository.getAllTodos()
    
    suspend fun addTodo(title: String, description: String = ""): Result<TodoItem> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Title cannot be blank"))
        }
        
        val todoItem = TodoItem(
            id = generateId(),
            title = title.trim(),
            description = description.trim(),
            createdAt = Clock.System.now()
        )
        
        return try {
            repository.addTodo(todoItem)
            Result.success(todoItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleTodoCompletion(id: String): Result<TodoItem> {
        val todo = repository.getTodoById(id) 
            ?: return Result.failure(IllegalArgumentException("Todo not found"))
        
        val updatedTodo = todo.copy(
            isCompleted = !todo.isCompleted,
            completedAt = if (!todo.isCompleted) Clock.System.now() else null
        )
        
        return try {
            repository.updateTodo(updatedTodo)
            Result.success(updatedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTodo(id: String, title: String, description: String = ""): Result<TodoItem> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Title cannot be blank"))
        }
        
        val todo = repository.getTodoById(id)
            ?: return Result.failure(IllegalArgumentException("Todo not found"))
        
        val updatedTodo = todo.copy(
            title = title.trim(),
            description = description.trim()
        )
        
        return try {
            repository.updateTodo(updatedTodo)
            Result.success(updatedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTodo(id: String): Result<Boolean> {
        return try {
            repository.deleteTodo(id)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCompletedTodosCount(): Int {
        return repository.getAllTodos().first().count { it.isCompleted }
    }
    
    private fun generateId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }
}