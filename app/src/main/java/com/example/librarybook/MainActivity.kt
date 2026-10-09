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
import com.example.librarybook.presentation.auth.AuthScreen
import com.example.librarybook.presentation.auth.AuthViewModel
import com.example.librarybook.presentation.book.BookListScreen
import com.example.librarybook.presentation.book.BookViewModel
import com.example.librarybook.ui.theme.LibraryBookTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryBookTheme {
                val authViewModel: AuthViewModel = koinViewModel()
                val bookViewModel: BookViewModel = koinViewModel()
                val authState by authViewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (authState.currentUser != null) {
                        BookListScreen(
                            viewModel = bookViewModel,
                            onSignOut = { authViewModel.signOut() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        AuthScreen(
                            viewModel = authViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}