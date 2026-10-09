package com.example.librarybook.domain.auth.model

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?
)
