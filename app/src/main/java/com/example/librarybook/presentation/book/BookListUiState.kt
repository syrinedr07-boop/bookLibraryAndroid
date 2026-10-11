package com.example.librarybook.presentation.book

import com.example.librarybook.domain.book.model.Book

data class BookListUiState(
    val searchQuery: String = "Android",
    val books: List<Book> = emptyList(),
    val favoriteBooks: List<Book> = emptyList(),
    val selectedBook: Book? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isShowingFavorites: Boolean = false
)
