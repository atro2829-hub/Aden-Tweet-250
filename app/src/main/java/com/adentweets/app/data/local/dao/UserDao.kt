package com.adentweets.app.data.local.dao

import androidx.room.*
import com.adentweets.app.data.local.entity.CachedUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM cached_users WHERE uid = :uid")
    suspend fun getUserById(uid: String): CachedUser?

    @Query("SELECT * FROM cached_users WHERE uid = :uid")
    fun observeUser(uid: String): Flow<CachedUser?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: CachedUser)

    @Query("DELETE FROM cached_users WHERE cachedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("DELETE FROM cached_users")
    suspend fun clearAll()
}