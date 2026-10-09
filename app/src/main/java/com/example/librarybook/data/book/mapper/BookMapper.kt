package com.example.librarybook.data.book.mapper

import com.example.librarybook.data.book.remote.dto.BookItemDto
import com.example.librarybook.domain.book.model.Book

fun BookItemDto.toDomain(): Book {
    val rawThumbnail = volumeInfoDto?.imageLinks?.thumbnail
        ?: volumeInfoDto?.imageLinks?.smallThumbnail
    val secureThumbnail = rawThumbnail?.replace("http://", "https://")

    return Book(
        id = id ?: "",
        title = volumeInfoDto?.title ?: "No Title Available",
        authors = volumeInfoDto?.authors ?: emptyList(),
        publisher = volumeInfoDto?.publisher ?: "Unknown Publisher",
        publishedDate = volumeInfoDto?.publishedDate ?: "Unknown Date",
        description = volumeInfoDto?.description ?: "No description available.",
        pageCount = volumeInfoDto?.pageCount ?: 0,
        categories = volumeInfoDto?.categories ?: emptyList(),
        rating = volumeInfoDto?.averageRating ?: 0.0,
        ratingsCount = volumeInfoDto?.ratingsCount ?: 0,
        imageThumbnail = secureThumbnail,
        previewLink = volumeInfoDto?.previewLink,
        infoLink = volumeInfoDto?.infoLink
    )
}
