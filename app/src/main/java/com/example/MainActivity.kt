package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.database.*
import com.example.ui.components.LifeSyncBottomBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.LifeSyncTheme
import com.example.ui.theme.PurplePrimary
import com.example.ui.viewmodel.LifeSyncViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LifeSyncTheme {
                val viewModel: LifeSyncViewModel = viewModel()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

                // Request Notification Permission on Android 13+
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Notification permission handled
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                var showQuickAddSheet by remember { mutableStateOf(false) }

                // Quick add sub-dialog triggers
                var showAddQuickTaskDialog by remember { mutableStateOf(false) }
                var showAddQuickEventDialog by remember { mutableStateOf(false) }
                var showAddQuickMeetingDialog by remember { mutableStateOf(false) }
                var showAddQuickPaymentDialog by remember { mutableStateOf(false) }
                var showAddQuickTripDialog by remember { mutableStateOf(false) }
                var showAddQuickNoteDialog by remember { mutableStateOf(false) }
                var showAddQuickGoalDialog by remember { mutableStateOf(false) }
                var showAddQuickContactDialog by remember { mutableStateOf(false) }

                // Core bottom bar tabs
                val isBottomBarVisible = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Calendar.route,
                    Screen.Tasks.route,
                    Screen.Payments.route,
                    Screen.More.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isBottomBarVisible) {
                            LifeSyncBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    },
                    floatingActionButton = {
                        // Show Quick Add FAB on the Home and bottom navigation screens
                        if (isBottomBarVisible) {
                            FloatingActionButton(
                                onClick = { showQuickAddSheet = true },
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .testTag("global_quick_add_fab"),
                                containerColor = PurplePrimary,
                                contentColor = Color.White,
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Quick Add",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) },
                                onOpenQuickAdd = { showQuickAddSheet = true }
                            )
                        }

                        composable(Screen.Calendar.route) {
                            CalendarScreen(
                                viewModel = viewModel
                            )
                        }

                        composable(Screen.Tasks.route) {
                            TasksScreen(
                                viewModel = viewModel
                            )
                        }

                        composable(Screen.Payments.route) {
                            PaymentsScreen(
                                viewModel = viewModel
                            )
                        }

                        composable(Screen.More.route) {
                            MoreScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        // Sub-screens
                        composable(Screen.Work.route) {
                            WorkScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Travel.route) {
                            TravelScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Family.route) {
                            FamilyScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Notes.route) {
                            NotesScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Goals.route) {
                            GoalsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Analytics.route) {
                            AnalyticsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Search.route) {
                            SearchScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Notifications.route) {
                            NotificationHistoryScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }

                // Quick Add Bottom Sheet
                if (showQuickAddSheet) {
                    QuickAddBottomSheet(
                        viewModel = viewModel,
                        onDismiss = { showQuickAddSheet = false },
                        onSelectModuleForm = { module ->
                            showQuickAddSheet = false
                            when (module) {
                                "TASK" -> showAddQuickTaskDialog = true
                                "EVENT" -> showAddQuickEventDialog = true
                                "MEETING" -> showAddQuickMeetingDialog = true
                                "PAYMENT" -> showAddQuickPaymentDialog = true
                                "TRIP" -> showAddQuickTripDialog = true
                                "NOTE" -> showAddQuickNoteDialog = true
                                "GOAL" -> showAddQuickGoalDialog = true
                                "CONTACT" -> showAddQuickContactDialog = true
                            }
                        }
                    )
                }

                // Module Quick Add Dialogs
                if (showAddQuickTaskDialog) {
                    AddEditTaskDialog(
                        initialTask = null,
                        onDismiss = { showAddQuickTaskDialog = false },
                        onSave = {
                            viewModel.addTask(it)
                            showAddQuickTaskDialog = false
                        }
                    )
                }

                if (showAddQuickEventDialog) {
                    AddEditEventDialog(
                        initialEvent = null,
                        initialDate = viewModel.selectedDate.collectAsState().value,
                        onDismiss = { showAddQuickEventDialog = false },
                        onSave = {
                            viewModel.addEvent(it)
                            showAddQuickEventDialog = false
                        }
                    )
                }

                if (showAddQuickMeetingDialog) {
                    AddEditMeetingDialog(
                        initialMeeting = null,
                        onDismiss = { showAddQuickMeetingDialog = false },
                        onSave = {
                            viewModel.addMeeting(it)
                            showAddQuickMeetingDialog = false
                        }
                    )
                }

                if (showAddQuickPaymentDialog) {
                    AddEditPaymentDialog(
                        initialPayment = null,
                        onDismiss = { showAddQuickPaymentDialog = false },
                        onSave = {
                            viewModel.addPayment(it)
                            showAddQuickPaymentDialog = false
                        }
                    )
                }

                if (showAddQuickTripDialog) {
                    AddEditTripDialog(
                        initialTrip = null,
                        onDismiss = { showAddQuickTripDialog = false },
                        onSave = {
                            viewModel.addTrip(it)
                            showAddQuickTripDialog = false
                        }
                    )
                }

                if (showAddQuickNoteDialog) {
                    AddEditNoteDialog(
                        initialNote = null,
                        onDismiss = { showAddQuickNoteDialog = false },
                        onSave = {
                            viewModel.addNote(it)
                            showAddQuickNoteDialog = false
                        }
                    )
                }

                if (showAddQuickGoalDialog) {
                    AddEditGoalDialog(
                        initialGoal = null,
                        onDismiss = { showAddQuickGoalDialog = false },
                        onSave = {
                            viewModel.addGoal(it)
                            showAddQuickGoalDialog = false
                        }
                    )
                }

                if (showAddQuickContactDialog) {
                    AddEditContactDialog(
                        initialContact = null,
                        onDismiss = { showAddQuickContactDialog = false },
                        onSave = {
                            viewModel.addContact(it)
                            showAddQuickContactDialog = false
                        }
                    )
                }
            }
        }
    }
}
