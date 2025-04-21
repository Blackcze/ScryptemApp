package com.example.scryptem.di

import com.example.scryptem.data.remote.BlockchairApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BlockchairModule {

    @Provides
    @Singleton
    @Named("blockchair")
    fun provideBlockchairRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.blockchair.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBlockchairApiService(
        @Named("blockchair") retrofit: Retrofit
    ): BlockchairApiService {
        return retrofit.create(BlockchairApiService::class.java)
    }
}
