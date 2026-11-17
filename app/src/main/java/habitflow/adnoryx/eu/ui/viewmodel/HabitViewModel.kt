package habitflow.adnoryx.eu.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCategory
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.HabitType
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.data.repository.BackupRepository
import habitflow.adnoryx.eu.data.repository.HabitRepository
import habitflow.adnoryx.eu.util.DateUtils
import habitflow.adnoryx.eu.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(
    private val repository: HabitRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeHabits: StateFlow<List<Habit>> = repository.activeHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedHabits: StateFlow<List<Habit>> = repository.archivedHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCompletions: StateFlow<List<HabitCompletion>> = repository.allCompletions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val freezeQuests: StateFlow<List<FreezeQuest>> = repository.freezeQuests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow(DateUtils.getTodayString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _timeOfDayFilter = MutableStateFlow<TimeOfDay?>(null)
    val timeOfDayFilter: StateFlow<TimeOfDay?> = _timeOfDayFilter.asStateFlow()

    private val _categoryFilter = MutableStateFlow<HabitCategory?>(null)
    val categoryFilter: StateFlow<HabitCategory?> = _categoryFilter.asStateFlow()

    val completionsForSelectedDate: StateFlow<List<HabitCompletion>> = _selectedDate
        .flatMapLatest { date -> repository.getCompletionsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showFreezeConfirmation = MutableStateFlow(false)
    val showFreezeConfirmation: StateFlow<Boolean> = _showFreezeConfirmation.asStateFlow()

    private val _showAddEditHabitDialog = MutableStateFlow(false)
    val showAddEditHabitDialog: StateFlow<Boolean> = _showAddEditHabitDialog.asStateFlow()

    private val _editingHabit = MutableStateFlow<Habit?>(null)
    val editingHabit: StateFlow<Habit?> = _editingHabit.asStateFlow()

    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _celebrationTrigger = MutableStateFlow(0L)
    val celebrationTrigger: StateFlow<Long> = _celebrationTrigger.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureProfileAndQuestsInitialized()
        }
    }

    fun selectDate(dateString: String) {
        _selectedDate.value = dateString
    }

    fun setTimeOfDayFilter(filter: TimeOfDay?) {
        _timeOfDayFilter.value = filter
    }

    fun setCategoryFilter(category: HabitCategory?) {
        _categoryFilter.value = category
    }

    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            val prevLevel = userProfile.value?.level ?: 1
            val becameCompleted = repository.toggleHabitCompletion(habit, _selectedDate.value)
            val newLevel = userProfile.value?.level ?: prevLevel
            val activeCount = activeHabits.value.size
            val doneToday = completionsForSelectedDate.value.count { it.isCompleted } + (if (becameCompleted) 1 else -1)

            if (becameCompleted && (newLevel > prevLevel || (activeCount > 0 && doneToday >= activeCount))) {
                _celebrationTrigger.value = System.currentTimeMillis()
            }
        }
    }

    fun updateHabitProgress(habit: Habit, newProgress: Int) {
        viewModelScope.launch {
            val prevLevel = userProfile.value?.level ?: 1
            val becameCompleted = repository.updateHabitProgress(habit, _selectedDate.value, newProgress)
            val newLevel = userProfile.value?.level ?: prevLevel
            val activeCount = activeHabits.value.size
            val doneToday = completionsForSelectedDate.value.count { it.isCompleted } + (if (becameCompleted) 1 else 0)

            if (becameCompleted && (newLevel > prevLevel || (activeCount > 0 && doneToday >= activeCount))) {
                _celebrationTrigger.value = System.currentTimeMillis()
            }
        }
    }

    fun buyFreezeWithXp(costXp: Int = 150) {
        viewModelScope.launch {
            val success = repository.buyFreezeWithXp(costXp)
            if (success) {
                _toastMessage.value = "freeze_purchased"
                _celebrationTrigger.value = System.currentTimeMillis()
            } else {
                _toastMessage.value = "not_enough_xp"
            }
        }
    }

    fun onRequestDayFreeze() {
        val profile = userProfile.value ?: return

        if (profile.freezesUsedToday >= 1) {
            _toastMessage.value = "already_used_today"
            return
        }
        if (profile.totalFreezes <= 0) {
            _toastMessage.value = "no_freezes"
            return
        }
        _showFreezeConfirmation.value = true
    }

    fun dismissFreezeConfirmation() {
        _showFreezeConfirmation.value = false
    }

    fun confirmUseDayFreeze() {
        _showFreezeConfirmation.value = false
        viewModelScope.launch {
            val success = repository.useDayFreeze()
            if (!success) {
                _toastMessage.value = "freeze_failed"
            }
        }
    }

    fun claimQuest(questId: String) {
        viewModelScope.launch {
            repository.claimQuestReward(questId)
            _celebrationTrigger.value = System.currentTimeMillis()
        }
    }

    fun openAddHabitDialog() {
        _editingHabit.value = null
        _showAddEditHabitDialog.value = true
    }

    fun openEditHabitDialog(habit: Habit) {
        _editingHabit.value = habit
        _showAddEditHabitDialog.value = true
    }

    fun dismissAddEditHabitDialog() {
        _showAddEditHabitDialog.value = false
        _editingHabit.value = null
    }

    fun saveHabit(
        context: Context,
        title: String,
        description: String,
        timeOfDay: TimeOfDay,
        xpValue: Int,
        icon: String = "⚡",
        category: HabitCategory = HabitCategory.GENERAL,
        habitType: HabitType = HabitType.BOOLEAN,
        targetValue: Int = 1,
        unit: String = "",
        reminderTime: String? = null
    ) {
        val habitToEdit = _editingHabit.value
        viewModelScope.launch {
            if (habitToEdit == null) {
                val created = repository.addHabit(
                    title = title,
                    description = description,
                    timeOfDay = timeOfDay,
                    xpValue = xpValue,
                    icon = icon,
                    category = category,
                    habitType = habitType,
                    targetValue = targetValue,
                    unit = unit,
                    reminderTime = reminderTime
                )
                NotificationHelper.scheduleHabitReminder(context, created)
            } else {
                val updated = habitToEdit.copy(
                    title = title,
                    description = description,
                    timeOfDay = timeOfDay,
                    xpValue = xpValue,
                    icon = icon,
                    category = category,
                    habitType = habitType,
                    targetValue = targetValue,
                    unit = unit,
                    reminderTime = reminderTime
                )
                repository.updateHabit(updated)
                NotificationHelper.scheduleHabitReminder(context, updated)
            }
            dismissAddEditHabitDialog()
        }
    }

    fun archiveHabit(context: Context, habit: Habit, archived: Boolean) {
        viewModelScope.launch {
            repository.archiveHabit(habit, archived)
            if (archived) {
                NotificationHelper.cancelHabitReminder(context, habit.id)
            } else {
                NotificationHelper.scheduleHabitReminder(context, habit.copy(isArchived = false))
            }
        }
    }

    fun moveHabitOrder(habit: Habit, moveUp: Boolean) {
        viewModelScope.launch {
            repository.moveHabitOrder(habit, moveUp)
        }
    }

    fun deleteHabit(context: Context, habit: Habit) {
        viewModelScope.launch {
            NotificationHelper.cancelHabitReminder(context, habit.id)
            repository.deleteHabit(habit)
        }
    }

    fun setShowLanguageDialog(show: Boolean) {
        _showLanguageDialog.value = show
    }

    fun exportBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val json = backupRepository.exportDataToJson()
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray())
                }
                _toastMessage.value = "export_success"
            } catch (e: Exception) {
                e.printStackTrace()
                _toastMessage.value = "export_failed"
            }
        }
    }

    fun importBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                } ?: ""
                val success = backupRepository.importDataFromJson(json)
                if (success) {
                    repository.ensureProfileAndQuestsInitialized()
                    _toastMessage.value = "import_success"
                } else {
                    _toastMessage.value = "import_failed"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _toastMessage.value = "import_failed"
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}

class HabitViewModelFactory(
    private val repository: HabitRepository,
    private val backupRepository: BackupRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            return HabitViewModel(repository, backupRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
