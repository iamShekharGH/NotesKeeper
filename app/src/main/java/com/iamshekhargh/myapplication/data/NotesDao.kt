package com.iamshekhargh.myapplication.data

import androidx.room.*
import com.iamshekhargh.myapplication.datastore.SortOrder
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

    @Update()
    suspend fun updateNote(n: Note)

    @Query("DELETE FROM notes_table")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM notes_table")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE current LIKE :date")
    fun getThisNote(date: Long): List<Note>

    fun getSearchResultNotes(
        query: String,
        asOrder: Boolean,
        sortOrder: SortOrder,
    ): Flow<List<Note>> {
        return when (sortOrder) {
            SortOrder.SORT_BY_NAME -> {
                when (asOrder) {
                    true -> getSearchResultNotesByNameASC(query)
                    false -> getSearchResultNotesByNameDesc(query)
                }
            }
            SortOrder.SORT_BY_DATE_CREATED -> {
                when (asOrder) {
                    true -> getSearchResultNotesByDateASC(query)
                    false -> getSearchResultNotesByDateDesc(query)
                }
            }
        }
    }

    @Query("SELECT * FROM notes_table WHERE heading LIKE '%' || :query || '%' OR description  LIKE '%' || :query || '%'  ORDER BY bookmark DESC,current ")
    fun getSearchResultNotesByDateDesc(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE heading LIKE '%' || :query || '%' OR description  LIKE '%' || :query || '%'  ORDER BY bookmark ASC,current")
    fun getSearchResultNotesByDateASC(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE heading LIKE '%' || :query || '%' OR description  LIKE '%' || :query || '%'  ORDER BY bookmark DESC, heading")
    fun getSearchResultNotesByNameDesc(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE heading LIKE '%' || :query || '%' OR description  LIKE '%' || :query || '%'  ORDER BY bookmark ASC, heading")
    fun getSearchResultNotesByNameASC(query: String): Flow<List<Note>>

//
//    @Query("SELECT * FROM notes_table WHERE heading LIKE '%' || :query || '%' OR description  LIKE '%' || :query || '%'  ORDER BY heading ASC, bookmark")
//    fun getSearchResultNotesASC(query: String): Flow<List<Note>>
//
//    @Query("SELECT * FROM notes_table WHERE heading LIKE '%' || :query || '%' OR description  LIKE '%' || :query || '%'  ORDER BY heading DESC, bookmark")
//    fun getSearchResultNotesDesc(query: String): Flow<List<Note>>


    @Query("SELECT * FROM notes_table WHERE heading LIKE '%' || :query || '%'")
    fun getFilteredList(query: String): Flow<List<Note>>


}