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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// 定義一組現代感的顏色
private val LightColors = lightColorScheme(
    primary = Color(0xFF006A60),      // 深青綠
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF68D3C3), // 淺青綠 (FAB 用)
    onPrimaryContainer = Color(0xFF00201C),
    surfaceVariant = Color(0xFFDAE5E1),   // 灰綠色 (Card 背景)
    background = Color(0xFFF4FBF9),       // 極淺綠背景
)
@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(
    todoController: TodoController,
    reminderSettingsController: ReminderSettingsController,
) {
    MaterialTheme(colorScheme = LightColors) {
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
                    ExtendedFloatingActionButton(
                        text = { Text("New Task") },
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        onClick = {
                            todoController.addTodo()
                            todoState = todoController.state
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
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
    // 改用 CenterAlignedTopAppBar 或 MediumTopAppBar 會更有質感
    CenterAlignedTopAppBar(
        title = {
            Text(
                "My Tasks",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        actions = {
            TodoAppMenu(onSettingsClick = onSettingsClick)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
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
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "All caught up!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.todos, key = { it.id }) { todo ->
                    TodoRow(todo = todo, onToggleClick = onToggleClick, onDeleteClick = onDeleteClick)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // 讓 Card 之間有呼吸感
        shape = RoundedCornerShape(16.dp), // 更圓潤的角，更有現代感
        colors = CardDefaults.cardColors(
            containerColor = if (todo.isDone)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 使用系統提供的 Checkbox 增加互動感
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = { onToggleClick(todo.id) },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = todo.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (todo.isDone) FontWeight.Normal else FontWeight.Medium,
                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else null
                ),
                color = if (todo.isDone) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
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