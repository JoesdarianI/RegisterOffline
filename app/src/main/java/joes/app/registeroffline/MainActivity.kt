package joes.app.registeroffline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import joes.app.registeroffline.data.local.AppDatabase
import joes.app.registeroffline.data.remote.RetrofitClient
import joes.app.registeroffline.data.repository.UserRepositoryImpl
import joes.app.registeroffline.ui.home.HomeScreen
import joes.app.registeroffline.ui.login.LoginScreen
import joes.app.registeroffline.ui.login.LoginViewModel
import joes.app.registeroffline.ui.profile.ProfileScreen
import joes.app.registeroffline.ui.registration.RegistrationScreen
import joes.app.registeroffline.ui.registration.RegistrationViewModel
import joes.app.registeroffline.ui.splash.SplashScreen
import joes.app.registeroffline.ui.theme.RegisterOfflineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepositoryImpl(
            userDao = database.userDao(),
            memberDao = database.memberDao(),
            apiService = RetrofitClient.apiService
        )
        
        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

            RegisterOfflineTheme {
                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return LoginViewModel(userRepository) as T
                            }
                        }
                    )
                    
                    val registrationViewModel: RegistrationViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return RegistrationViewModel(userRepository) as T
                            }
                        }
                    )
                    
                    val profile by loginViewModel.profile.collectAsState()

                    // Automatically move to Home screen when profile is fetched successfully
                    LaunchedEffect(profile) {
                        if (profile != null && currentScreen == Screen.Login) {
                            currentScreen = Screen.Home
                        } else if (profile == null) {
                            currentScreen = Screen.Login
                        }
                    }

                    when (currentScreen) {
                        Screen.Login -> {
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    LoginScreen(viewModel = loginViewModel)
                                }
                            }
                        }
                        Screen.Home -> {
                            HomeScreen(
                                profile = profile,
                                loginViewModel = loginViewModel,
                                registrationViewModel = registrationViewModel,
                                onTambahDataClick = { currentScreen = Screen.Registration },
                                onProfileClick = { currentScreen = Screen.Profile }
                            )
                        }
                        Screen.Registration -> {
                            RegistrationScreen(
                                viewModel = registrationViewModel,
                                onBack = { currentScreen = Screen.Home }
                            )
                        }
                        Screen.Profile -> {
                            ProfileScreen(
                                profile = profile,
                                onBack = { currentScreen = Screen.Home },
                                onLogout = {
                                    loginViewModel.logout()
                                    currentScreen = Screen.Login
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object Registration : Screen()
    object Profile : Screen()
}
