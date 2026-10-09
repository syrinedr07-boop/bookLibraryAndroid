package com.example.librarybook.data.book.repository

import com.example.librarybook.data.book.mapper.toDomain
import com.example.librarybook.data.book.remote.BookApiService
import com.example.librarybook.domain.book.model.Book
import com.example.librarybook.domain.book.repository.BookRepository

class BookRepositoryImpl(
    private val apiService: BookApiService
) : BookRepository {

    override suspend fun searchBooks(query: String): Result<List<Book>> {
        return try {
            val response = apiService.searchBooks(query = query)
            val books = response.items?.map { it.toDomain() } ?: emptyList()
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookDetails(bookId: String): Result<Book> {
        return try {
            val response = apiService.getBookDetails(bookId = bookId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
