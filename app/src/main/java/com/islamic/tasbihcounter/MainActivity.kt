package com.islamic.tasbihcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.islamic.tasbihcounter.ui.Str
import com.islamic.tasbihcounter.ui.TasbihViewModel
import com.islamic.tasbihcounter.ui.screens.AsmaulHusnaScreen
import com.islamic.tasbihcounter.ui.screens.CounterScreen
import com.islamic.tasbihcounter.ui.screens.DuaScreen
import com.islamic.tasbihcounter.ui.screens.HistoryScreen
import com.islamic.tasbihcounter.ui.screens.SettingsScreen
import com.islamic.tasbihcounter.ui.theme.TasbihCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: TasbihViewModel = viewModel()
            val state by vm.state.collectAsState()

            TasbihCounterTheme(
                themeStyle = state.themeStyle,
                themeMode = state.themeMode,
                dynamicColor = state.dynamicColor
            ) {
                // Honour the keep-screen-on preference.
                val view = LocalView.current
                LaunchedEffect(state.keepScreenOn) {
                    view.keepScreenOn = state.keepScreenOn
                }
                TasbihAppRoot(vm)
            }
        }
    }
}

private enum class Dest(val route: String, val label: String, val icon: ImageVector) {
    COUNTER("counter", Str.navCounter, Icons.Filled.Fingerprint),
    NAMES("names", Str.navNames, Icons.Filled.Star),
    DUAS("duas", Str.navDuas, Icons.AutoMirrored.Filled.MenuBook),
    HISTORY("history", Str.navHistory, Icons.Filled.History),
    SETTINGS("settings", Str.navSettings, Icons.Filled.Settings)
}

@Composable
private fun TasbihAppRoot(vm: TasbihViewModel) {
    val navController = rememberNavController()
    val state by vm.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val backStack by navController.currentBackStackEntryAsState()
                val current = backStack?.destination
                Dest.entries.forEach { dest ->
                    NavigationBarItem(
                        selected = current?.hierarchy?.any { it.route == dest.route } == true,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.COUNTER.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Dest.COUNTER.route) { CounterScreen(state, vm) }
            composable(Dest.NAMES.route) { AsmaulHusnaScreen(state, vm) }
            composable(Dest.DUAS.route) { DuaScreen() }
            composable(Dest.HISTORY.route) { HistoryScreen(state, vm) }
            composable(Dest.SETTINGS.route) { SettingsScreen(state, vm) }
        }
    }
}
