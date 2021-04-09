package com.iamshekhargh.myapplication.ui.mainFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 7:50 PM.
 */
@AndroidEntryPoint
class FragmentMain : Fragment(R.layout.fragment_main) {

    private val viewModel: FragmentMainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)
        val notesAdapter = NotesAdapter()

        binding.apply {
            fragMainRv.adapter = notesAdapter
            fragMainRv.layoutManager = LinearLayoutManager(requireContext())
            fragMainRv.setHasFixedSize(true)

            fragMainFab.setOnClickListener {
                viewModel.fabClicked()
            }

        }

        viewModel.notesList.observe(viewLifecycleOwner) { notes ->
            notesAdapter.submitList(notes)
        }
        setupEventHandling()
    }

    private fun setupEventHandling() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    EventHandler.OpenAddNoteFragment -> {
                        val action = FragmentMainDirections.actionFragmentMainToFragmentAddNote()
                        findNavController().navigate(action)

                    }
                    EventHandler.OpenAddNotesFrag -> TODO()
                }

            }
        }
    }
}