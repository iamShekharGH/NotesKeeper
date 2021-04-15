package com.iamshekhargh.myapplication.ui.utils

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 09 April 2021
 * at 8:32 PM.
 */
@HiltViewModel
class ConfirmationDialogViewModel @Inject constructor() : ViewModel() {

    var title: String = "Name"

    var message: String = "Description"

    fun onOKClicked(text: String) {
        title = text
    }

    fun onCancelClicked() {
        title = "Canceled"
    }


}