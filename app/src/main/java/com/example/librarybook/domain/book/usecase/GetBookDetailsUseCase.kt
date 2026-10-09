package com.example.librarybook.domain.book.usecase

import com.example.librarybook.domain.book.model.Book
import com.example.librarybook.domain.book.repository.BookRepository

class GetBookDetailsUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: String): Result<Book> {
        return repository.getBookDetails(bookId)
    }
}
