package com.adentweets.app.data.repository

import android.app.Activity
import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.auth.FirebaseAuthSource
import com.adentweets.app.data.remote.user.FirebaseUserSource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authSource: FirebaseAuthSource,
    private val userSource: FirebaseUserSource
) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): Resource<User> {
        return try {
            val result = authSource.signInWithEmail(email, password)
            val uid = result.user?.uid ?: return Resource.Error("Login failed: no user")
            val snapshot = userSource.getUserById(uid)
            val user = snapshot?.toDomainUser() ?: User(uid = uid, email = email)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun registerWithEmail(email: String, password: String, user: User): Resource<User> {
        return try {
            val result = authSource.createUserWithEmail(email, password)
            val uid = result.user?.uid ?: return Resource.Error("Registration failed: no user")
            val profileData = mapOf(
                "username" to user.username,
                "displayName" to user.displayName,
                "bio" to user.bio,
                "avatarBase64" to user.avatarBase64,
                "bannerBase64" to user.bannerBase64,
                "isVerified" to false,
                "isPremium" to false,
                "followersCount" to 0,
                "followingCount" to 0,
                "postsCount" to 0,
                "joinedAt" to System.currentTimeMillis()
            )
            userSource.createUser(uid, profileData)
            Resource.Success(user.copy(uid = uid))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return try {
            val result = authSource.signInWithGoogle(idToken)
            val uid = result.user?.uid ?: return Resource.Error("Google login failed: no user")
            val snapshot = userSource.getUserById(uid)
            val user = snapshot?.toDomainUser() ?: User(
                uid = uid,
                displayName = result.user?.displayName ?: "",
                email = result.user?.email ?: ""
            )
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google login failed")
        }
    }

    override suspend fun loginWithPhone(verificationId: String, otp: String): Resource<User> {
        return try {
            val result = authSource.signInWithPhone(verificationId, otp)
            val uid = result.user?.uid ?: return Resource.Error("Phone login failed: no user")
            val snapshot = userSource.getUserById(uid)
            val user = snapshot?.toDomainUser() ?: User(uid = uid)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Phone login failed")
        }
    }

    override suspend fun sendOtp(phoneNumber: String, activity: Activity): Resource<String> {
        return try {
            authSource.sendOtp(phoneNumber, activity)
            Resource.Success("")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send OTP")
        }
    }

    override suspend fun sendPasswordReset(email: String): Resource<Unit> {
        return try {
            authSource.sendPasswordReset(email)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send password reset")
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            authSource.signOut()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Logout failed")
        }
    }

    override fun getCurrentUser(): FirebaseUser? = authSource.currentUser()

    override fun isLoggedIn(): Boolean = authSource.currentUser() != null

    override fun getCurrentUserId(): String? = authSource.currentUserId()

    private fun com.google.firebase.database.DataSnapshot.toDomainUser(): User {
        return User(
            uid = key ?: "",
            username = child("username").getValue(String::class.java) ?: "",
            displayName = child("displayName").getValue(String::class.java) ?: "",
            bio = child("bio").getValue(String::class.java) ?: "",
            location = child("location").getValue(String::class.java) ?: "",
            website = child("website").getValue(String::class.java) ?: "",
            avatarBase64 = child("avatarBase64").getValue(String::class.java) ?: "",
            bannerBase64 = child("bannerBase64").getValue(String::class.java) ?: "",
            isVerified = child("isVerified").getValue(Boolean::class.java) ?: false,
            isPremium = child("isPremium").getValue(Boolean::class.java) ?: false,
            isPrivate = child("isPrivate").getValue(Boolean::class.java) ?: false,
            joinedAt = child("joinedAt").getValue(Long::class.java) ?: 0L,
            followersCount = child("followersCount").getValue(Int::class.java) ?: 0,
            followingCount = child("followingCount").getValue(Int::class.java) ?: 0,
            postsCount = child("postsCount").getValue(Int::class.java) ?: 0
        )
    }
}