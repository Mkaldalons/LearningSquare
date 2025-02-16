package hbv601g.learningsquare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hbv601g.learningsquare.model.LoginRequest
import hbv601g.learningsquare.network.RetrofitInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(username, password)
                val response = RetrofitInstance.api.login(request)
                if (response.isSuccessful && response.body()?.isInstructor == true) {
                    _loginSuccess.value = true
                } else {
                    _errorMessage.value = response.body()?.message ?: "Invalid credentials"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}
