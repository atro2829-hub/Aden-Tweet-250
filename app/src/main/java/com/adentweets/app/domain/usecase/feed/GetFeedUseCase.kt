package com.adentweets.app.domain.usecase.feed

import com.adentweets.app.domain.repository.FeedRepository
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val repo: FeedRepository
) {
    operator fun invoke(uid: String) = repo.observeHomeFeed(uid)
}