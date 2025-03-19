package com.example.plantbuddiesapp.di

import android.content.Context
import com.example.plantbuddiesapp.data.repository.AuthRepositoryImpl
import com.example.plantbuddiesapp.data.repository.PlantRepositoryImpl
import com.example.plantbuddiesapp.data.repository.TokenManager
import com.example.plantbuddiesapp.data.repository.UserRepositoryImpl
import com.example.plantbuddiesapp.data.services.AuthService
import com.example.plantbuddiesapp.data.services.PlantService
import com.example.plantbuddiesapp.data.services.UserService
import com.example.plantbuddiesapp.domain.repository.AuthRepository
import com.example.plantbuddiesapp.domain.repository.PlantRepository
import com.example.plantbuddiesapp.domain.repository.UserRepository
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
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        authService: AuthService,
        userService: UserService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(context, authService, userService, tokenManager)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        @ApplicationContext context: Context,
        userService: UserService,
        tokenManager: TokenManager
    ): UserRepository {
        return UserRepositoryImpl(context, userService, tokenManager)
    }

    @Provides
    @Singleton
    fun providePlantRepository(
        @ApplicationContext context: Context,
        plantService: PlantService,
        tokenManager: TokenManager
    ): PlantRepository {
        return PlantRepositoryImpl(context, plantService, tokenManager)
    }
}