package com.iamshekhargh.myapplication.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 8:10 PM.
 */
@Entity(tableName = "notes_table")
@Parcelize
data class Note(
    val heading: String,
    val description: String,
    val labels: List<String>,
    val bookmark: Boolean = false,
    val current: Long = System.currentTimeMillis(),
    val reminder: Long = 0,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) : Parcelable {
    val formattedDate: String get() = DateFormat.getDateTimeInstance().format(current)
}
