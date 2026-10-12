package com.example.librarybook.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarybook.domain.auth.usecase.GetCurrentUserUseCase
import com.example.librarybook.domain.auth.usecase.SignInUseCase
import com.example.librarybook.domain.auth.usecase.SignOutUseCase
import com.example.librarybook.domain.auth.usecase.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(emailInput = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(passwordInput = password, errorMessage = null) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isSignUpMode = !it.isSignUpMode,
                errorMessage = null
            )
        }
    }

    fun submit() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (state.isSignUpMode) {
                signUpUseCase(state.emailInput, state.passwordInput)
            } else {
                signInUseCase(state.emailInput, state.passwordInput)
            }

            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentUser = user,
                            emailInput = "",
                            passwordInput = "",
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    val rawMsg = error.message ?: ""
                    val userFriendlyMessage = when {
                        rawMsg.contains("no user record", ignoreCase = true) ||
                        rawMsg.contains("user-not-found", ignoreCase = true) -> "No account found with this email."
                        
                        rawMsg.contains("password", ignoreCase = true) && (rawMsg.contains("invalid", ignoreCase = true) || rawMsg.contains("wrong", ignoreCase = true)) -> "Incorrect password. Please try again."
                        
                        rawMsg.contains("already in use", ignoreCase = true) ||
                        rawMsg.contains("email-already-in-use", ignoreCase = true) -> "This email is already registered."
                        
                        rawMsg.contains("badly formatted", ignoreCase = true) ||
                        rawMsg.contains("invalid-email", ignoreCase = true) -> "Please enter a valid email address."
                        
                        else -> error.localizedMessage ?: "Authentication failed. Please check your credentials."
                    }
                    Log.e(TAG, "Authentication failed: $rawMsg", error)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = userFriendlyMessage
                        )
                    }
                }
            )
        }
    }

    fun signOut() {
        signOutUseCase()
    }
}
