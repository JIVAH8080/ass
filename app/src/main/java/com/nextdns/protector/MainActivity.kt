package com.nextdns.protector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nextdns.protector.ui.screens.*
import com.nextdns.protector.ui.theme.NextDNSProtectorTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Setup : Screen("setup", "Setup", Icons.Default.Home)
    object Security : Screen("security", "Security", Icons.Default.Security)
    object Privacy : Screen("privacy", "Privacy", Icons.Default.PrivacyTip)
    object ParentalControl : Screen("parental", "Parental", Icons.Default.FamilyRestroom)
    object Denylist : Screen("denylist", "Denylist", Icons.Default.Block)
    object Allowlist : Screen("allowlist", "Allowlist", Icons.Default.CheckCircle)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
    object Logs : Screen("logs", "Logs", Icons.Default.Article)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NextDNSProtectorTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Setup,
        Screen.Security,
        Screen.Privacy,
        Screen.ParentalControl,
        Screen.Denylist,
        Screen.Allowlist,
        Screen.Analytics,
        Screen.Logs,
        Screen.Settings
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val currentScreen = screens.find { 
                        currentDestination?.hierarchy?.any { it.route == it.route } == true 
                    } ?: Screen.Setup
                    Text(currentScreen.title)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Show only main navigation items in bottom bar
                val mainScreens = listOf(
                    Screen.Setup,
                    Screen.Security,
                    Screen.Privacy,
                    Screen.Settings
                )

                mainScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Setup.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Setup.route) { SetupScreen() }
            composable(Screen.Security.route) { SecurityScreen() }
            composable(Screen.Privacy.route) { PrivacyScreen() }
            composable(Screen.ParentalControl.route) { ParentalControlScreen() }
            composable(Screen.Denylist.route) { DenylistScreen() }
            composable(Screen.Allowlist.route) { AllowlistScreen() }
            composable(Screen.Analytics.route) { AnalyticsScreen() }
            composable(Screen.Logs.route) { LogsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
