package com.example.librarybook.presentation.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.librarybook.presentation.book.components.BookDetailDialog
import com.example.librarybook.presentation.book.components.BookItemCard
import com.example.librarybook.presentation.book.components.BookSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    viewModel: BookViewModel,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val quickFilters = listOf("Android", "Kotlin", "Fiction", "Science", "History", "Design")
    val displayList = if (uiState.isShowingFavorites) uiState.favoriteBooks else uiState.books

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isShowingFavorites) "My Favorites ⭐" else "Library Book",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleShowFavorites() }) {
                        Icon(
                            imageVector = if (uiState.isShowingFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorites",
                            tint = if (uiState.isShowingFavorites) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (!uiState.isShowingFavorites) {
                Spacer(modifier = Modifier.height(12.dp))

                BookSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickFilters) { filter ->
                        FilterChip(
                            selected = uiState.searchQuery.equals(filter, ignoreCase = true),
                            onClick = { viewModel.onSearchQueryChanged(filter) },
                            label = { Text(filter) }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your Saved Favorite Books (${uiState.favoriteBooks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading && !uiState.isShowingFavorites -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.errorMessage != null && !uiState.isShowingFavorites -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.errorMessage!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.searchBooks() }) {
                                Text("Retry")
                            }
                        }
                    }

                    displayList.isEmpty() -> {
                        Text(
                            text = if (uiState.isShowingFavorites) "No favorite books added yet. Tap the heart icon on any book to save it!" else "No books found",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = displayList,
                                key = { it.id }
                            ) { book ->
                                val isFav = uiState.favoriteBooks.any { it.id == book.id }
                                BookItemCard(
                                    book = book,
                                    isFavorite = isFav,
                                    onClick = { viewModel.onBookSelected(book) },
                                    onToggleFavorite = { viewModel.toggleFavorite(book) }
                                )
                            }
                        }
                    }
                }
            }
        }

        uiState.selectedBook?.let { selectedBook ->
            val isFav = uiState.favoriteBooks.any { it.id == selectedBook.id }
            BookDetailDialog(
                book = selectedBook,
                isFavorite = isFav,
                onToggleFavorite = { viewModel.toggleFavorite(selectedBook) },
                onDismiss = viewModel::onDismissBookDetail
            )
        }
    }
}
