package com.adentweets.app.domain.usecase.user

import com.adentweets.app.domain.repository.UserRepository
import javax.inject.Inject

class GetFollowingUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(uid: String, limit: Int) = repo.getFollowing(uid, limit)
}