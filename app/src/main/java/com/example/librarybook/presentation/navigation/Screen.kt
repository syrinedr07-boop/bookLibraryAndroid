package com.example.librarybook.presentation.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object BookList : Screen("book_list")
    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }
}
