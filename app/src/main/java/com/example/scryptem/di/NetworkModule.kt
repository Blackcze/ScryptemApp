package com.example.scryptem.di


import com.example.scryptem.data.remote.CoinGeckoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinGeckoApi(retrofit: Retrofit): CoinGeckoApiService {
        return retrofit.create(CoinGeckoApiService::class.java)
    }
}
