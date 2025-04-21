package com.example.scryptem.di

import android.content.Context
import com.example.scryptem.data.local.AddressPreferences
import com.example.scryptem.data.local.AmountPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideAddressPreferences(
        @ApplicationContext context: Context
    ): AddressPreferences {
        return AddressPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAmountPreferences(@ApplicationContext context: Context): AmountPreferences {
        return AmountPreferences(context)
    }

}