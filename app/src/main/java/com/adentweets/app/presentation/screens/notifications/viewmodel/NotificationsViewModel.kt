package com.adentweets.app.presentation.screens.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.domain.model.AppNotification
import com.adentweets.app.domain.usecase.notification.GetNotificationsUseCase
import com.adentweets.app.domain.usecase.notification.MarkNotificationsReadUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markReadUseCase: MarkNotificationsReadUseCase
) : ViewModel() {
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    init {
        if (uid.isNotEmpty()) {
            getNotificationsUseCase(uid).catch {}.collectLatest { _notifications.value = it }
        }
    }

    fun selectTab(index: Int) { _selectedTab.value = index }

    fun markAllRead() {
        viewModelScope.launch { if (uid.isNotEmpty()) markReadUseCase(uid) }
    }
}