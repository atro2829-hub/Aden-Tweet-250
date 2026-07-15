package com.adentweets.app.domain.usecase.post

import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class RetweetPostUseCase @Inject constructor(
    private val repo: PostRepository
) {
    suspend operator fun invoke(postId: String, isRetweeted: Boolean) {
        if (isRetweeted) repo.retweetPost(postId) else repo.undoRetweet(postId)
    }
}