package com.khtn.freebies.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.khtn.freebies.helper.FireStoreCollection
import com.khtn.freebies.helper.FirebaseStorageConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseDatabaseInstance(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @FollowingCollection
    @Singleton
    @Provides
    fun provideFollowingCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.FOLLOWING)
    }

    @FollowerCollection
    @Singleton
    @Provides
    fun provideFollowerCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.FOLLOWER)
    }

    @UserCollection
    @Singleton
    @Provides
    fun provideUserCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.USER)
    }

    @AccountSettingCollection
    @Singleton
    @Provides
    fun provideAccountSettingCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.ACCOUNT_SETTING)
    }

    @PlantCollection
    @Singleton
    @Provides
    fun providePlantCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.PLANT)
    }

    @PlantTypeCollection
    @Singleton
    @Provides
    fun providePlantTypeCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.PLANT_TYPE)
    }

    @PhotographyCollection
    @Singleton
    @Provides
    fun providePhotographyCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.PHOTOGRAPHY)
    }

    @SpeciesCollection
    @Singleton
    @Provides
    fun provideSpeciesCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.SPECIES)
    }

    @ArticlesCollection
    @Singleton
    @Provides
    fun provideArticlesCollectionReference(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection(FireStoreCollection.ARTICLE)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseStorageInstance(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @AvatarStorage
    @Provides
    @Singleton
    fun provideAvatarStorageReference(storage: FirebaseStorage): StorageReference {
        return storage.getReference(FirebaseStorageConstants.AVATAR)
    }
}