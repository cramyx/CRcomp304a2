package com.example.coleramsey_comp304lab2_ex1

import android.app.Activity
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.window.layout.WindowMetricsCalculator

enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED }

class WindowSizeClassUtil(private val activity: Activity) {
    @Composable
    fun calculateWindowSizeClass(): WindowSizeClass {
        val configuration = LocalConfiguration.current
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(activity)

        val density = LocalDensity.current
        val widthDp = with(density) { metrics.bounds.width().toDp() }

        return when {
            widthDp < 600.dp -> WindowSizeClass.COMPACT
            widthDp < 840.dp -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }
    }
}