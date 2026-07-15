package com.adentweets.app.presentation.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.user.GetUserProfileUseCase
import com.adentweets.app.domain.usecase.user.UpdateProfileUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location

    private val _website = MutableStateFlow("")
    val website: StateFlow<String> = _website

    private val _avatarBase64 = MutableStateFlow("")
    val avatarBase64: StateFlow<String> = _avatarBase64

    private val _bannerBase64 = MutableStateFlow("")
    val bannerBase64: StateFlow<String> = _bannerBase64

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    companion object {
        const val MAX_BIO_LENGTH = 160
        const val MAX_DISPLAY_NAME_LENGTH = 50
    }

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            when (val result = getUserProfileUseCase(currentUid)) {
                is Resource.Success -> {
                    val user = result.data
                    _displayName.value = user.displayName
                    _username.value = user.username
                    _bio.value = user.bio
                    _location.value = user.location
                    _website.value = user.website
                    _avatarBase64.value = user.avatarBase64
                    _bannerBase64.value = user.bannerBase64
                }
                else -> {}
            }
        }
    }

    fun updateDisplayName(name: String) {
        if (name.length <= MAX_DISPLAY_NAME_LENGTH) _displayName.value = name
    }

    fun updateUsername(name: String) {
        _username.value = name
    }

    fun updateBio(text: String) {
        if (text.length <= MAX_BIO_LENGTH) _bio.value = text
    }

    fun updateLocation(text: String) { _location.value = text }
    fun updateWebsite(text: String) { _website.value = text }
    fun updateAvatar(base64: String) { _avatarBase64.value = base64 }
    fun updateBanner(base64: String) { _bannerBase64.value = base64 }

    fun saveProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val currentUser = getUserProfileUseCase(currentUid)
                val existingUser = (currentUser as? Resource.Success)?.data ?: return@launch
                val updatedUser = existingUser.copy(
                    displayName = _displayName.value.trim(),
                    username = _username.value.trim(),
                    bio = _bio.value.trim(),
                    location = _location.value.trim(),
                    website = _website.value.trim(),
                    avatarBase64 = _avatarBase64.value,
                    bannerBase64 = _bannerBase64.value
                )
                when (val result = updateProfileUseCase(updatedUser)) {
                    is Resource.Success -> _isSaved.value = true
                    is Resource.Error -> _errorMessage.value = result.message
                    else -> {}
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "حدث خطأ أثناء الحفظ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _errorMessage.value = null }
}