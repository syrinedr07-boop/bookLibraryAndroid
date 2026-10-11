package com.example.librarybook.di

import com.example.librarybook.data.auth.AuthRepositoryImpl
import com.example.librarybook.data.book.remote.BookApiService
import com.example.librarybook.data.book.repository.BookRepositoryImpl
import com.example.librarybook.domain.auth.repository.AuthRepository
import com.example.librarybook.domain.auth.usecase.GetCurrentUserUseCase
import com.example.librarybook.domain.auth.usecase.SignInUseCase
import com.example.librarybook.domain.auth.usecase.SignOutUseCase
import com.example.librarybook.domain.auth.usecase.SignUpUseCase
import com.example.librarybook.domain.book.repository.BookRepository
import com.example.librarybook.domain.book.usecase.GetBookDetailsUseCase
import com.example.librarybook.domain.book.usecase.SearchBooksUseCase
import com.example.librarybook.presentation.auth.AuthViewModel
import com.example.librarybook.presentation.book.BookViewModel
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(BookApiService.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BookApiService::class.java)
    }
}

val authModule = module {
    single { FirebaseAuth.getInstance() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { SignInUseCase(get()) }
    factory { SignUpUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    viewModel { AuthViewModel(get(), get(), get(), get()) }
}

val bookModule = module {
    single<BookRepository> { BookRepositoryImpl(get(), androidContext()) }
    factory { SearchBooksUseCase(get()) }
    factory { GetBookDetailsUseCase(get()) }
    viewModel { BookViewModel(get(), get(), get()) }
}

val appModules = listOf(networkModule, authModule, bookModule)
