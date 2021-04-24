package com.iamshekhargh.myapplication.ui

import androidx.lifecycle.ViewModel
import com.iamshekhargh.myapplication.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 23 April 2021
 * at 12:31 PM.
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {


    fun notesSyncFunctions() {
        repository.fetchFireStoreList()
    }
}