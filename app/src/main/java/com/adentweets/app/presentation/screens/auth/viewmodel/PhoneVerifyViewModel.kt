package com.adentweets.app.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.usecase.auth.LoginWithPhoneUseCase
import com.adentweets.app.domain.usecase.auth.SendOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneVerifyViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val loginWithPhoneUseCase: LoginWithPhoneUseCase
) : ViewModel() {

    // Phone Number
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _countryCode = MutableStateFlow("+967")
    val countryCode: StateFlow<String> = _countryCode.asStateFlow()

    private val _phoneError = MutableStateFlow<String?>(null)
    val phoneError: StateFlow<String?> = _phoneError.asStateFlow()

    // OTP
    private val _otp = MutableStateFlow(List(6) { "" })
    val otp: StateFlow<List<String>> = _otp.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    // Verification
    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _isCodeSent = MutableStateFlow(false)
    val isCodeSent: StateFlow<Boolean> = _isCodeSent.asStateFlow()

    // Countdown timer
    private val _countdownSeconds = MutableStateFlow(0)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private var countdownJob: Job? = null

    // General
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isVerifySuccess = MutableStateFlow(false)
    val isVerifySuccess: StateFlow<Boolean> = _isVerifySuccess.asStateFlow()

    fun onPhoneNumberChanged(value: String) {
        _phoneNumber.value = value.filter { it.isDigit() }
        _phoneError.value = null
    }

    fun onCountryCodeChanged(code: String) {
        _countryCode.value = code
    }

    fun onOtpChanged(index: Int, value: String) {
        if (value.length > 1) return
        val current = _otp.value.toMutableList()
        current[index] = value.filter { it.isDigit() }
        _otp.value = current
        _otpError.value = null
    }

    fun sendOtp() {
        val phone = _phoneNumber.value.trim()
        if (phone.isBlank()) {
            _phoneError.value = "يرجى إدخال رقم الهاتف"
            return
        }
        if (phone.length < 9) {
            _phoneError.value = "يرجى إدخال رقم هاتف صحيح"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val fullNumber = "${_countryCode.value}$phone"
            sendOtpUseCase(fullNumber)
                .onSuccess { verId ->
                    _verificationId.value = verId
                    _isCodeSent.value = true
                    startCountdown()
                }
                .onFailure { throwable ->
                    _error.value = throwable.localizedMessage ?: "حدث خطأ أثناء إرسال رمز التحقق"
                }
            _isLoading.value = false
        }
    }

    fun verifyOtp() {
        val verId = _verificationId.value
        val code = _otp.value.joinToString("")

        if (verId == null) {
            _error.value = "يرجى إرسال رمز التحقق أولاً"
            return
        }
        if (code.length != 6) {
            _otpError.value = "يرجى إدخال الرمز كاملاً"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            loginWithPhoneUseCase(verId, code)
                .onSuccess {
                    _isVerifySuccess.value = true
                }
                .onFailure { throwable ->
                    _otpError.value = throwable.localizedMessage ?: "رمز التحقق غير صحيح"
                }
            _isLoading.value = false
        }
    }

    fun resendOtp() {
        if (_countdownSeconds.value > 0) return
        _otp.value = List(6) { "" }
        _verificationId.value = null
        _isCodeSent.value = false
        sendOtp()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _countdownSeconds.value = 60
        countdownJob = viewModelScope.launch {
            while (_countdownSeconds.value > 0) {
                delay(1000L)
                _countdownSeconds.value -= 1
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}