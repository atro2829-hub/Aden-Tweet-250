package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, activity: android.app.Activity) = repo.sendOtp(phoneNumber, activity)
}