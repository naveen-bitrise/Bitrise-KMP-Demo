import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = TodoViewModel()
    @State private var showingAddTodo = false
    
    var body: some View {
        NavigationView {
            VStack {
                if viewModel.todos.isEmpty {
                    VStack {
                        Text("No todos yet")
                            .font(.title2)
                            .foregroundColor(.secondary)
                        Text("Add one to get started!")
                            .font(.body)
                            .foregroundColor(.secondary)
                    }
                    .accessibilityIdentifier("emptyState")
                } else {
                    List {
                        ForEach(viewModel.todos, id: \.id) { todo in
                            TodoRowView(
                                todo: todo,
                                onToggle: { viewModel.toggleTodo(todo) },
                                onDelete: { viewModel.deleteTodo(todo) }
                            )
                            .accessibilityIdentifier("todoItem_\(todo.id)")
                        }
                    }
                    .accessibilityIdentifier("todoList")
                }
            }
            .navigationTitle("Todo List")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showingAddTodo = true }) {
                        Image(systemName: "plus")
                    }
                    .accessibilityIdentifier("addTodoButton")
                }
            }
        }
        .sheet(isPresented: $showingAddTodo) {
            AddTodoView { title, description in
                viewModel.addTodo(title: title, description: description)
                showingAddTodo = false
            }
        }
        .onAppear {
            viewModel.loadTodos()
        }
        .accessibilityIdentifier("todoApp")
    }
}

struct TodoRowView: View {
    let todo: TodoItem
    let onToggle: () -> Void
    let onDelete: () -> Void
    
    var body: some View {
        HStack {
            Button(action: onToggle) {
                Image(systemName: todo.isCompleted ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(todo.isCompleted ? .green : .gray)
            }
            .buttonStyle(PlainButtonStyle())
            .accessibilityIdentifier("todoCheckbox_\(todo.id)")
            
            VStack(alignment: .leading, spacing: 4) {
                Text(todo.title)
                    .font(.headline)
                    .strikethrough(todo.isCompleted)
                    .foregroundColor(todo.isCompleted ? .secondary : .primary)
                    .accessibilityIdentifier("todoTitle_\(todo.id)")
                
                if !todo.description_.isEmpty {
                    Text(todo.description_)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .strikethrough(todo.isCompleted)
                }
            }
            
            Spacer()
            
            Button(action: onDelete) {
                Image(systemName: "trash")
                    .foregroundColor(.red)
            }
            .buttonStyle(PlainButtonStyle())
            .accessibilityIdentifier("deleteButton_\(todo.id)")
        }
        .padding(.vertical, 4)
    }
}

struct AddTodoView: View {
    @State private var title = ""
    @State private var description = ""
    @Environment(\.presentationMode) var presentationMode
    
    let onAdd: (String, String) -> Void
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Todo Details")) {
                    TextField("Title", text: $title)
                        .accessibilityIdentifier("titleInput")
                    
                    TextField("Description (Optional)", text: $description, axis: .vertical)
                        .lineLimit(3...6)
                        .accessibilityIdentifier("descriptionInput")
                }
            }
            .navigationTitle("Add Todo")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .accessibilityIdentifier("cancelAddButton")
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Add") {
                        onAdd(title, description)
                    }
                    .disabled(title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                    .accessibilityIdentifier("confirmAddButton")
                }
            }
        }
    }
}

class TodoViewModel: ObservableObject {
    @Published var todos: [TodoItem] = []
    
    private let todoManager = TodoManager(repository: InMemoryTodoRepository())
    
    func loadTodos() {
        // For now, we'll implement a simple async approach
        // In a real app, you'd want to properly handle Flow collection
        Task {
            do {
                // Since we can't easily collect Flow in iOS, let's get initial todos
                // This is simplified - in production you'd want proper Flow collection
                await MainActor.run {
                    // Start with empty list - this is where you'd collect from Flow
                    self.todos = []
                }
            }
        }
    }
    
    func addTodo(title: String, description: String) {
        Task {
            do {
                let result = try await todoManager.addTodo(title: title, description: description)
                // Cast the result from Any? to TodoItem
                if let todoItem = result as? TodoItem {
                    await MainActor.run {
                        self.todos.append(todoItem)
                    }
                }
            } catch {
                print("Failed to add todo: \(error)")
            }
        }
    }
    
    func toggleTodo(_ todo: TodoItem) {
        Task {
            do {
                let result = try await todoManager.toggleTodoCompletion(id: todo.id)
                // Cast the result from Any? to TodoItem
                if let updatedTodo = result as? TodoItem {
                    await MainActor.run {
                        if let index = self.todos.firstIndex(where: { $0.id == todo.id }) {
                            self.todos[index] = updatedTodo
                        }
                    }
                }
            } catch {
                print("Failed to toggle todo: \(error)")
            }
        }
    }
    
    func deleteTodo(_ todo: TodoItem) {
        Task {
            do {
                let _ = try await todoManager.deleteTodo(id: todo.id)
                await MainActor.run {
                    self.todos.removeAll { $0.id == todo.id }
                }
            } catch {
                print("Failed to delete todo: \(error)")
            }
        }
    }
}

#Preview {
    ContentView()
}