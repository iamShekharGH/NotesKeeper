package com.iamshekhargh.myapplication.ui.signUpFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.databinding.FragmentSignupBinding
import com.iamshekhargh.myapplication.utils.FirstFragArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Created by <<-- iamShekharGH -->>
 * on 15 April 2021
 * at 7:42 PM.
 */
@AndroidEntryPoint
class FragmentSignUp : Fragment(R.layout.fragment_signup) {

    private val viewModel: FragmentSignUpViewModel by viewModels()
    private lateinit var binding: FragmentSignupBinding
    var fragType: FirstFragArgs = FirstFragArgs.SIGN_UP

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val arg = FragmentSignUpArgs.fromBundle(requireArguments())
            fragType = arg.whereTo
        }

        viewModel.initialiseFirebaseAuth()

        binding = FragmentSignupBinding.bind(view)
        binding.apply {
            signUpSubmit.setOnClickListener {
                viewModel.loginClicked(
                    signUpEmail.text.toString(),
                    signUpPassword.text.toString(),
                    fragType
                )
            }
        }

        setupEvents()


    }

    private fun showErrorOnEmailEditText(text: String) {
        binding.apply {
            signUpEmail.error = text
            signUpEmail.requestFocus()
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
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//        }
    }
}