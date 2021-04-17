package com.iamshekhargh.myapplication.ui.signUpFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.utils.FirstFragArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 15 April 2021
 * at 7:43 PM.
 */
@HiltViewModel
class FragmentSignUpViewModel @Inject constructor() : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private var user: FirebaseUser? = null

    private val eventsChannel = Channel<SignUpEvents>()
    val eventsAsFlow = eventsChannel.receiveAsFlow()

    fun initialiseFirebaseAuth() {
        this.auth = Firebase.auth
    }

    private fun startSignUp(email: String, password: String) {
        if (auth.currentUser == null) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            Log.i(TAG, "startSignUp: sign up successful.")
                            user = auth.currentUser
                        }
                        task.exception is FirebaseAuthUserCollisionException -> {
                            Log.i(
                                TAG,
                                "startSignUp: LoginFailed ${task.exception} now trying to sign in"
                            )
                            showSignInErrorMessage("User already exists, trying to Sign in instead. You forgot :'( ")
                            startSignIn(email, password)
                        }
                        else -> {
                            //                        FirebaseAuthUserCollisionException
                            task.exception?.printStackTrace()
                            //                        Log.i(TAG, "startSignUp: LoginFailed ${task.exception}")
                            loginFailed(task.exception?.message + " <- iss vaja se")
                        }
                    }
                }
        }
    }

    private fun startSignIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    user = auth.currentUser
                }
                task.exception is FirebaseAuthInvalidCredentialsException -> {
                    loginFailed("Password is wrong, IDIOT!")
                }
                else -> {
                    task.exception?.printStackTrace()
                    loginFailed(task.exception?.message + " <- iss vaja se")
                }
            }
        }
    }

    private fun loginFailed(e: String) = viewModelScope.launch {
        eventsChannel.send(SignUpEvents.ShowSnackBar(e))
    }

    private fun showSignInErrorMessage(text: String) = viewModelScope.launch {
        eventsChannel.send(SignUpEvents.ShowEmailEditTextError(text))
    }

    fun loginClicked(email: String, password: String, fragType: FirstFragArgs) =
        viewModelScope.launch {
            if (email.isNotEmpty() && password.isNotEmpty()) {

                if (fragType == FirstFragArgs.SIGN_UP)
                    startSignUp(email, password)

                if (fragType == FirstFragArgs.SIGN_IN)
                    startSignIn(email, password)

            } else if (email.isEmpty()) {
                eventsChannel.send(SignUpEvents.ShowSnackBar("Email cannot be empty. duh!"))
            } else {
                eventsChannel.send(SignUpEvents.ShowSnackBar("Password absent, make it present."))
            }
        }

    companion object {
        private const val TAG = "FragmentSignUpViewModel"
    }
}

sealed class SignUpEvents {
    class ShowSnackBar(val text: String) : SignUpEvents()
    class ShowEmailEditTextError(val text: String) : SignUpEvents()
}