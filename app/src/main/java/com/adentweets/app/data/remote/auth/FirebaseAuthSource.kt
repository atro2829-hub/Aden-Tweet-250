package com.adentweets.app.data.remote.auth

import android.app.Activity
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {
    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun createUserWithEmail(email: String, password: String): AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signInWithGoogle(idToken: String): AuthResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential).await()
    }

    suspend fun signInWithPhone(verificationId: String, otp: String): AuthResult {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        return auth.signInWithCredential(credential).await()
    }

    suspend fun sendOtp(phoneNumber: String, activity: Activity): String {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
                override fun onVerificationFailed(e: FirebaseException) {}
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {}
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        return ""
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun signOut() = auth.signOut()

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun currentUserId(): String? = auth.currentUser?.uid

    fun getUsersRef(): DatabaseReference = database.getReference("users")
}