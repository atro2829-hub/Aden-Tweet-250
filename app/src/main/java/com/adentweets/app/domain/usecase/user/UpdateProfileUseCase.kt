package com.adentweets.app.domain.usecase.user

import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(user: User) = repo.updateUserProfile(user)
}