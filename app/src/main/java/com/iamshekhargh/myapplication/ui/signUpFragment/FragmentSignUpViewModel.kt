package com.iamshekhargh.myapplication.ui.signUpFragment

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
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

    var fragType: FirstFragArgs = FirstFragArgs.SIGN_UP

    lateinit var googleSignInOptions: GoogleSignInOptions

    lateinit var googleSignInClient: GoogleSignInClient

    fun initialiseFirebaseAuth() {
        this.auth = Firebase.auth
    }

    fun initialiseGoogleSignInOptions(token: String) {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    }

    fun initialiseGoogleSignInClient(c: Context) {
        googleSignInClient = GoogleSignIn.getClient(c, googleSignInOptions)

    }

    private fun startSignUp(email: String, password: String) {
        if (auth.currentUser == null) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            Log.i(TAG, "startSignUp: sign up successful.")
                            user = auth.currentUser
                            showSnackbar("Sign Up ho gaya.")
                            loginSuccessful()

                        }
                        task.exception is FirebaseAuthUserCollisionException -> {
                            Log.i(
                                TAG,
                                "startSignUp: LoginFailed ${task.exception} now trying to sign in"
                            )
                            showSnackbar("User already exists, trying to Sign in instead. You forgot :'( ")
//                            showSignInErrorMessage("User already exists, trying to Sign in instead. You forgot :'( ")
                            startSignIn(email, password)
                        }
                        else -> {
                            //                        FirebaseAuthUserCollisionException
                            task.exception?.printStackTrace()
                            //                        Log.i(TAG, "startSignUp: LoginFailed ${task.exception}")
                            showSnackbar(task.exception?.message + " <- iss vaja se")
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
                    showSnackbar("Login Successful!")
                    loginSuccessful()
                }
                task.exception is FirebaseAuthInvalidCredentialsException -> {
//                    loginFailed("Password is wrong, IDIOT!")
                    showSignInErrorWrongPassword("Password is wrong, IDIOT!")
                }
                else -> {
                    task.exception?.printStackTrace()
                    showSnackbar(task.exception?.message + " <- iss vaja se")
                }
            }
        }
    }

    private fun showSnackbar(e: String) = viewModelScope.launch {
        eventsChannel.send(SignUpEvents.ShowSnackBar(e))
    }

    private fun showSignInErrorWrongEmail(text: String) = viewModelScope.launch {
        eventsChannel.send(SignUpEvents.ShowEmailEditTextError(text))
    }

    private fun showSignInErrorWrongPassword(text: String) = viewModelScope.launch {
        eventsChannel.send(SignUpEvents.ShowPasswordEditTextError(text))
    }

    private fun loginSuccessful() = viewModelScope.launch {
        eventsChannel.send(SignUpEvents.LoginSuccessful)

    }

    fun loginClicked(email: String, password: String, fragType: FirstFragArgs) =
        viewModelScope.launch {
            eventsChannel.send(SignUpEvents.ShowProgressBar(orNot = true))
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

    fun firebaseAuthWithGoogle(idToken: String?) {
        val credentials = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    showSnackbar("Google Login Successful!!")
                    loginSuccessful()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showSnackbar("Login Failed!! Kuch ni hoga tumse..")
                    task.exception?.printStackTrace()
                }
            }
    }

    fun progressBar(orNot: Boolean) = viewModelScope.launch {
        eventsChannel.send(SignUpEvents.ShowProgressBar(orNot))
    }

    companion object {
        private const val TAG = "FragmentSignUpViewModel"
    }
}

sealed class SignUpEvents {
    data class ShowSnackBar(val text: String) : SignUpEvents()
    data class ShowEmailEditTextError(val text: String) : SignUpEvents()
    data class ShowPasswordEditTextError(val text: String) : SignUpEvents()
    object LoginSuccessful : SignUpEvents()
    data class ShowProgressBar(val orNot: Boolean) : SignUpEvents()
}