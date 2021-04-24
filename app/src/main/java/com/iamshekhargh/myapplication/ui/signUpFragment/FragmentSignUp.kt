package com.iamshekhargh.myapplication.ui.signUpFragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * Created by <<-- iamShekharGH -->>
 * on 15 April 2021
 * at 7:42 PM.
 */
@AndroidEntryPoint
class FragmentSignUp @Inject constructor() : Fragment(R.layout.fragment_signup) {

    private val viewModel: FragmentSignUpViewModel by viewModels()
    private lateinit var binding: FragmentSignupBinding

    companion object {
        //        private const val RC_SIGN_IN = 9001
        private const val TAG = "FragmentSignUp"
    }

    private val resultContracts = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                Log.w(TAG, "--------- Google Sign In Failed : : ", e)
                e.printStackTrace()
            }
        } else {
            Snackbar.make(requireView(), "Login Cancelled by User", Snackbar.LENGTH_LONG).show()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val arg = FragmentSignUpArgs.fromBundle(requireArguments())
            viewModel.fragType = arg.whereTo
        }

        viewModel.initialiseFirebaseAuth()
        viewModel.initialiseGoogleSignInOptions(getString(R.string.default_web_client_id))
        viewModel.initialiseGoogleSignInClient(requireContext())

        binding = FragmentSignupBinding.bind(view)
        binding.apply {
            signUpSubmit.setOnClickListener {
                viewModel.loginClicked(
                    signUpEmail.text.toString(),
                    signUpPassword.text.toString(),
                    viewModel.fragType
                )
            }
            googleSignInButton.setOnClickListener {
                startGoogleSignIn()
            }
        }

        setupEvents()

    }


    private fun startGoogleSignIn() {
        viewModel.startGoogleSignIn()
        val signInIntent = viewModel.googleSignInClient.signInIntent
        resultContracts.launch(signInIntent)
    }

    private fun showErrorOnEmailEditText(text: String) {
        binding.apply {
            signUpEmail.error = text
            signUpEmail.requestFocus()
        }
    }

    private fun showErrorOnPasswordEditText(text: String) {
        binding.apply {
            signUpPassword.error = text
            signUpPassword.requestFocus()
        }
    }

    private fun setupEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.eventsAsFlow.collect { event ->
                when (event) {
                    is SignUpEvents.ShowSnackBar -> {
                        Snackbar.make(requireView(), event.text, Snackbar.LENGTH_SHORT).show()
                    }
                    is SignUpEvents.ShowEmailEditTextError -> {
                        showErrorOnEmailEditText(event.text)
                    }
                    is SignUpEvents.ShowPasswordEditTextError -> {
                        showErrorOnPasswordEditText(event.text)
                    }
                    SignUpEvents.LoginSuccessful -> {
                        val action = FragmentSignUpDirections.actionFragmentSignUpToFragmentMain()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }
}