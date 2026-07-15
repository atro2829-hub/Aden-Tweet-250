package com.adentweets.app.domain.usecase.post

import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repo: PostRepository
) {
    suspend operator fun invoke(postId: String) = repo.deletePost(postId)
}