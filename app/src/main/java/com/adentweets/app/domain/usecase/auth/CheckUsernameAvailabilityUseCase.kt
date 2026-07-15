package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.UserRepository
import javax.inject.Inject

class CheckUsernameAvailabilityUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String): Resource<Boolean> {
        return try {
            val users = userRepository.searchUsersByUsername(username)
            Resource.Success(users.isEmpty())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "فشل التحقق من اسم المستخدم")
        }
    }
}
