package com.adentweets.app.domain.usecase.user

import com.adentweets.app.domain.repository.UserRepository
import javax.inject.Inject

class FollowUserUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(targetUid: String, isFollowing: Boolean) {
        if (isFollowing) repo.followUser(targetUid) else repo.unfollowUser(targetUid)
    }
}