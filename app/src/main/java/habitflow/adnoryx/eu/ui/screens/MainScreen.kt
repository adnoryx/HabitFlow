package habitflow.adnoryx.eu.ui.screens

import android.widget.Toast
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.ui.components.AddEditHabitDialog
import habitflow.adnoryx.eu.ui.components.FreezeConfirmationDialog
import habitflow.adnoryx.eu.ui.components.HeaderTopBar
import habitflow.adnoryx.eu.ui.components.LanguageSelectionDialog
import habitflow.adnoryx.eu.ui.components.SettingsDialog
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.viewmodel.HabitViewModel
import habitflow.adnoryx.eu.util.LanguageManager
import habitflow.adnoryx.eu.util.OnboardingManager

enum class NavTab(val titleRes: Int, val icon: ImageVector) {
    TODAY(habitflow.adnoryx.eu.R.string.nav_today, Icons.Default.CheckCircle),
    ROUTINES(habitflow.adnoryx.eu.R.string.nav_routines, Icons.Default.FormatListNumbered),
    TROPHIES(habitflow.adnoryx.eu.R.string.nav_trophies, Icons.Default.EmojiEvents)
}

@Composable
fun MainScreen(
    viewModel: HabitViewModel
) {
    val rawContext = LocalContext.current
    val currentLang = LanguageManager.currentLanguage.value

    val localizedContext = remember(currentLang, rawContext) {
        LanguageManager.updateContextLocale(rawContext, currentLang)
    }

    val activityOwner = rawContext as? ActivityResultRegistryOwner

    var showOnboarding by remember {
        mutableStateOf(!OnboardingManager.hasSeenOnboarding(localizedContext))
    }

    var showSettingsDialog by remember { mutableStateOf(false) }

    val providers = if (activityOwner != null) {
        arrayOf(
            LocalContext provides localizedContext,
            LocalActivityResultRegistryOwner provides activityOwner
        )
    } else {
        arrayOf(
            LocalContext provides localizedContext
        )
    }

    CompositionLocalProvider(values = providers) {
        val context = LocalContext.current
        var selectedTab by remember { mutableIntStateOf(0) }

        val userProfile by viewModel.userProfile.collectAsState()
        val habits by viewModel.activeHabits.collectAsState()
        val completions by viewModel.completionsForSelectedDate.collectAsState()
        val freezeQuests by viewModel.freezeQuests.collectAsState()
        val selectedDate by viewModel.selectedDate.collectAsState()
        val timeFilter by viewModel.timeOfDayFilter.collectAsState()

        val showFreezeConfirmation by viewModel.showFreezeConfirmation.collectAsState()
        val showAddEditDialog by viewModel.showAddEditHabitDialog.collectAsState()
        val editingHabit by viewModel.editingHabit.collectAsState()
        val showLanguageDialog by viewModel.showLanguageDialog.collectAsState()
        val toastMessage by viewModel.toastMessage.collectAsState()

        val exportLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null) {
                viewModel.exportBackup(context, uri)
            }
        }

        val importLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                viewModel.importBackup(context, uri)
            }
        }

        LaunchedEffect(toastMessage) {
            toastMessage?.let { msg ->
                val text = when (msg) {
                    "already_used_today" -> context.getString(habitflow.adnoryx.eu.R.string.freeze_already_used_today)
                    "no_freezes" -> context.getString(habitflow.adnoryx.eu.R.string.no_freezes_available)
                    "export_success" -> context.getString(habitflow.adnoryx.eu.R.string.export_success)
                    "import_success" -> context.getString(habitflow.adnoryx.eu.R.string.import_success)
                    else -> context.getString(habitflow.adnoryx.eu.R.string.export_import_failed)
                }
                Toast.makeText(context, text, Toast.LENGTH_LONG).show()
                viewModel.clearToastMessage()
            }
        }

        if (showOnboarding) {
            OnboardingScreen(
                onFinish = {
                    OnboardingManager.setOnboardingSeen(context, true)
                    showOnboarding = false
                }
            )
        } else {
            Scaffold(
                modifier = Modifier.statusBarsPadding(),
                topBar = {
                    HeaderTopBar(
                        userProfile = userProfile,
                        onSettingsClick = { showSettingsDialog = true }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = GamingSurface,
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    ) {
                        NavTab.entries.forEachIndexed { index, tab ->
                            val isSelected = (selectedTab == index)
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = index },
                                icon = {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(GamingBlue)
                                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = null,
                                            tint = TextMuted
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = context.getString(tab.titleRes),
                                        color = if (isSelected) GamingBlue else TextMuted,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                },
                containerColor = GamingBackground
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (selectedTab) {
                        0 -> TodayScreen(
                            userProfile = userProfile,
                            habits = habits,
                            completions = completions,
                            freezeQuests = freezeQuests,
                            selectedDate = selectedDate,
                            timeFilter = timeFilter,
                            onDateSelected = { viewModel.selectDate(it) },
                            onFilterSelected = { viewModel.setTimeOfDayFilter(it) },
                            onToggleCompletion = { viewModel.toggleHabitCompletion(it) },
                            onUseFreezeClicked = { viewModel.onRequestDayFreeze() },
                            onClaimQuest = { viewModel.claimQuest(it) },
                            onAddRoutineClick = { viewModel.openAddHabitDialog() },
                            onEditHabit = { viewModel.openEditHabitDialog(it) },
                            onDeleteHabit = { viewModel.deleteHabit(it) }
                        )
                        1 -> RoutinesScreen(
                            habits = habits,
                            onAddRoutineClick = { viewModel.openAddHabitDialog() },
                            onEditHabit = { viewModel.openEditHabitDialog(it) },
                            onDeleteHabit = { viewModel.deleteHabit(it) }
                        )
                        2 -> TrophiesScreen(
                            userProfile = userProfile
                        )
                    }
                }
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    onDismiss = { showSettingsDialog = false },
                    onLanguageClick = { viewModel.setShowLanguageDialog(true) },
                    onExportClick = { exportLauncher.launch("habitflow_backup.json") },
                    onImportClick = { importLauncher.launch("application/json") },
                    onReplayOnboarding = {
                        showSettingsDialog = false
                        showOnboarding = true
                    }
                )
            }

            if (showFreezeConfirmation) {
                FreezeConfirmationDialog(
                    onConfirm = { viewModel.confirmUseDayFreeze() },
                    onDismiss = { viewModel.dismissFreezeConfirmation() }
                )
            }

            if (showAddEditDialog) {
                AddEditHabitDialog(
                    habitToEdit = editingHabit,
                    onSave = { title, desc, timeOfDay, xp ->
                        viewModel.saveHabit(title, desc, timeOfDay, xp)
                    },
                    onDismiss = { viewModel.dismissAddEditHabitDialog() }
                )
            }

            if (showLanguageDialog) {
                LanguageSelectionDialog(
                    onDismiss = { viewModel.setShowLanguageDialog(false) }
                )
            }
        }
    }
}
