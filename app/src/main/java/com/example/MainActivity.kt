package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.AuthManager
import com.example.data.FinanceRepository
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FinanceViewModel
import com.example.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database components
        val database = AppDatabase.getDatabase(applicationContext)
        val financeDao = database.financeDao()
        val repository = FinanceRepository(financeDao)

        // Instantiate ViewModel with proper factory
        val viewModel: FinanceViewModel by viewModels {
            FinanceViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var isUnlocked by remember { mutableStateOf(AuthManager.isSessionActive()) }

                    Crossfade(targetState = isUnlocked, label = "AuthenticationFlow") { unlocked ->
                        if (unlocked) {
                            MainScreen(
                                viewModel = viewModel,
                                onLogout = {
                                    AuthManager.logout()
                                    isUnlocked = false
                                }
                            )
                        } else {
                            LoginScreen(
                                onLoginSuccess = {
                                    isUnlocked = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
