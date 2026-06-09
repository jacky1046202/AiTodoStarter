package tw.edu.example.aitodostarter.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import tw.edu.example.aitodostarter.data.RoomTodoRepository
import tw.edu.example.aitodostarter.data.ReminderSettings
import tw.edu.example.aitodostarter.data.ReminderSettingsRepository
import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.reminder.ReminderScheduler

enum class AppPage { Todos, Settings }

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(
    todoController: TodoController,
    reminderSettingsController: ReminderSettingsController,
) {
    MaterialTheme {
        var page by remember { mutableStateOf(AppPage.Todos) }
        var todoState by remember { mutableStateOf(todoController.state) }
        var reminderSettingsState by remember { mutableStateOf(reminderSettingsController.state) }

        Scaffold(
            topBar = {
                when (page) {
                    AppPage.Todos -> TodoTopAppBar(onSettingsClick = { page = AppPage.Settings })
                    AppPage.Settings -> SettingsTopAppBar(onBackClick = { page = AppPage.Todos })
                }
            },
            // 【要求四】新增 FloatingActionButton 用來執行 Add
            floatingActionButton = {
                if (page == AppPage.Todos) {
                    FloatingActionButton(
                        onClick = {
                            todoController.addTodo()
                            todoState = todoController.state
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Todo")
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when (page) {
                        AppPage.Todos -> TodoScreen(
                            state = todoState,
                            activeCount = todoController.activeCount(),
                            onInputChange = {
                                todoController.updateInput(it)
                                todoState = todoController.state
                            },
                            onSearchClick = { // 【要求四】改為 Search 功能
                                todoController.search()
                                todoState = todoController.state
                            },
                            onToggleClick = {
                                todoController.toggleTodo(it)
                                todoState = todoController.state
                            },
                            onDeleteClick = { // 【要求二】傳遞 Delete 事件
                                todoController.deleteTodo(it)
                                todoState = todoController.state
                            }
                        )

                        AppPage.Settings -> ReminderSettingsScreen(
                            state = reminderSettingsState,
                            onTimeSelected = { hour, minute ->
                                reminderSettingsController.updateReminderTime(hour, minute)
                                reminderSettingsState = reminderSettingsController.state
                            },
                            onTestClick = { reminderSettingsController.testReminder() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTopAppBar(onSettingsClick: () -> Unit) {
    TopAppBar(
        title = { Text("Todo List") },
        actions = {
            TodoAppMenu(onSettingsClick = onSettingsClick)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Reminder Settings") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to todo list",
                )
            }
        },
    )
}

@Composable
fun TodoScreen(
    state: TodoUiState,
    activeCount: Int,
    onInputChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onToggleClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "$activeCount active item(s)", style = MaterialTheme.typography.bodyMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.inputText,
                onValueChange = onInputChange,
                label = { Text("Search or Add") }, // 提示可以輸入搜尋或新增
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) }
            )
            Button(onClick = onSearchClick) {
                Text("Search")
            }
        }

        HorizontalDivider()

        if (state.todos.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("No todos found")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.todos, key = { it.id }) { todo ->
                    TodoRow(
                        todo = todo,
                        onToggleClick = onToggleClick,
                        onDeleteClick = onDeleteClick // 傳遞給 TodoRow
                    )
                }
            }
        }
    }
}

@Composable
fun TodoAppMenu(onSettingsClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Open menu",
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = {
                expanded = false
                onSettingsClick()
            },
        )
    }
}

@Composable
fun ReminderSettingsScreen(
    state: ReminderSettingsUiState,
    onTimeSelected: (Int, Int) -> Unit,
    onTestClick: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally // 置中設計
    ) {
        // 使用 Card 提升視覺層次感
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Daily Reminder",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = state.settings.formattedTime(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute -> onTimeSelected(hour, minute) },
                            state.settings.hour,
                            state.settings.minute,
                            true,
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Time")
                }
            }
        }

        OutlinedButton(
            onClick = onTestClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Notification Now")
        }
    }
}

@Composable
fun TodoRow(
    todo: TodoItem,
    onToggleClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    // 使用 Card 讓列表項目更好看
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = todo.title,
                textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
                color = if (todo.isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurface
            )
            TodoItemMenu(
                isDone = todo.isDone,
                onDoneClick = { onToggleClick(todo.id) },
                onUndoneClick = { onToggleClick(todo.id) },
                onDeleteClick = { onDeleteClick(todo.id) }
            )
        }
    }
}

@Composable
fun TodoItemMenu(
    isDone: Boolean,
    onDoneClick: () -> Unit,
    onUndoneClick: () -> Unit,
    onDeleteClick: () -> Unit // 【要求二】
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Filled.MoreVert, contentDescription = "Open todo menu")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        if (isDone) {
            DropdownMenuItem(text = { Text("Undone") }, onClick = { expanded = false; onUndoneClick() })
        } else {
            DropdownMenuItem(text = { Text("Done") }, onClick = { expanded = false; onDoneClick() })
        }
        // 新增 Delete 選項，字體改紅色以作區別
        DropdownMenuItem(
            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
            onClick = { expanded = false; onDeleteClick() }
        )
    }
}