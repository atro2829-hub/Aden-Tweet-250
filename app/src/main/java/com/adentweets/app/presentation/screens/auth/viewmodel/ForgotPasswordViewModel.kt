package com.adentweets.app.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.usecase.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSent = MutableStateFlow(false)
    val isSent: StateFlow<Boolean> = _isSent.asStateFlow()

    fun onEmailChanged(value: String) {
        _email.value = value
        _emailError.value = null
        _error.value = null
    }

    fun sendResetLink() {
        val emailVal = _email.value.trim()

        if (emailVal.isBlank()) {
            _emailError.value = "يرجى إدخال البريد الإلكتروني"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            _emailError.value = "يرجى إدخال بريد إلكتروني صحيح"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            resetPasswordUseCase(emailVal)
                .onSuccess {
                    _isSent.value = true
                }
                .onFailure { throwable ->
                    _error.value = throwable.localizedMessage ?: "حدث خطأ أثناء إرسال الرابط"
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun reset() {
        _email.value = ""
        _emailError.value = null
        _error.value = null
        _isSent.value = false
        _isLoading.value = false
    }
}