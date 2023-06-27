package com.khtn.freebies.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
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
        auth: FirebaseAuth,
        @UserCollection userCollection: CollectionReference,
        @AccountSettingCollection accountSettingCollection: CollectionReference,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepo {
        return AuthRepoImp(
            auth, userCollection,
            accountSettingCollection,
            appPreferences, gson
        )
    }

    @Provides
    @Singleton
    fun providePlantTypeRepository(
        @PlantTypeCollection plantTypeCollection: CollectionReference,
        @PhotographyCollection photographyCollection: CollectionReference
    ): PlantTypeRepo {
        return PlantTypeRepoImp(plantTypeCollection, photographyCollection)
    }

    @Provides
    @Singleton
    fun provideSpeciesRepository(
        @SpeciesCollection speciesCollection: CollectionReference
    ): SpeciesRepo {
        return SpeciesRepoImp(speciesCollection)
    }

    @Provides
    @Singleton
    fun providePlantsRepository(
        @PlantCollection plantCollection: CollectionReference,
        @FollowerCollection followerCollection: CollectionReference,
        @FollowingCollection followingCollection: CollectionReference,
        appPreferences: SharedPreferences,
        gson: Gson
    ): PlantRepo {
        return PlantRepoImp(
            plantCollection, followerCollection,
            followingCollection, appPreferences, gson
        )
    }

    @Provides
    @Singleton
    fun provideArticlesRepository(
        @ArticlesCollection articlesCollection: CollectionReference,
        @UserCollection userCollection: CollectionReference,
        @FollowingCollection followingCollection: CollectionReference,
        @FollowerCollection followerCollection: CollectionReference,
        appPreferences: SharedPreferences,
        gson: Gson
    ): ArticlesRepo {
        return ArticlesRepoImp(
            articlesCollection, userCollection,
            followingCollection, followerCollection,
            appPreferences, gson
        )
    }
}