package com.iamshekhargh.myapplication.di

import android.app.Application
import androidx.room.Room
import com.iamshekhargh.myapplication.data.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 7:51 PM.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotesDatabase(app: Application) =
        Room.databaseBuilder(app, NotesDatabase::class.java, "notes_table")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideNotesDao(database: NotesDatabase) = database.getNotesDao()


}