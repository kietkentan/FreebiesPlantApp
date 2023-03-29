package com.khtn.freebies.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.khtn.freebies.repo.AuthRepo
import com.khtn.freebies.repo.AuthRepoImp
import com.khtn.freebies.repo.PlantTypeRepo
import com.khtn.freebies.repo.PlantTypeRepoImp
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
}