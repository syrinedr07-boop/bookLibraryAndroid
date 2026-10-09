package com.example.librarybook.domain.auth.usecase

import com.example.librarybook.domain.auth.model.User
import com.example.librarybook.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.currentUser
    }
}
