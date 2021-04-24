package com.iamshekhargh.myapplication.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.data.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 7:51 PM.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val FIRE_STORE_COLLECTION_NAME = "testNote"

    @Provides
    @Singleton
    fun provideNotesDatabase(app: Application) =
        Room.databaseBuilder(app, NotesDatabase::class.java, "notes_table")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideNotesDao(database: NotesDatabase) = database.getNotesDao()

    @Provides
    fun provideFirebaseAuthObj() = Firebase.auth

    @Provides
    fun getFirebaseUser(auth: FirebaseAuth): FirebaseUser? = auth.currentUser

    @Provides
    fun provideFirebaseDB(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun provideCollectionReference(): CollectionReference =
        Firebase.firestore.collection(FIRE_STORE_COLLECTION_NAME)

    @Provides
    @Singleton
    fun provideCoroutineScope() = CoroutineScope(SupervisorJob())


}