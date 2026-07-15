package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithPhoneUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(verificationId: String, otp: String) = repo.loginWithPhone(verificationId, otp)
}