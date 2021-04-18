package com.iamshekhargh.myapplication.utils

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