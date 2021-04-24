package com.iamshekhargh.myapplication.ui.fragmentFirst

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 16 April 2021
 * at 6:17 PM.
 */
@HiltViewModel
class FragmentFirstViewModel @Inject constructor() : ViewModel() {

    var user: FirebaseUser? = Firebase.auth.currentUser

    private val eventChannel = Channel<FirstFragEvent>()
    val eventsAsFlow = eventChannel.receiveAsFlow()

    fun signInClicked() = viewModelScope.launch {
        eventChannel.send(FirstFragEvent.SignInFragment)
    }

    fun signUpClicked() = viewModelScope.launch {
        eventChannel.send(FirstFragEvent.SignUpFragment)
    }

    fun skipClicked() = viewModelScope.launch {
        eventChannel.send(FirstFragEvent.SkipFragment)
    }

    override fun onCleared() {
        user = null
        super.onCleared()
    }

}

sealed class FirstFragEvent {
    object SignInFragment : FirstFragEvent()
    object SignUpFragment : FirstFragEvent()
    object SkipFragment : FirstFragEvent()
}