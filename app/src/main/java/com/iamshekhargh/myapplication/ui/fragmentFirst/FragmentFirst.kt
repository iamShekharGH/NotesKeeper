package com.iamshekhargh.myapplication.ui.fragmentFirst

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.databinding.FragmentFirstBinding
import com.iamshekhargh.myapplication.utils.FirstFragArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Created by <<-- iamShekharGH -->>
 * on 16 April 2021
 * at 6:16 PM.
 */
@AndroidEntryPoint
class FragmentFirst : Fragment(R.layout.fragment_first) {

    private val viewModel: FragmentFirstViewModel by viewModels()
    private lateinit var binding: FragmentFirstBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUser()

        binding = FragmentFirstBinding.bind(view)

        binding.apply {
            firstfragSignin.setOnClickListener { viewModel.signInClicked() }
            firstfragSignup.setOnClickListener { viewModel.signUpClicked() }
            firstfragSkip.setOnClickListener { viewModel.skipClicked() }
        }

        setupEvents()
    }

    private fun checkUser() {
        if (viewModel.user == null) {
            viewModel.signInClicked()
        } else {
            viewModel.skipClicked()
        }
    }

    private fun setupEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsAsFlow.collect { event ->
                when (event) {
                    FirstFragEvent.SignInFragment -> {
                        val action =
                            FragmentFirstDirections.actionFragmentFirstToFragmentSignUp(
                                FirstFragArgs.SIGN_IN
                            )
                        findNavController().navigate(action)
                    }
                    FirstFragEvent.SignUpFragment -> {
                        val action = FragmentFirstDirections.actionFragmentFirstToFragmentSignUp(
                            FirstFragArgs.SIGN_UP
                        )
                        findNavController().navigate(action)
                    }
                    FirstFragEvent.SkipFragment -> {
                        val action =
                            FragmentFirstDirections.actionFragmentFirstToFragmentMain()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }


}