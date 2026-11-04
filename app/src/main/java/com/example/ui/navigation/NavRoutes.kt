package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Home")
    data object Calendar : Screen("calendar", "Calendar")
    data object Tasks : Screen("tasks", "Tasks")
    data object Payments : Screen("payments", "Payments")
    data object More : Screen("more", "More")

    // Sub-screens
    data object Work : Screen("work", "Work & Meetings")
    data object Travel : Screen("travel", "Trips & Travel")
    data object Family : Screen("family", "Family & Friends")
    data object Notes : Screen("notes", "Notes")
    data object Goals : Screen("goals", "Goals & Routines")
    data object Analytics : Screen("analytics", "Analytics")
    data object Settings : Screen("settings", "Settings")
    data object Search : Screen("search", "Search")
    data object Notifications : Screen("notifications", "Notifications")
}
