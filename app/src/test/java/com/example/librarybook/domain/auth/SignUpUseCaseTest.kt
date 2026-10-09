package com.example.librarybook.domain.auth

import com.example.librarybook.domain.auth.model.User
import com.example.librarybook.domain.auth.repository.AuthRepository
import com.example.librarybook.domain.auth.usecase.SignUpUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpUseCaseTest {

    private val fakeAuthRepository = object : AuthRepository {
        override val currentUser: Flow<User?> = flowOf(null)

        override suspend fun signIn(email: String, password: String): Result<User> {
            TODO("Not needed")
        }

        override suspend fun signUp(email: String, password: String): Result<User> {
            return Result.success(User(uid = "new_user", email = email, displayName = null))
        }

        override fun signOut() {}
    }

    private val useCase = SignUpUseCase(fakeAuthRepository)

    @Test
    fun `password less than 6 chars should fail validation`() = runTest {
        val result = useCase("new@example.com", "12345")
        assertTrue(result.isFailure)
        assertEquals("Password must be at least 6 characters.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `valid email and password should succeed`() = runTest {
        val result = useCase("new@example.com", "securePass123")
        assertTrue(result.isSuccess)
        assertEquals("new_user", result.getOrNull()?.uid)
    }
}
