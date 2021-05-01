package com.iamshekhargh.myapplication.utils

import android.util.Log
import androidx.appcompat.widget.SearchView

/**
 * Created by <<-- iamShekharGH -->>
 * on 17 April 2021
 * at 3:25 PM.
 */

inline fun SearchView.onTextEntered(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = true

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

const val PRODUCTION = false
fun logi(tag: String, text: String) {
    if (!PRODUCTION) {
        Log.i(tag, text)
    }
}

sealed class ChannelEvents {
    data class ShowProgressBar(val show: Boolean) : ChannelEvents()
    data class ShowNetworkMessage(val message: String) : ChannelEvents()
    data class DeleteTaskCompleted(val completed: Boolean) : ChannelEvents()
}

enum class NotificationKeys {
    HEADING, DESCRIPTION, TIME, NOTE_OBJECT
}