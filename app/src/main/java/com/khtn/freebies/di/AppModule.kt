package com.khtn.freebies.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.khtn.freebies.activity.MainActivity
import com.khtn.freebies.helper.SharedPrefConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FollowingCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FollowerCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AccountSettingCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlantCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlantTypeCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PhotographyCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SpeciesCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ArticlesCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AvatarStorage

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    fun provideMainActivity(): MainActivity {
        return MainActivity()
    }
}