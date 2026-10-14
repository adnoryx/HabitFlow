package habitflow.adnoryx.eu.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.data.repository.BackupRepository
import habitflow.adnoryx.eu.data.repository.HabitRepository
import habitflow.adnoryx.eu.util.DateUtils
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

    val freezeQuests: StateFlow<List<FreezeQuest>> = repository.freezeQuests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow(DateUtils.getTodayString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _timeOfDayFilter = MutableStateFlow<TimeOfDay?>(null)
    val timeOfDayFilter: StateFlow<TimeOfDay?> = _timeOfDayFilter.asStateFlow()

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

    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habit, _selectedDate.value)
        }
    }

    fun onRequestDayFreeze() {
        val profile = userProfile.value
        if (profile == null) return

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

    fun saveHabit(title: String, description: String, timeOfDay: TimeOfDay, xpValue: Int) {
        val habitToEdit = _editingHabit.value
        viewModelScope.launch {
            if (habitToEdit == null) {
                repository.addHabit(title, description, timeOfDay, xpValue)
            } else {
                repository.updateHabit(
                    habitToEdit.copy(
                        title = title,
                        description = description,
                        timeOfDay = timeOfDay,
                        xpValue = xpValue
                    )
                )
            }
            dismissAddEditHabitDialog()
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
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
