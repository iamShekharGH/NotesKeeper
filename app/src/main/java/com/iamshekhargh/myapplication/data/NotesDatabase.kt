package com.iamshekhargh.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 8:20 PM.
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(MyTypeConverter::class)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun getNotesDao(): NotesDao
}