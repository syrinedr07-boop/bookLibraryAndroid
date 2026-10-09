package com.example.librarybook.data.book

import com.example.librarybook.data.book.mapper.toDomain
import com.example.librarybook.data.book.remote.dto.BookItemDto
import com.example.librarybook.data.book.remote.dto.ImageLinksDto
import com.example.librarybook.data.book.remote.dto.VolumeInfoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookMapperTest {

    @Test
    fun `toDomain should convert http thumbnail URL to https`() {
        val dto = BookItemDto(
            id = "test_id",
            volumeInfoDto = VolumeInfoDto(
                title = "Android Guide",
                authors = listOf("Author A"),
                publisher = "Tech Press",
                publishedDate = "2024",
                description = "Description",
                pageCount = 200,
                categories = listOf("Tech"),
                averageRating = 4.5,
                ratingsCount = 10,
                imageLinks = ImageLinksDto(
                    smallThumbnail = "http://books.google.com/small.jpg",
                    thumbnail = "http://books.google.com/large.jpg"
                ),
                previewLink = "http://preview.com",
                infoLink = "http://info.com"
            )
        )

        val domainModel = dto.toDomain()

        assertEquals("test_id", domainModel.id)
        assertEquals("Android Guide", domainModel.title)
        assertEquals("https://books.google.com/large.jpg", domainModel.imageThumbnail)
        assertTrue(domainModel.imageThumbnail!!.startsWith("https://"))
    }

    @Test
    fun `toDomain should handle null fields with fallback defaults`() {
        val dto = BookItemDto(
            id = null,
            volumeInfoDto = null
        )

        val domainModel = dto.toDomain()

        assertEquals("", domainModel.id)
        assertEquals("No Title Available", domainModel.title)
        assertTrue(domainModel.authors.isEmpty())
        assertEquals("Unknown Publisher", domainModel.publisher)
        assertEquals("Unknown Date", domainModel.publishedDate)
        assertEquals("No description available.", domainModel.description)
        assertEquals(0, domainModel.pageCount)
    }
}
