package com.example.librarybook.presentation.auth

import com.example.librarybook.domain.auth.model.User

data class AuthUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: User? = null,
    val isSignUpMode: Boolean = false
)
