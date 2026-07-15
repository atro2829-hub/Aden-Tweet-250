package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String) = repo.sendPasswordReset(email)
}