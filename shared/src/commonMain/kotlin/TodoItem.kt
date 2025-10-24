import kotlinx.datetime.Instant

data class TodoItem(
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Instant,
    val completedAt: Instant? = null
)