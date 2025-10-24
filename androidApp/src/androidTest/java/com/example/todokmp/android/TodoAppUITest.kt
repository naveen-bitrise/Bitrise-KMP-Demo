package com.example.todokmp.android

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class TodoAppUITest {

    @get:Rule
    val composeTestRule = createComposeRule()
    
    private fun takeScreenshot(name: String) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val screenshotDir = File("/data/data/com.example.todokmp.android/files/screenshots")
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs()
        }
        val screenshotFile = File(screenshotDir, "$name.png")
        device.takeScreenshot(screenshotFile)
        println("Screenshot saved: ${screenshotFile.absolutePath}")
    }

    @Test
    fun testEmptyStateIsDisplayed() {
        composeTestRule.setContent {
            TodoApp()
        }
        
        composeTestRule.onNodeWithTag("emptyState").assertIsDisplayed()
        composeTestRule.onNodeWithText("No todos yet. Add one to get started!").assertIsDisplayed()
        
        takeScreenshot("01_empty_state")
    }

    @Test
    fun testAddTodoDialog() {
        composeTestRule.setContent {
            TodoApp()
        }
        
        composeTestRule.onNodeWithTag("addTodoButton").performClick()
        
        composeTestRule.onNodeWithTag("titleInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("descriptionInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmAddButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cancelAddButton").assertIsDisplayed()
        
        takeScreenshot("02_add_todo_dialog")
    }

    @Test
    fun testAddTodoSuccessfully() {
        composeTestRule.setContent {
            TodoApp()
        }
        
        composeTestRule.onNodeWithTag("addTodoButton").performClick()
        
        composeTestRule.onNodeWithTag("titleInput").performTextInput("Test Todo")
        composeTestRule.onNodeWithTag("descriptionInput").performTextInput("Test Description")
        takeScreenshot("03_add_todo_filled")
        
        composeTestRule.onNodeWithTag("confirmAddButton").performClick()
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Test Todo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Description").assertIsDisplayed()
        
        takeScreenshot("04_todo_added")
    }

    @Test
    fun testToggleTodoCompletion() {
        composeTestRule.setContent {
            TodoApp()
        }
        
        composeTestRule.onNodeWithTag("addTodoButton").performClick()
        composeTestRule.onNodeWithTag("titleInput").performTextInput("Toggle Test")
        composeTestRule.onNodeWithTag("confirmAddButton").performClick()
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Toggle Test").assertIsDisplayed()
        
        takeScreenshot("05_todo_before_toggle")
        
        val checkboxMatcher = hasTestTag("todoCheckbox_")
        composeTestRule.onNode(checkboxMatcher).performClick()
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNode(checkboxMatcher).assertIsDisplayed()
        
        takeScreenshot("06_todo_after_toggle")
    }

    @Test
    fun testDeleteTodo() {
        composeTestRule.setContent {
            TodoApp()
        }
        
        composeTestRule.onNodeWithTag("addTodoButton").performClick()
        composeTestRule.onNodeWithTag("titleInput").performTextInput("Delete Test")
        composeTestRule.onNodeWithTag("confirmAddButton").performClick()
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Delete Test").assertIsDisplayed()
        
        takeScreenshot("07_todo_before_delete")
        
        val deleteButtonMatcher = hasTestTag("deleteButton_")
        composeTestRule.onNode(deleteButtonMatcher).performClick()
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Delete Test").assertDoesNotExist()
        composeTestRule.onNodeWithTag("emptyState").assertIsDisplayed()
        
        takeScreenshot("08_todo_after_delete")
    }
}