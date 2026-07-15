package com.adentweets.app.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.usecase.auth.CheckUsernameAvailabilityUseCase
import com.adentweets.app.domain.usecase.auth.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerWithEmailUseCase: RegisterWithEmailUseCase,
    private val checkUsernameAvailabilityUseCase: CheckUsernameAvailabilityUseCase
) : ViewModel() {

    // Step tracking (1 to 4)
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    // Step 1 - Display Name
    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _displayNameError = MutableStateFlow<String?>(null)
    val displayNameError: StateFlow<String?> = _displayNameError.asStateFlow()

    // Step 2 - Username
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

    private val _isUsernameAvailable = MutableStateFlow<Boolean?>(null)
    val isUsernameAvailable: StateFlow<Boolean?> = _isUsernameAvailable.asStateFlow()

    private val _isCheckingUsername = MutableStateFlow(false)
    val isCheckingUsername: StateFlow<Boolean> = _isCheckingUsername.asStateFlow()

    // Step 3 - Email & Password
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _confirmPasswordError = MutableStateFlow<String?>(null)
    val confirmPasswordError: StateFlow<String?> = _confirmPasswordError.asStateFlow()

    // General
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isRegisterSuccess = MutableStateFlow(false)
    val isRegisterSuccess: StateFlow<Boolean> = _isRegisterSuccess.asStateFlow()

    // --- Step 1 ---
    fun onDisplayNameChanged(value: String) {
        _displayName.value = value
        _displayNameError.value = null
    }

    fun validateStep1(): Boolean {
        val name = _displayName.value.trim()
        return when {
            name.isBlank() -> {
                _displayNameError.value = "يرجى إدخال الاسم"
                false
            }
            name.length < 3 -> {
                _displayNameError.value = "يجب أن يكون الاسم ٣ أحرف على الأقل"
                false
            }
            else -> true
        }
    }

    // --- Step 2 ---
    fun onUsernameChanged(value: String) {
        val sanitized = value.filter { it.isLetterOrDigit() || it == '_' }
        _username.value = sanitized
        _usernameError.value = null
        _isUsernameAvailable.value = null
    }

    fun checkUsernameAvailability() {
        val uname = _username.value.trim()
        if (uname.isBlank()) {
            _usernameError.value = "يرجى إدخال اسم المستخدم"
            return
        }
        if (uname.length < 3) {
            _usernameError.value = "يجب أن يكون اسم المستخدم ٣ أحرف على الأقل"
            return
        }
        viewModelScope.launch {
            _isCheckingUsername.value = true
            checkUsernameAvailabilityUseCase(uname)
                .onSuccess { available ->
                    _isUsernameAvailable.value = available
                    if (!available) {
                        _usernameError.value = "اسم المستخدم مستخدم بالفعل"
                    }
                }
                .onFailure {
                    _usernameError.value = "حدث خطأ أثناء التحقق"
                }
            _isCheckingUsername.value = false
        }
    }

    fun validateStep2(): Boolean {
        val uname = _username.value.trim()
        return when {
            uname.isBlank() -> {
                _usernameError.value = "يرجى إدخال اسم المستخدم"
                false
            }
            uname.length < 3 -> {
                _usernameError.value = "يجب أن يكون اسم المستخدم ٣ أحرف على الأقل"
                false
            }
            _isUsernameAvailable.value != true -> {
                _usernameError.value = "يرجى التحقق من توفر اسم المستخدم"
                false
            }
            else -> true
        }
    }

    // --- Step 3 ---
    fun onEmailChanged(value: String) {
        _email.value = value
        _emailError.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _passwordError.value = null
    }

    fun onConfirmPasswordChanged(value: String) {
        _confirmPassword.value = value
        _confirmPasswordError.value = null
    }

    fun validateStep3(): Boolean {
        var valid = true
        val emailVal = _email.value.trim()
        val passVal = _password.value
        val confirmVal = _confirmPassword.value

        if (emailVal.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            _emailError.value = "يرجى إدخال بريد إلكتروني صحيح"
            valid = false
        }
        if (passVal.length < 6) {
            _passwordError.value = "يجب أن تكون كلمة المرور ٦ أحرف على الأقل"
            valid = false
        }
        if (passVal != confirmVal) {
            _confirmPasswordError.value = "كلمتا المرور غير متطابقتين"
            valid = false
        }
        return valid
    }

    // --- Navigation ---
    fun nextStep() {
        val step = _currentStep.value
        val isValid = when (step) {
            1 -> validateStep1()
            2 -> validateStep2()
            3 -> validateStep3()
            else -> false
        }
        if (isValid) {
            if (step < 4) {
                _currentStep.value = step + 1
            }
        }
    }

    fun previousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }

    // --- Registration ---
    fun register() {
        if (!validateStep3()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            registerWithEmailUseCase(
                displayName = _displayName.value.trim(),
                username = _username.value.trim(),
                email = _email.value.trim(),
                password = _password.value
            )
                .onSuccess {
                    _currentStep.value = 4
                    _isRegisterSuccess.value = true
                }
                .onFailure { throwable ->
                    _error.value = throwable.localizedMessage ?: "حدث خطأ أثناء إنشاء الحساب"
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}