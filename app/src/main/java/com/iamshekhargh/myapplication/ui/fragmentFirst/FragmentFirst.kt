package com.iamshekhargh.myapplication.ui.fragmentFirst

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.databinding.FragmentFirstBinding
import com.iamshekhargh.myapplication.utils.FirstFragArgs
import com.iamshekhargh.myapplication.utils.logi
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
    lateinit var timer: CountDownTimer
    var timerStatus = false
    private val TAG = "FragmentFirst"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFirstBinding.bind(view)

        checkUser()

        binding.apply {
            firstfragSignin.setOnClickListener { viewModel.signInClicked() }
            firstfragSignup.setOnClickListener { viewModel.signUpClicked() }
            firstfragSkip.setOnClickListener { viewModel.skipClicked() }
        }
        setupEvents()
        requireActivity().actionBar?.setIcon(R.mipmap.ic_launcher_round)
    }

    private fun showLoginTimer() {

        timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerStatus = true
                binding.firstfragTimer.text =
                    "Logging-in in " + (millisUntilFinished / 1000).toString() + " seconds."
            }

            override fun onFinish() {
                timerStatus = false
                viewModel.skipClicked()
            }
        }
        timer.start()

        binding.apply {
            firstfragTimer.visibility = View.VISIBLE
            firstfragTimerStop.visibility = View.VISIBLE
            firstfragTimerStop.setOnClickListener {
                timer.cancel()
            }
        }
    }

    private fun checkUser() {
        if (Firebase.auth.currentUser == null) {
            viewModel.user = null
        }
        if (viewModel.user != null) {
            showLoginTimer()
            logi(TAG, viewModel.user!!.uid.toString())
        } else {
            binding.apply {
                firstfragTimer.visibility = View.GONE
                firstfragTimerStop.visibility = View.GONE
            }
        }
    }

    private fun setTheTitle(title: String) {
        findNavController().currentDestination?.label = title
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