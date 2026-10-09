package com.example.librarybook.domain.auth

import com.example.librarybook.domain.auth.model.User
import com.example.librarybook.domain.auth.repository.AuthRepository
import com.example.librarybook.domain.auth.usecase.SignInUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignInUseCaseTest {

    private val fakeAuthRepository = object : AuthRepository {
        override val currentUser: Flow<User?> = flowOf(null)

        override suspend fun signIn(email: String, password: String): Result<User> {
            return if (email == "test@example.com" && password == "password123") {
                Result.success(User(uid = "user123", email = email, displayName = "Test User"))
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        }

        override suspend fun signUp(email: String, password: String): Result<User> {
            TODO("Not needed for this test")
        }

        override fun signOut() {}
    }

    private val useCase = SignInUseCase(fakeAuthRepository)

    @Test
    fun `blank email should return error without calling repository`() = runTest {
        val result = useCase("", "password123")
        assertTrue(result.isFailure)
        assertEquals("Email is required.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `blank password should return error without calling repository`() = runTest {
        val result = useCase("test@example.com", "  ")
        assertTrue(result.isFailure)
        assertEquals("Password is required.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `valid credentials should return success User`() = runTest {
        val result = useCase("test@example.com", "password123")
        assertTrue(result.isSuccess)
        assertEquals("user123", result.getOrNull()?.uid)
    }
}
