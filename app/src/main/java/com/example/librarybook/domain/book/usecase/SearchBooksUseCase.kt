package com.example.librarybook.domain.book.usecase

import com.example.librarybook.domain.book.model.Book
import com.example.librarybook.domain.book.repository.BookRepository

class SearchBooksUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(query: String): Result<List<Book>> {
        val searchQuery = query.trim().ifEmpty { "android" }
        return repository.searchBooks(searchQuery)
    }
}
