package com.example.librarybook.data.book.repository

import android.content.Context
import com.example.librarybook.data.book.mapper.toDomain
import com.example.librarybook.data.book.remote.BookApiService
import com.example.librarybook.domain.book.model.Book
import com.example.librarybook.domain.book.repository.BookRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class BookRepositoryImpl(
    private val apiService: BookApiService,
    context: Context
) : BookRepository {

    private val prefs = context.getSharedPreferences("library_book_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _favoriteBooks = MutableStateFlow<List<Book>>(loadFavoritesFromPrefs())
    override val favoriteBooks: Flow<List<Book>> = _favoriteBooks.asStateFlow()

    private fun loadFavoritesFromPrefs(): List<Book> {
        val json = prefs.getString("favorite_books_json", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Book>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveFavoritesToPrefs(books: List<Book>) {
        try {
            val json = gson.toJson(books)
            prefs.edit().putString("favorite_books_json", json).apply()
        } catch (_: Exception) {
            // ignore
        }
    }

    override suspend fun searchBooks(query: String): Result<List<Book>> {
        return try {
            val response = apiService.searchBooks(query = query)
            val books = response.items?.map { it.toDomain() } ?: emptyList()
            Result.success(books)
        } catch (_: IOException) {
            Result.failure(Exception("No internet connection. Please check your network settings."))
        } catch (_: SocketTimeoutException) {
            Result.failure(Exception("Connection timed out. Please try again later."))
        } catch (_: UnknownHostException) {
            Result.failure(Exception("Unable to connect to server. Check your internet connection."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "An unexpected error occurred."))
        }
    }

    override suspend fun getBookDetails(bookId: String): Result<Book> {
        return try {
            val response = apiService.getBookDetails(bookId = bookId)
            Result.success(response.toDomain())
        } catch (_: IOException) {
            Result.failure(Exception("No internet connection."))
        } catch (_: SocketTimeoutException) {
            Result.failure(Exception("Connection timed out."))
        } catch (_: UnknownHostException) {
            Result.failure(Exception("Unable to connect to server."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "An unexpected error occurred."))
        }
    }

    override suspend fun toggleFavorite(book: Book) {
        val current = _favoriteBooks.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == book.id }
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
        } else {
            current.add(book)
        }
        _favoriteBooks.update { current }
        saveFavoritesToPrefs(current)
    }

    override suspend fun isFavorite(bookId: String): Boolean {
        return _favoriteBooks.value.any { it.id == bookId }
    }
}
