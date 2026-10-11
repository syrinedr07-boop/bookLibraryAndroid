package com.example.librarybook.domain.book.repository

import com.example.librarybook.domain.book.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun searchBooks(query: String): Result<List<Book>>
    suspend fun getBookDetails(bookId: String): Result<Book>
    val favoriteBooks: Flow<List<Book>>
    suspend fun toggleFavorite(book: Book)
    suspend fun isFavorite(bookId: String): Boolean
}
