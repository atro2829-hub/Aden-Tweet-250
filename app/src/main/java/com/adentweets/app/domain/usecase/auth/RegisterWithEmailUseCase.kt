package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, user: User) = repo.registerWithEmail(email, password, user)
}