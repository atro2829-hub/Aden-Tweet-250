package com.adentweets.app.domain.model

data class Poll(
    val pollId: String = "",
    val postId: String = "",
    val options: List<PollOption> = emptyList(),
    val endsAt: Long = 0,
    val totalVotes: Int = 0,
    val votedByCurrentUser: Boolean = false
)

data class PollOption(
    val optionId: String = "",
    val text: String = "",
    val votesCount: Int = 0
)