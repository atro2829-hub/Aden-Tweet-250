package com.adentweets.app.domain.usecase.post

import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class BookmarkPostUseCase @Inject constructor(
    private val repo: PostRepository
) {
    suspend operator fun invoke(postId: String, isBookmarked: Boolean) {
        if (isBookmarked) repo.bookmarkPost(postId) else repo.removeBookmark(postId)
    }
}