package com.example.librarybook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.librarybook.presentation.auth.AuthScreen
import com.example.librarybook.presentation.auth.AuthViewModel
import com.example.librarybook.presentation.book.BookDetailScreen
import com.example.librarybook.presentation.book.BookDetailViewModel
import com.example.librarybook.presentation.book.BookListScreen
import com.example.librarybook.presentation.book.BookViewModel
import com.example.librarybook.presentation.navigation.Screen
import com.example.librarybook.ui.theme.LibraryBookTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryBookTheme {
                val authViewModel: AuthViewModel = koinViewModel()
                val bookViewModel: BookViewModel = koinViewModel()
                val authState by authViewModel.uiState.collectAsState()
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (authState.currentUser == null) {
                        AuthScreen(
                            viewModel = authViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.BookList.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.BookList.route) {
                                BookListScreen(
                                    viewModel = bookViewModel,
                                    onBookClick = { book ->
                                        navController.navigate(Screen.BookDetail.createRoute(book.id))
                                    },
                                    onSignOut = { authViewModel.signOut() }
                                )
                            }
                            composable(
                                route = Screen.BookDetail.route,
                                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                                val detailViewModel: BookDetailViewModel = koinViewModel { parametersOf(bookId) }
                                val detailState by detailViewModel.uiState.collectAsState()
                                val bookUiState by bookViewModel.uiState.collectAsState()
                                val isFav = detailState.book?.let { b -> bookUiState.favoriteBooks.any { it.id == b.id } } ?: false

                                BookDetailScreen(
                                    viewModel = detailViewModel,
                                    isFavorite = isFav,
                                    onToggleFavorite = {
                                        detailState.book?.let { bookViewModel.toggleFavorite(it) }
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
