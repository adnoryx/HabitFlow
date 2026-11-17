package habitflow.adnoryx.eu.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.HorizontalDivider
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
import habitflow.adnoryx.eu.ui.components.ConfettiOverlay
import habitflow.adnoryx.eu.ui.components.FreezeConfirmationDialog
import habitflow.adnoryx.eu.ui.components.HeaderTopBar
import habitflow.adnoryx.eu.ui.components.LanguageSelectionDialog
import habitflow.adnoryx.eu.ui.components.SettingsDialog
import habitflow.adnoryx.eu.ui.theme.CharcoalDarkCard
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.viewmodel.HabitViewModel
import habitflow.adnoryx.eu.util.LanguageManager
import habitflow.adnoryx.eu.util.NotificationHelper
import habitflow.adnoryx.eu.util.OnboardingManager

enum class NavTab(val titleRes: Int, val icon: ImageVector) {
    TODAY(habitflow.adnoryx.eu.R.string.nav_today, Icons.Default.CheckCircle),
    ROUTINES(habitflow.adnoryx.eu.R.string.nav_routines, Icons.Default.FormatListNumbered),
    INSIGHTS(habitflow.adnoryx.eu.R.string.nav_insights, Icons.Default.BarChart),
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
        val archivedHabits by viewModel.archivedHabits.collectAsState()
        val allCompletions by viewModel.allCompletions.collectAsState()
        val completions by viewModel.completionsForSelectedDate.collectAsState()
        val freezeQuests by viewModel.freezeQuests.collectAsState()
        val selectedDate by viewModel.selectedDate.collectAsState()
        val timeFilter by viewModel.timeOfDayFilter.collectAsState()
        val categoryFilter by viewModel.categoryFilter.collectAsState()
        val celebrationTrigger by viewModel.celebrationTrigger.collectAsState()

        val showFreezeConfirmation by viewModel.showFreezeConfirmation.collectAsState()
        val showAddEditDialog by viewModel.showAddEditHabitDialog.collectAsState()
        val editingHabit by viewModel.editingHabit.collectAsState()
        val showLanguageDialog by viewModel.showLanguageDialog.collectAsState()
        val toastMessage by viewModel.toastMessage.collectAsState()

        val notifPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ ->
            NotificationHelper.scheduleStreakSaver(context)
        }

        LaunchedEffect(Unit) {
            NotificationHelper.createNotificationChannel(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationHelper.scheduleStreakSaver(context)
            }
        }

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
                    "freeze_purchased" -> when (currentLang.code) {
                        "sk" -> "❄️ Zakúpený +1 Day Freeze!"
                        "cs" -> "❄️ Zakoupen +1 Day Freeze!"
                        else -> "❄️ Purchased +1 Day Freeze!"
                    }
                    "not_enough_xp" -> when (currentLang.code) {
                        "sk" -> "Nedostatok XP (potrebuješ 150 XP)"
                        "cs" -> "Nedostatek XP (potřebuješ 150 XP)"
                        else -> "Not enough XP (150 XP required)"
                    }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GamingBackground)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        HeaderTopBar(
                            userProfile = userProfile,
                            onSettingsClick = { showSettingsDialog = true }
                        )
                    },
                    bottomBar = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GamingSurface)
                        ) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = GamingBorder
                            )
                            NavigationBar(
                                containerColor = GamingSurface,
                                tonalElevation = 0.dp
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
                                                        .background(CharcoalDarkCard)
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
                                                color = if (isSelected) TextPrimary else TextMuted,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                fontSize = 11.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = Color.Transparent
                                        )
                                    )
                                }
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
                                categoryFilter = categoryFilter,
                                onDateSelected = { viewModel.selectDate(it) },
                                onFilterSelected = { viewModel.setTimeOfDayFilter(it) },
                                onCategoryFilterSelected = { viewModel.setCategoryFilter(it) },
                                onToggleCompletion = { viewModel.toggleHabitCompletion(it) },
                                onProgressChange = { habit, prog -> viewModel.updateHabitProgress(habit, prog) },
                                onUseFreezeClicked = { viewModel.onRequestDayFreeze() },
                                onClaimQuest = { viewModel.claimQuest(it) },
                                onAddRoutineClick = { viewModel.openAddHabitDialog() },
                                onArchiveHabit = { viewModel.archiveHabit(context, it, true) },
                                onEditHabit = { viewModel.openEditHabitDialog(it) },
                                onDeleteHabit = { viewModel.deleteHabit(context, it) }
                            )
                            1 -> RoutinesScreen(
                                habits = habits,
                                archivedHabits = archivedHabits,
                                onAddRoutineClick = { viewModel.openAddHabitDialog() },
                                onMoveHabit = { habit, up -> viewModel.moveHabitOrder(habit, up) },
                                onArchiveHabit = { habit, arch -> viewModel.archiveHabit(context, habit, arch) },
                                onEditHabit = { viewModel.openEditHabitDialog(it) },
                                onDeleteHabit = { viewModel.deleteHabit(context, it) }
                            )
                            2 -> InsightsScreen(
                                userProfile = userProfile,
                                habits = habits,
                                allCompletions = allCompletions,
                                onExportClick = { exportLauncher.launch("habitflow_backup.json") },
                                onImportClick = { importLauncher.launch("application/json") },
                                onLanguageClick = { viewModel.setShowLanguageDialog(true) },
                                onReplayOnboarding = { showOnboarding = true }
                            )
                            3 -> TrophiesScreen(
                                userProfile = userProfile,
                                habits = habits,
                                archivedHabits = archivedHabits,
                                allCompletions = allCompletions,
                                freezeQuests = freezeQuests,
                                onBuyFreezeClick = { viewModel.buyFreezeWithXp(150) }
                            )
                        }
                    }
                }

                ConfettiOverlay(trigger = celebrationTrigger)
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
                    onSave = { title, desc, timeOfDay, xp, icon, category, habitType, targetVal, unit, reminderTime ->
                        viewModel.saveHabit(
                            context = context,
                            title = title,
                            description = desc,
                            timeOfDay = timeOfDay,
                            xpValue = xp,
                            icon = icon,
                            category = category,
                            habitType = habitType,
                            targetValue = targetVal,
                            unit = unit,
                            reminderTime = reminderTime
                        )
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
