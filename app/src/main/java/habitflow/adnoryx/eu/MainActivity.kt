package habitflow.adnoryx.eu

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import habitflow.adnoryx.eu.data.local.AppDatabase
import habitflow.adnoryx.eu.data.repository.BackupRepository
import habitflow.adnoryx.eu.data.repository.HabitRepository
import habitflow.adnoryx.eu.ui.screens.MainScreen
import habitflow.adnoryx.eu.ui.theme.HabitFlowTheme
import habitflow.adnoryx.eu.ui.viewmodel.HabitViewModel
import habitflow.adnoryx.eu.ui.viewmodel.HabitViewModelFactory
import habitflow.adnoryx.eu.util.LanguageManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            enableEdgeToEdge()
            enable120HzHighRefreshRate()
        } catch (e: Throwable) {
            Log.e("HabitFlow", "edgeToEdge or 120Hz error", e)
        }

        try {
            LanguageManager.init(this)

            val database = AppDatabase.getDatabase(this)
            val habitRepository = HabitRepository(
                habitDao = database.habitDao(),
                userProfileDao = database.userProfileDao(),
                freezeQuestDao = database.freezeQuestDao()
            )
            val backupRepository = BackupRepository(
                habitDao = database.habitDao(),
                userProfileDao = database.userProfileDao(),
                freezeQuestDao = database.freezeQuestDao()
            )

            val factory = HabitViewModelFactory(habitRepository, backupRepository)
            val viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

            setContent {
                HabitFlowTheme {
                    MainScreen(viewModel = viewModel)
                }
            }
        } catch (e: Throwable) {
            Log.e("HabitFlow", "Startup error", e)
            setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0B0E14))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "HabitFlow Error: ${e.localizedMessage}",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    private fun enable120HzHighRefreshRate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = display
            val maxMode = display?.supportedModes?.maxByOrNull { it.refreshRate }
            if (maxMode != null) {
                val layoutParams = window.attributes
                layoutParams.preferredDisplayModeId = maxMode.modeId
                window.attributes = layoutParams
            }
        }
    }
}
