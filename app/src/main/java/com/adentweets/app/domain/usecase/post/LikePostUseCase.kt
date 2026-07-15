package com.adentweets.app.domain.usecase.post

import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val repo: PostRepository
) {
    suspend operator fun invoke(postId: String, isLiked: Boolean) {
        if (isLiked) repo.likePost(postId) else repo.unlikePost(postId)
    }
}