package com.adentweets.app.domain.usecase.post

import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class QuotePostUseCase @Inject constructor(
    private val repo: PostRepository
) {
    suspend operator fun invoke(postId: String, quoteContent: String) = repo.quotePost(postId, quoteContent)
}