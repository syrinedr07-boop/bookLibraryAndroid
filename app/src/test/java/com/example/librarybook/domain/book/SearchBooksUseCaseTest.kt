package com.example.librarybook.domain.book

import com.example.librarybook.domain.book.model.Book
import com.example.librarybook.domain.book.repository.BookRepository
import com.example.librarybook.domain.book.usecase.SearchBooksUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchBooksUseCaseTest {

    private val fakeRepository = object : BookRepository {
        var lastSearchQuery: String? = null
        var shouldReturnError = false

        override suspend fun searchBooks(query: String): Result<List<Book>> {
            lastSearchQuery = query
            return if (shouldReturnError) {
                Result.failure(Exception("Network error"))
            } else {
                Result.success(
                    listOf(
                        Book(
                            id = "1",
                            title = "Kotlin in Action",
                            authors = listOf("Dmitry Jemerov"),
                            publisher = "Manning",
                            publishedDate = "2017",
                            description = "Great book on Kotlin",
                            pageCount = 360,
                            categories = listOf("Computers"),
                            rating = 4.8,
                            ratingsCount = 120,
                            imageThumbnail = "https://example.com/cover.jpg",
                            previewLink = null,
                            infoLink = null
                        )
                    )
                )
            }
        }

        override suspend fun getBookDetails(bookId: String): Result<Book> {
            TODO("Not needed for this test")
        }
    }

    private val useCase = SearchBooksUseCase(fakeRepository)

    @Test
    fun `empty query should fallback to default search term android`() = runTest {
        val result = useCase("")
        assertEquals("android", fakeRepository.lastSearchQuery)
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `non empty query should be trimmed and passed to repository`() = runTest {
        val result = useCase("  Jetpack Compose  ")
        assertEquals("Jetpack Compose", fakeRepository.lastSearchQuery)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `repository error should return failure Result`() = runTest {
        fakeRepository.shouldReturnError = true
        val result = useCase("Kotlin")
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
