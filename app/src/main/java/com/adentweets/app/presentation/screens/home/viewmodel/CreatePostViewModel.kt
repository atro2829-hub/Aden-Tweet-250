package com.adentweets.app.presentation.screens.home.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.MediaItem
import com.adentweets.app.domain.model.MediaType
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.usecase.media.CompressImageUseCase
import com.adentweets.app.domain.usecase.post.CreatePostUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val compressImageUseCase: CompressImageUseCase
) : ViewModel() {

    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content

    private val _mediaBase64List = MutableStateFlow<List<String>>(emptyList())
    val mediaBase64List: StateFlow<List<String>> = _mediaBase64List

    val charCount: StateFlow<Int> = _content.map { it.length }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isPublished = MutableStateFlow(false)
    val isPublished: StateFlow<Boolean> = _isPublished

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    companion object {
        const val MAX_CHARS = 280
        const val MAX_MEDIA = 4
    }

    fun updateContent(text: String) {
        if (text.length <= MAX_CHARS) {
            _content.value = text
        }
    }

    fun addImage(uri: Uri) {
        if (_mediaBase64List.value.size >= MAX_MEDIA) return
        viewModelScope.launch {
            when (val result = compressImageUseCase(uri, 1200, 500)) {
                is Resource.Success -> {
                    _mediaBase64List.value = _mediaBase64List.value + result.data
                }
                is Resource.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {}
            }
        }
    }

    fun removeImage(index: Int) {
        _mediaBase64List.value = _mediaBase64List.value.toMutableList().apply { removeAt(index) }
    }

    fun createPost(replyToPostId: String? = null) {
        val text = _content.value.trim()
        if (text.isEmpty() && _mediaBase64List.value.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val mediaItems = _mediaBase64List.value.mapIndexed { index, base64 ->
                    MediaItem(
                        mediaId = "temp_$index",
                        type = MediaType.IMAGE,
                        base64Data = base64,
                        mimeType = "image/jpeg"
                    )
                }
                val post = Post(
                    authorId = currentUid,
                    content = text,
                    mediaItems = mediaItems,
                    replyToPostId = replyToPostId
                )
                when (val result = createPostUseCase(post)) {
                    is Resource.Success -> _isPublished.value = true
                    is Resource.Error -> _errorMessage.value = result.message
                    else -> {}
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "حدث خطأ غير متوقع"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _errorMessage.value = null }

    fun resetState() {
        _content.value = ""
        _mediaBase64List.value = emptyList()
        _isLoading.value = false
        _isPublished.value = false
        _errorMessage.value = null
    }
}