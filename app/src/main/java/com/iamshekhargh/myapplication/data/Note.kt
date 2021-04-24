package com.iamshekhargh.myapplication.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 8:10 PM.
 */
@Entity(tableName = "notes_table")
@Parcelize
data class Note(
    val heading: String = "Heading",
    val description: String = "description",
    val labels: List<String> = arrayListOf(),
    val bookmark: Boolean = false,
    val reminder: Long = 0,
    val id: Int = 0,
    @PrimaryKey() val current: Long = System.currentTimeMillis(),
) : Parcelable {
    val formattedDate: String get() = DateFormat.getDateTimeInstance().format(current)
    val simpleDate: String
        get() = SimpleDateFormat("E, dd MMM", Locale.ENGLISH).format(
            current
        )
}
