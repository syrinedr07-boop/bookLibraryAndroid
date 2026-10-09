package com.example.librarybook.domain.auth.usecase

import com.example.librarybook.domain.auth.model.User
import com.example.librarybook.domain.auth.repository.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email is required."))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        return authRepository.signUp(email.trim(), password)
    }
}
