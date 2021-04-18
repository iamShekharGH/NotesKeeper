package com.iamshekhargh.myapplication.ui.profileFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Created by <<-- iamShekharGH -->>
 * on 17 April 2021
 * at 4:59 PM.
 */
@AndroidEntryPoint
class FragmentProfile : Fragment(R.layout.fragment_profile) {

    private val viewModel: FragmentProfileViewModel by viewModels()
    lateinit var binding: FragmentProfileBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initialiseFirebaseUser()

        binding = FragmentProfileBinding.bind(view)
        binding.apply {
            if (viewModel.user != null) {
                // TODO update information in fb
            } else {
                // TODO ask for information.
            }

            profileFab.setOnClickListener {
                viewModel.fabClicked(
                    profileDetailsName.text.toString(),
                    profileDetailsEmail.text.toString(),
                    profileDetailsPhotoUrl.text.toString()
                )
            }
        }

        setupUIEvents()
    }

    private fun showNameError(message: String) {
        binding.apply {
            profileDetailsName.error = message
            profileDetailsName.requestFocus()
        }
    }

    private fun showEmailError(message: String) {
        binding.apply {
            profileDetailsEmail.error = message
            profileDetailsEmail.requestFocus()
        }
    }

    private fun showPhotoUrlError(message: String) {
        binding.apply {
            profileDetailsPhotoUrl.error = message
            profileDetailsPhotoUrl.requestFocus()
        }

    }


    private fun setupUIEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.flowEvents.collect { event ->
                when (event) {
                    is ProfileEvents.ShowEmailError -> {
                        showEmailError(event.text)
                    }
                    is ProfileEvents.ShowNameError -> {
                        showNameError(event.text)
                    }
                    is ProfileEvents.ShowPhotoError -> {
                        showPhotoUrlError(event.text)
                    }
                    is ProfileEvents.ShowSnackBar -> {
                        Snackbar.make(requireView(), event.text, Snackbar.LENGTH_LONG).show()
                    }
                    ProfileEvents.OpenLoginFrag -> {

                    }
                }

            }

        }
    }
}