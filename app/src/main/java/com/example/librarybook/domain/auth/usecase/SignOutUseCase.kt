package com.example.librarybook.domain.auth.usecase

import com.example.librarybook.domain.auth.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke() {
        authRepository.signOut()
    }
}
