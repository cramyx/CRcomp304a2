package com.example.coleramsey_comp304lab2_ex1

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class TaskRepository {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        // hardcoded tasks to appear on main home screen
        _tasks.value = listOf(
            Task(1, "Complete Assignment", "Finish and submit the assignment by tonight.", false),
            Task(2, "Grocery Shopping", "Buy groceries for the week.", true)
        )
    }

    fun addTask(task: Task) {
        val updatedTasks = _tasks.value.toMutableList()
        updatedTasks.add(task)
        _tasks.value = updatedTasks
    }

    fun updateTask(task: Task) {
        val updatedTasks = _tasks.value.map {
            if (it.id == task.id) task else it
        }
        _tasks.value = updatedTasks
    }

    fun deleteTask(taskId: Int) {
        val updatedTasks = _tasks.value.filterNot { it.id == taskId }
        _tasks.value = updatedTasks
    }
}