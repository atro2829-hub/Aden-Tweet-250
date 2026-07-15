package com.adentweets.app.presentation.screens.auth.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.usecase.auth.LoginWithEmailUseCase
import com.adentweets.app.domain.usecase.auth.LoginWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess: StateFlow<Boolean> = _isLoginSuccess.asStateFlow()

    fun onEmailChanged(value: String) {
        _email.value = value
        _error.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _error.value = null
    }

    fun loginWithEmail() {
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value

        if (currentEmail.isBlank()) {
            _error.value = "يرجى إدخال البريد الإلكتروني"
            return
        }
        if (currentPassword.isBlank()) {
            _error.value = "يرجى إدخال كلمة المرور"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            loginWithEmailUseCase(currentEmail, currentPassword)
                .onSuccess {
                    _isLoginSuccess.value = true
                }
                .onFailure { throwable ->
                    _error.value = throwable.localizedMessage ?: "حدث خطأ أثناء تسجيل الدخول"
                }
            _isLoading.value = false
        }
    }

    fun loginWithGoogle(activity: ComponentActivity) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            loginWithGoogleUseCase(activity)
                .onSuccess {
                    _isLoginSuccess.value = true
                }
                .onFailure { throwable ->
                    _error.value = throwable.localizedMessage ?: "حدث خطأ أثناء تسجيل الدخول بحساب جوجل"
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}