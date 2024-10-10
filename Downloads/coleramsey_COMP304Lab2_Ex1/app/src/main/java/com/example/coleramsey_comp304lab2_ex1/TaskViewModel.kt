package com.example.coleramsey_comp304lab2_ex1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.tasks

    fun addTask(task: Task) = viewModelScope.launch {
        val updatedTasks = tasks.value.toMutableList()
        val newTaskId = if (updatedTasks.isNotEmpty()) updatedTasks.maxOf { it.id } + 1 else 1
        repository.addTask(task.copy(id = newTaskId))
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(taskId: Int) = viewModelScope.launch {
        repository.deleteTask(taskId)
    }
}