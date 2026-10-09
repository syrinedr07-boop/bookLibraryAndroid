package com.example.librarybook.domain.book.model

data class Book(
    val id: String,
    val title: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: String,
    val description: String,
    val pageCount: Int,
    val categories: List<String>,
    val rating: Double,
    val ratingsCount: Int,
    val imageThumbnail: String?,
    val previewLink: String?,
    val infoLink: String?
)
