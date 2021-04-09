package com.iamshekhargh.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 8:17 PM.
 */
@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(n: Note)

    @Delete
    suspend fun deleteNote(n: Note)

    @Update
    suspend fun updateNote(n: Note)

    @Query("SELECT * FROM notes_table")
    fun getAllNotes(): Flow<List<Note>>
}