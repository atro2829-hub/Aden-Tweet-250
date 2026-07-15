package com.adentweets.app.di

import android.content.Context
import androidx.room.Room
import com.adentweets.app.data.local.AdenTweetDatabase
import com.adentweets.app.data.local.dao.MessageDao
import com.adentweets.app.data.local.dao.PostDao
import com.adentweets.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AdenTweetDatabase {
        return Room.databaseBuilder(context, AdenTweetDatabase::class.java, "aden_tweet_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePostDao(db: AdenTweetDatabase): PostDao = db.postDao()

    @Provides
    fun provideMessageDao(db: AdenTweetDatabase): MessageDao = db.messageDao()

    @Provides
    fun provideUserDao(db: AdenTweetDatabase): UserDao = db.userDao()
}