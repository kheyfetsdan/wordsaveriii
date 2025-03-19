package com.example.mysimpleapp.di

import android.content.Context
import com.example.mysimpleapp.data.api.RetrofitClient
import com.example.mysimpleapp.data.repository.AuthRepository
import com.example.mysimpleapp.data.repository.RepositoryAdapter
import com.example.mysimpleapp.data.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context): AuthRepository {
        return AuthRepository(context)
    }
    
    @Provides
    @Singleton
    fun provideWordRepository(authRepository: AuthRepository): WordRepository {
        return WordRepository(authRepository)
    }
    
    @Provides
    @Singleton
    fun provideRepositoryAdapter(authRepository: AuthRepository): RepositoryAdapter {
        return RepositoryAdapter(authRepository)
    }
} 