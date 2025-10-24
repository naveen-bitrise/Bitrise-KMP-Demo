import XCTest

final class TodoKMPUITests: XCTestCase {
    
    override func setUpWithError() throws {
        continueAfterFailure = false
    }
    
    override func tearDownWithError() throws {
        // Clean up after each test
    }
    
    private func takeScreenshot(name: String) {
        let screenshot = XCUIScreen.main.screenshot()
        let attachment = XCTAttachment(screenshot: screenshot)
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
        print("Screenshot taken: \(name)")
    }
    
    func testEmptyStateIsDisplayed() throws {
        let app = XCUIApplication()
        app.launch()
        
        // Wait for the app to load
        let emptyStateText = app.staticTexts["No todos yet"]
        XCTAssertTrue(emptyStateText.waitForExistence(timeout: 5))
        
        let instructionText = app.staticTexts["Add one to get started!"]
        XCTAssertTrue(instructionText.exists)
        
        takeScreenshot(name: "01_empty_state")
    }
    
    func testAddTodoDialog() throws {
        let app = XCUIApplication()
        app.launch()
        
        // Tap the add button
        let addButton = app.buttons.matching(identifier: "addTodoButton").firstMatch
        XCTAssertTrue(addButton.waitForExistence(timeout: 5))
        addButton.tap()
        
        // Check that the add todo dialog is displayed
        let titleInput = app.textFields["titleInput"]
        XCTAssertTrue(titleInput.waitForExistence(timeout: 2))
        
        let descriptionInput = app.textViews["descriptionInput"]
        XCTAssertTrue(descriptionInput.exists)
        
        let confirmButton = app.buttons["confirmAddButton"]
        XCTAssertTrue(confirmButton.exists)
        
        let cancelButton = app.buttons["cancelAddButton"]
        XCTAssertTrue(cancelButton.exists)
        
        takeScreenshot(name: "02_add_todo_dialog")
    }
    
    func testAddTodoSuccessfully() throws {
        let app = XCUIApplication()
        app.launch()
        
        // Tap the add button
        let addButton = app.buttons.matching(identifier: "addTodoButton").firstMatch
        addButton.tap()
        
        // Fill in the todo details
        let titleInput = app.textFields["titleInput"]
        XCTAssertTrue(titleInput.waitForExistence(timeout: 2))
        titleInput.tap()
        titleInput.typeText("Test Todo")
        
        let descriptionInput = app.textViews["descriptionInput"]
        descriptionInput.tap()
        descriptionInput.typeText("Test Description")
        
        takeScreenshot(name: "03_add_todo_filled")
        
        // Tap the confirm button
        let confirmButton = app.buttons["confirmAddButton"]
        confirmButton.tap()
        
        // Wait for the dialog to dismiss and check that the todo was added
        XCTAssertTrue(app.staticTexts["Test Todo"].waitForExistence(timeout: 3))
        XCTAssertTrue(app.staticTexts["Test Description"].exists)
        
        takeScreenshot(name: "04_todo_added")
    }
    
    func testToggleTodoCompletion() throws {
        let app = XCUIApplication()
        app.launch()
        
        // First add a todo
        let addButton = app.buttons.matching(identifier: "addTodoButton").firstMatch
        addButton.tap()
        
        let titleInput = app.textFields["titleInput"]
        titleInput.tap()
        titleInput.typeText("Toggle Test")
        
        let confirmButton = app.buttons["confirmAddButton"]
        confirmButton.tap()
        
        // Wait for the todo to appear
        XCTAssertTrue(app.staticTexts["Toggle Test"].waitForExistence(timeout: 3))
        
        takeScreenshot(name: "05_todo_before_toggle")
        
        // Find and tap the checkbox to toggle completion
        let checkbox = app.buttons.matching(NSPredicate(format: "identifier CONTAINS[c] 'todoCheckbox_'")).firstMatch
        XCTAssertTrue(checkbox.waitForExistence(timeout: 2))
        checkbox.tap()
        
        // Wait a moment for the UI to update
        Thread.sleep(forTimeInterval: 0.5)
        
        takeScreenshot(name: "06_todo_after_toggle")
    }
    
    func testDeleteTodo() throws {
        let app = XCUIApplication()
        app.launch()
        
        // First add a todo
        let addButton = app.buttons.matching(identifier: "addTodoButton").firstMatch
        addButton.tap()
        
        let titleInput = app.textFields["titleInput"]
        titleInput.tap()
        titleInput.typeText("Delete Test")
        
        let confirmButton = app.buttons["confirmAddButton"]
        confirmButton.tap()
        
        // Wait for the todo to appear
        XCTAssertTrue(app.staticTexts["Delete Test"].waitForExistence(timeout: 3))
        
        takeScreenshot(name: "07_todo_before_delete")
        
        // Find and tap the delete button
        let deleteButton = app.buttons.matching(NSPredicate(format: "identifier CONTAINS[c] 'deleteButton_'")).firstMatch
        XCTAssertTrue(deleteButton.waitForExistence(timeout: 2))
        deleteButton.tap()
        
        // Wait for the todo to be deleted and empty state to appear
        XCTAssertTrue(app.staticTexts["No todos yet"].waitForExistence(timeout: 3))
        XCTAssertFalse(app.staticTexts["Delete Test"].exists)
        
        takeScreenshot(name: "08_todo_after_delete")
    }
}