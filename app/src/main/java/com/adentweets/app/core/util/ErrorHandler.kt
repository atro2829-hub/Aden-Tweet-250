package com.adentweets.app.core.util

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.FirebaseTooManyRequestsException

object ErrorHandler {
    fun handleFirebaseException(e: Exception): Resource.Error {
        return when (e) {
            is FirebaseNetworkException -> Resource.Error("لا يوجد اتصال بالإنترنت. يرجى المحاولة مرة أخرى.", 2001)
            is FirebaseTooManyRequestsException -> Resource.Error("محاولات كثيرة. يرجى الانتظار والمحاولة مرة أخرى.", 1005)
            is DatabaseException -> Resource.Error("خطأ في قاعدة البيانات. يرجى المحاولة مرة أخرى.", 3001)
            is FirebaseAuthInvalidCredentialsException -> Resource.Error("البريد الإلكتروني أو كلمة المرور غير صحيحة.", 1001)
            is FirebaseAuthInvalidUserException -> Resource.Error("الحساب غير موجود.", 1002)
            is FirebaseAuthUserCollisionException -> Resource.Error("البريد الإلكتروني مستخدم بالفعل.", 1003)
            is FirebaseAuthWeakPasswordException -> Resource.Error("كلمة المرور ضعيفة جداً (8 أحرف على الأقل).", 1004)
            else -> Resource.Error(e.message ?: "حدث خطأ غير متوقع")
        }
    }
}