package com.example.mobilelabexam.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobilelabexam.ui.screens.AddNoteScreen
import com.example.mobilelabexam.ui.screens.LockedNotesScreen
import com.example.mobilelabexam.ui.screens.NoteDetailScreen
import com.example.mobilelabexam.ui.screens.NotesScreen
import com.example.mobilelabexam.ui.screens.QuotesScreen
import com.example.mobilelabexam.ui.screens.SplashScreen
import com.example.mobilelabexam.viewmodel.NoteViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Notes", Icons.Default.Home)
    object Add : Screen("add", "Add Note", Icons.Default.Add)
    object Locked : Screen("locked", "Locked", Icons.Default.Lock)
    object Quotes : Screen("quotes", "Quotes", Icons.Default.Star)

    object Detail : Screen("detail/{noteId}", "Detail") {
        fun createRoute(noteId: Int) = "detail/$noteId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: NoteViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if bottom bar should be visible
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Add.route,
        Screen.Locked.route,
        Screen.Quotes.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF1E1E2C), // Modern dark navigation bar
                    contentColor = Color.White
                ) {
                    val items = listOf(Screen.Home, Screen.Add, Screen.Locked, Screen.Quotes)
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF8A2BE2), // Indigo purple selected accent
                                selectedTextColor = Color(0xFF8A2BE2),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF2C2C3E)
                            ),
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onGetStartedClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                NotesScreen(
                    viewModel = viewModel,
                    onNoteClick = { noteId ->
                        navController.navigate(Screen.Detail.createRoute(noteId))
                    }
                )
            }
            composable(Screen.Add.route) {
                AddNoteScreen(
                    viewModel = viewModel,
                    onNoteSaved = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Locked.route) {
                LockedNotesScreen(
                    viewModel = viewModel,
                    onNoteClick = { noteId ->
                        navController.navigate(Screen.Detail.createRoute(noteId))
                    }
                )
            }
            composable(Screen.Quotes.route) {
                QuotesScreen(viewModel = viewModel)
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.IntType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                NoteDetailScreen(
                    noteId = noteId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
