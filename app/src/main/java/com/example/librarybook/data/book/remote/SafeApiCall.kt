package com.example.librarybook.data.book.remote

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

inline fun <T> safeApiCall(apiCall: () -> T): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (_: IOException) {
        Result.failure(Exception("No internet connection. Please check your network settings."))
    } catch (_: SocketTimeoutException) {
        Result.failure(Exception("Connection timed out. Please try again later."))
    } catch (_: UnknownHostException) {
        Result.failure(Exception("Unable to connect to server. Check your internet connection."))
    } catch (e: HttpException) {
        val message = when (e.code()) {
            404 -> "No results found. Please try another search."
            500, 502, 503, 504 -> "Server is temporarily unavailable. Please try again later."
            else -> "A server error occurred. Please try again."
        }
        Result.failure(Exception(message))
    } catch (e: Exception) {
        Result.failure(Exception(e.localizedMessage ?: "An unexpected error occurred."))
    }
}
