package com.khtn.freebies.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.khtn.freebies.repo.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepoModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepo {
        return AuthRepoImp(auth,database,appPreferences,gson)
    }

    @Provides
    @Singleton
    fun providePlantTypeRepository(
        database: FirebaseFirestore,
    ): PlantTypeRepo {
        return PlantTypeRepoImp(database)
    }

    @Provides
    @Singleton
    fun provideAccountSettingRepository(
        database: FirebaseFirestore,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AccountSettingRepo {
        return AccountSettingRepoImp(database, appPreferences, gson)
    }

    @Provides
    @Singleton
    fun provideSpeciesRepository(
        database: FirebaseFirestore
    ): SpeciesRepo {
        return SpeciesRepoImp(database)
    }

    @Provides
    @Singleton
    fun providePlantsRepository(
        database: FirebaseDatabase,
        reference: StorageReference
    ): PlantRepo {
        return PlantRepoImp(database, reference)
    }
}