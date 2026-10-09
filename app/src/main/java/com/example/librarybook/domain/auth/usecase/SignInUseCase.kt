package com.example.librarybook.domain.auth.usecase

import com.example.librarybook.domain.auth.model.User
import com.example.librarybook.domain.auth.repository.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email is required."))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password is required."))
        return authRepository.signIn(email.trim(), password)
    }
}
