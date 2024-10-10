package com.example.coleramsey_comp304lab2_ex1
// COLE RAMSEY 301333287
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp


import com.example.coleramsey_comp304lab2_ex1.ui.theme.Coleramsey_COMP304Lab2_Ex1Theme


class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Coleramsey_COMP304Lab2_Ex1Theme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    val activity = LocalContext.current as Activity
    val windowSizeClass = rememberWindowSizeClass(activity)

    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = { TaskFAB(navController) },
        content = { innerPadding ->
            NavHost(navController, startDestination = "taskList", modifier = Modifier.padding(innerPadding)) {
                composable("taskList") {
                    TaskList(viewModel, navController, windowSizeClass)
                }
                composable("addTask") {
                    AddTaskScreen(viewModel, navController)
                }
                composable("editTask/{taskId}") { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: return@composable
                    EditTaskScreen(viewModel, navController, taskId)
                }
            }
        }
    )
}

@Composable
fun adjustPaddingBasedOnSize(sizeClass: WindowSizeClass): Dp = when (sizeClass) {
    WindowSizeClass.COMPACT -> 8.dp
    WindowSizeClass.MEDIUM -> 16.dp
    WindowSizeClass.EXPANDED -> 24.dp
}

@Composable
fun rememberWindowSizeClass(activity: Activity): WindowSizeClass {
    val windowSizeClassUtil = WindowSizeClassUtil(activity)
    return windowSizeClassUtil.calculateWindowSizeClass()
}

@Composable
fun AddTaskScreen(viewModel: TaskViewModel, navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(100.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        Button(onClick = {
            if (title.isNotEmpty() && description.isNotEmpty()) {
                viewModel.addTask(Task(id = 0, title = title, description = description, isComplete = false))
                navController.popBackStack()
            }
        }) {
            Text("Add Task")
        }
    }
}

@Composable
fun EditTaskScreen(viewModel: TaskViewModel, navController: NavController, taskId: Int) {
    val tasks = viewModel.tasks.collectAsState().value
    val task = tasks.find { it.id == taskId }

    task?.let {
        var title by remember { mutableStateOf(it.title) }
        var description by remember { mutableStateOf(it.description) }

        Column(modifier = Modifier.padding(100.dp)) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") }
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )
            Button(onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    viewModel.updateTask(it.copy(title = title, description = description))
                    navController.popBackStack()
                }
            }) {
                Text("Update Task")
            }
        }
    } ?: Text("Task not found!")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Task Manager",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun TaskFAB(navController: NavController) {
    FloatingActionButton(
        onClick = { navController.navigate("addTask") },
        modifier = Modifier.size(80.dp),
        content = {
            Icon(Icons.Filled.Add, contentDescription = "Add Task", modifier = Modifier.size(36.dp))
        }
    )
}

@Composable
fun TaskList(viewModel: TaskViewModel, navController: NavController, windowSizeClass: WindowSizeClass) {
    val tasks = viewModel.tasks.collectAsState().value
    val padding = adjustPaddingBasedOnSize(windowSizeClass)
    LazyColumn(modifier = Modifier.padding(padding)) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onClick = { navController.navigate("editTask/${task.id}") },
                onStatusChange = { isComplete ->
                    viewModel.updateTask(task.copy(isComplete = isComplete))
                },
                onDelete = {
                    viewModel.deleteTask(task.id)
                }
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onClick: () -> Unit, onStatusChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable { onClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Checkbox(
                checked = task.isComplete,
                onCheckedChange = { isChecked -> onStatusChange(isChecked) },
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = task.description,
                    fontSize = 16.sp
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task"
                )
            }
        }
    }
}

@Composable
fun PreviewViewModelProvider(): TaskViewModel {
    val repository = TaskRepository().apply {
        addTask(Task(0, "Preview Task 1", "Do something cool", false))
        addTask(Task(1, "Preview Task 2", "Do something even cooler", true))
    }
    return TaskViewModel(repository)
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    Coleramsey_COMP304Lab2_Ex1Theme {
        MainScreen(PreviewViewModelProvider())
    }
}