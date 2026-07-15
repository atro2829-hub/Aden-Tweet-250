package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(idToken: String) = repo.loginWithGoogle(idToken)
}