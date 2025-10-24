import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<TodoItem>>
    suspend fun addTodo(item: TodoItem)
    suspend fun updateTodo(item: TodoItem)
    suspend fun deleteTodo(id: String)
    suspend fun getTodoById(id: String): TodoItem?
}

class InMemoryTodoRepository : TodoRepository {
    private val todos = mutableMapOf<String, TodoItem>()
    private val _todoFlow = kotlinx.coroutines.flow.MutableStateFlow(emptyList<TodoItem>())
    
    override fun getAllTodos(): Flow<List<TodoItem>> = _todoFlow
    
    override suspend fun addTodo(item: TodoItem) {
        todos[item.id] = item
        updateFlow()
    }
    
    override suspend fun updateTodo(item: TodoItem) {
        todos[item.id] = item
        updateFlow()
    }
    
    override suspend fun deleteTodo(id: String) {
        todos.remove(id)
        updateFlow()
    }
    
    override suspend fun getTodoById(id: String): TodoItem? = todos[id]
    
    private fun updateFlow() {
        _todoFlow.value = todos.values.sortedByDescending { it.createdAt }
    }
}