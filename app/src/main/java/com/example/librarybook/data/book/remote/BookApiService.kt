package com.example.librarybook.data.book.remote

import com.example.librarybook.BuildConfig
import com.example.librarybook.data.book.remote.dto.BookItemDto
import com.example.librarybook.data.book.remote.dto.BookSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String = API_KEY,
        @Query("maxResults") maxResults: Int = 30
    ): BookSearchResponseDto

    @GET("volumes/{id}")
    suspend fun getBookDetails(
        @Path("id") bookId: String,
        @Query("key") apiKey: String = API_KEY
    ): BookItemDto

    companion object {
        const val BASE_URL = "https://www.googleapis.com/books/v1/"
        val API_KEY: String = BuildConfig.GOOGLE_BOOKS_API_KEY
    }
}
