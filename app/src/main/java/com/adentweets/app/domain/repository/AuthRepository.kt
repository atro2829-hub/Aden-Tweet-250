package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Resource<User>
    suspend fun registerWithEmail(email: String, password: String, user: User): Resource<User>
    suspend fun loginWithGoogle(idToken: String): Resource<User>
    suspend fun loginWithPhone(verificationId: String, otp: String): Resource<User>
    suspend fun sendOtp(phoneNumber: String, activity: android.app.Activity): Resource<String>
    suspend fun sendPasswordReset(email: String): Resource<Unit>
    suspend fun logout(): Resource<Unit>
    fun getCurrentUser(): FirebaseUser?
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): String?
}