package com.adentweets.app.di

import android.content.Context
import com.adentweets.app.core.network.ConnectivityObserver
import com.adentweets.app.core.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(impl: NetworkMonitor): ConnectivityObserver
}