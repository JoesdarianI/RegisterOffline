package joes.app.registeroffline.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import joes.app.registeroffline.data.model.User
import joes.app.registeroffline.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    val users: StateFlow<List<User>> = repository.getLoggedInUser()
        .map { user -> if (user != null) listOf(user) else emptyList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addUser(name: String, email: String) {
        viewModelScope.launch {
            val newUser = User(
                id = UUID.randomUUID().toString(),
                name = name,
                email = email,
                token = "" // Placeholder token for manual add
            )
            repository.saveLoggedInUser(newUser)
        }
    }
}
