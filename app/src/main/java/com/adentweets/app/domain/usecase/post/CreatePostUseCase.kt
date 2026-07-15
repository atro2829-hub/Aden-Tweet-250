package com.adentweets.app.domain.usecase.post

import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repo: PostRepository
) {
    suspend operator fun invoke(post: Post) = repo.createPost(post)
}