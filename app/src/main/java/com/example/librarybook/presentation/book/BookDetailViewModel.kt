package com.example.librarybook.presentation.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarybook.domain.book.model.Book
import com.example.librarybook.domain.book.usecase.GetBookDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookDetailUiState(
    val book: Book? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BookDetailViewModel(
    private val bookId: String,
    private val getBookDetailsUseCase: GetBookDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        loadBookDetails()
    }

    fun loadBookDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getBookDetailsUseCase(bookId)
            result.fold(
                onSuccess = { book ->
                    _uiState.update { it.copy(book = book, isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load book details."
                        )
                    }
                }
            )
        }
    }
}
