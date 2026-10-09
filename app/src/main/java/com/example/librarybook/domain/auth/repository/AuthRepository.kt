package com.example.librarybook.domain.auth.repository

import com.example.librarybook.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    fun signOut()
}
