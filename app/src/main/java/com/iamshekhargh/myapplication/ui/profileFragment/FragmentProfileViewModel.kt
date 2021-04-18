package com.iamshekhargh.myapplication.ui.profileFragment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 17 April 2021
 * at 5:02 PM.
 */
@HiltViewModel
class FragmentProfileViewModel @Inject constructor() : ViewModel() {

    private lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null

    private val channelEvents = Channel<ProfileEvents>()
    val flowEvents = channelEvents.receiveAsFlow()

    fun initialiseFirebaseUser() {
        auth = Firebase.auth
        user = auth.currentUser
    }

    fun getUserInformation() {}

    fun updateUserProfile() {}

    private fun setUserEmail(email: String) {
        if (email.isNotEmpty()) {
            user?.updateEmail(email)?.addOnCompleteListener {

            }
        } else {
            showEmailError("Put email id in this box, God!!")
        }
    }

    fun setUserPassword(password: String) {}

    fun sendPasswordResetEmail() {}

    fun sendUserVerificationEmail() {
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showSnackBar("Verification Email Sent")
            } else {
                showSnackBar("Verification Email Not Sent. What did you do now?")
            }

        }
    }

    fun deleteUser() {}

    fun fabClicked(name: String, email: String, photoURL: String) {
        if (user != null) {
            if (name.isNotEmpty() && photoURL.isNotEmpty()) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                    photoUri = Uri.parse(photoURL)
                }
                user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showSnackBar("Profile updated.")
                    } else {
                        task.exception?.message?.let { showSnackBar(it) }
                        task.exception?.printStackTrace()
                    }
                }
            } else if (name.isEmpty()) {
                showNameError("Name you should have!!")
            } else {
                showPhotoError("Put photo url Idiot!!")
            }

            setUserEmail(email)

        } else {
            showSnackBar("Login You Moron!")
            openLoginFrag()
        }
    }

    private fun openLoginFrag() {

    }

    private fun showSnackBar(text: String) = viewModelScope.launch {
        channelEvents.send(ProfileEvents.ShowSnackBar(text))
    }

    private fun showNameError(text: String) = viewModelScope.launch {
        channelEvents.send(ProfileEvents.ShowNameError(text))
    }

    private fun showEmailError(text: String) = viewModelScope.launch {
        channelEvents.send(ProfileEvents.ShowEmailError(text))
    }

    private fun showPhotoError(text: String) = viewModelScope.launch {
        channelEvents.send(ProfileEvents.ShowPhotoError(text))
    }
}


sealed class ProfileEvents {
    data class ShowSnackBar(val text: String) : ProfileEvents()
    data class ShowNameError(val text: String) : ProfileEvents()
    data class ShowEmailError(val text: String) : ProfileEvents()
    data class ShowPhotoError(val text: String) : ProfileEvents()
    object OpenLoginFrag : ProfileEvents()

}