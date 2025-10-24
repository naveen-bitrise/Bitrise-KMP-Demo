package com.example.todokmp.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import TodoItem
import TodoManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp() {
    val todoManager = remember { TodoManager() }
    val todos by todoManager.getAllTodos().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("todoApp")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Todo List",
                style = MaterialTheme.typography.headlineMedium
            )
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("addTodoButton")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
        
        if (todos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No todos yet. Add one to get started!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("emptyState")
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.testTag("todoList"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todos) { todo ->
                    TodoItemView(
                        todo = todo,
                        onToggle = { 
                            scope.launch { 
                                todoManager.toggleTodoCompletion(todo.id)
                            }
                        },
                        onDelete = { 
                            scope.launch { 
                                todoManager.deleteTodo(todo.id)
                            }
                        }
                    )
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddTodoDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, description ->
                scope.launch {
                    todoManager.addTodo(title, description)
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TodoItemView(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("todoItem_${todo.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() },
                modifier = Modifier.testTag("todoCheckbox_${todo.id}")
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    modifier = Modifier.testTag("todoTitle_${todo.id}")
                )
                
                if (todo.description.isNotBlank()) {
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                    )
                }
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("deleteButton_${todo.id}")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Todo")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Todo") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("titleInput"),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("descriptionInput"),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(title, description) },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirmAddButton")
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancelAddButton")
            ) {
                Text("Cancel")
            }
        }
    )
}