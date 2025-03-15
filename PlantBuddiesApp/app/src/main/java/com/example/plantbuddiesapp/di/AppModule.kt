package com.example.plantbuddiesapp.di

import PlantRepositoryImpl
import android.content.Context
import com.example.plantbuddiesapp.data.services.PlantService
import com.example.plantbuddiesapp.domain.repository.PlantRepository
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
    fun providePlantRepository(
        @ApplicationContext context: Context,
        plantService: PlantService
    ): PlantRepository {
        return PlantRepositoryImpl(context, plantService)
    }
}