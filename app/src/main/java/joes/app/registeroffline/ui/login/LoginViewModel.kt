package joes.app.registeroffline.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import joes.app.registeroffline.data.model.LoginRequest
import joes.app.registeroffline.data.model.User
import joes.app.registeroffline.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val profile = repository.getLoggedInUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val token = profile.map { it?.token }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onEmailChange(newEmail: String) {
        _email.update { newEmail }
    }

    fun onPasswordChange(newPassword: String) {
        _password.update { newPassword }
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.update { !it }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _isLoading.update { true }
            _errorMessage.update { null }
            try {
                Log.d("LoginViewModel", ">>> START LOGIN REQUEST")
                val loginResponse = repository.login(LoginRequest(_email.value, _password.value))
                
                if (loginResponse.isSuccessful) {
                    val receivedToken = loginResponse.body()?.token
                    Log.d("LoginViewModel", ">>> LOGIN SUCCESS. Token: $receivedToken")
                    
                    if (!receivedToken.isNullOrBlank()) {
                        Log.d("LoginViewModel", ">>> START PROFILE REQUEST")
                        val profileResponse = repository.getProfile(receivedToken)
                        
                        if (profileResponse.isSuccessful) {
                            val profileData = profileResponse.body()
                            if (profileData != null) {
                                // Save to Room for persistence
                                val user = User(
                                    id = profileData.id,
                                    name = profileData.fullName,
                                    email = profileData.email,
                                    token = receivedToken
                                )
                                repository.saveLoggedInUser(user)
                                Log.d("LoginViewModel", ">>> USER SAVED TO ROOM")
                            } else {
                                _errorMessage.update { "Profile data is empty" }
                            }
                        } else {
                            val errorBody = profileResponse.errorBody()?.string()
                            Log.e("LoginViewModel", ">>> PROFILE FAILED: ${profileResponse.code()} - $errorBody")
                            _errorMessage.update { "Profile error: ${profileResponse.code()}" }
                        }
                    } else {
                        _errorMessage.update { "Token is missing in response" }
                    }
                } else {
                    val errorBody = loginResponse.errorBody()?.string()
                    Log.e("LoginViewModel", ">>> LOGIN FAILED: ${loginResponse.code()} - $errorBody")
                    _errorMessage.update { "Login failed: ${loginResponse.code()}" }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", ">>> CRITICAL ERROR", e)
                _errorMessage.update { "Network error: ${e.localizedMessage}" }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearLoggedInUser()
            _email.update { "" }
            _password.update { "" }
        }
    }
}
