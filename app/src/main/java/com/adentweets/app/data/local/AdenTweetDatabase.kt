package com.adentweets.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adentweets.app.data.local.dao.PostDao
import com.adentweets.app.data.local.dao.MessageDao
import com.adentweets.app.data.local.dao.UserDao
import com.adentweets.app.data.local.entity.CachedPost
import com.adentweets.app.data.local.entity.CachedMessage
import com.adentweets.app.data.local.entity.CachedUser

@Database(
    entities = [CachedPost::class, CachedUser::class, CachedMessage::class],
    version = 1,
    exportSchema = false
)
abstract class AdenTweetDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
}