package com.adentweets.app.core.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
    fun getCurrentConnectionStatus(): Boolean
}