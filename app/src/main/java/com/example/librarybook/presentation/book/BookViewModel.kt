package com.example.librarybook.presentation.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarybook.domain.book.model.Book
import com.example.librarybook.domain.book.usecase.GetBookDetailsUseCase
import com.example.librarybook.domain.book.usecase.SearchBooksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookViewModel(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val getBookDetailsUseCase: GetBookDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookListUiState())
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        searchBooks(_uiState.value.searchQuery)
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            searchBooks(newQuery)
        }
    }

    fun searchBooks(query: String = _uiState.value.searchQuery) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = searchBooksUseCase(query)
            result.fold(
                onSuccess = { books ->
                    _uiState.update {
                        it.copy(
                            books = books,
                            isLoading = false,
                            errorMessage = if (books.isEmpty()) "No books found for \"$query\"" else null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to fetch books."
                        )
                    }
                }
            )
        }
    }

    fun onBookSelected(book: Book) {
        _uiState.update { it.copy(selectedBook = book) }
    }

    fun onDismissBookDetail() {
        _uiState.update { it.copy(selectedBook = null) }
    }
}
