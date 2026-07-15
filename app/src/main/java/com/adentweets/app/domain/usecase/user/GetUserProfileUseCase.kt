package com.adentweets.app.domain.usecase.user

import com.adentweets.app.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(uid: String) = repo.getUserProfile(uid)
}