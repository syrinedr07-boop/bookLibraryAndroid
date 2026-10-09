package com.example.librarybook.domain.book.repository

import com.example.librarybook.domain.book.model.Book

interface BookRepository {
    suspend fun searchBooks(query: String): Result<List<Book>>
    suspend fun getBookDetails(bookId: String): Result<Book>
}
