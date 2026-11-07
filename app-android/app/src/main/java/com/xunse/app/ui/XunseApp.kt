package com.xunse.app.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xunse.app.R
import com.xunse.app.data.HistoryRepository
import com.xunse.app.data.SettingsRepository

private enum class Tab(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int,
    val description: String,
) {
    CAMERA("camera", "相机", R.drawable.ic_tab_camera, "相机预览与色彩滤镜"),
    COMPARE("compare", "识色与比对", R.drawable.ic_tab_compare, "单点识色与双区域颜色比对"),
    FIND("find", "寻色", R.drawable.ic_tab_find, "按目标颜色查找与高亮"),
    CHART("chart", "图表", R.drawable.ic_tab_chart, "图表导入与颜色区域解析"),
    SETTINGS("settings", "设置", R.drawable.ic_tab_settings, ""),
}

@Composable
fun XunseApp(settings: SettingsRepository, history: HistoryRepository) {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val appScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    val selected = (entry?.destination?.route ?: Tab.CAMERA.route) == tab.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(tab.title, textAlign = TextAlign.Center) },
                    )
                }
            }
        },
    ) { insets ->
        NavHost(
            navController = navController,
            startDestination = Tab.CAMERA.route,
            modifier = Modifier.padding(insets).fillMaxSize(),
        ) {
            Tab.entries.forEach { tab ->
                composable(tab.route) {
                    if (tab == Tab.SETTINGS) {
                        SettingsScreen(settings, history, appScope)
                    } else {
                        PlaceholderScreen(tab.title, tab.description)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(description, style = MaterialTheme.typography.bodyLarge)
        Text("页面占位 · 第 2 周", style = MaterialTheme.typography.bodyMedium)
    }
}
