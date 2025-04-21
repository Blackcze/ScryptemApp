package com.example.scryptem.di


import com.example.scryptem.data.remote.CoinGeckoApiService
import com.example.scryptem.data.remote.MempoolApiService
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
object NetworkModule {

    @Provides
    @Singleton
    @Named("CoinGecko")
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCoinGeckoApi(@Named("CoinGecko") retrofit: Retrofit): CoinGeckoApiService =
        retrofit.create(CoinGeckoApiService::class.java)


    @Provides
    @Singleton
    @Named("Mempool")
    fun provideMempoolRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://mempool.space/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideMempoolApiService(@Named("Mempool") retrofit: Retrofit): MempoolApiService =
        retrofit.create(MempoolApiService::class.java)

}
