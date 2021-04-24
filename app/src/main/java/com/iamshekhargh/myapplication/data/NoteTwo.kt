package com.iamshekhargh.myapplication.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by <<-- iamShekharGH -->>
 * on 09 April 2021
 * at 7:52 PM.
 */
@Entity(tableName = "second_notes_table")
@Parcelize
class NoteTwo(
    val heading: String,
    val description: String,
    val bookmark: Boolean = false,
    val reminder: Long = 0,
    val labels: List<String> = ArrayList<String>(),
    val current: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) : Parcelable {
    val timeInNormal: String get() = DateFormat.getDateTimeInstance().format(current)
    val simpleDate: String
        get() = SimpleDateFormat("E, dd MMM", Locale.ENGLISH).format(
            current
        )
}