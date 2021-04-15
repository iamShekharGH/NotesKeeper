package com.iamshekhargh.myapplication.ui.mainFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Created by <<-- iamShekharGH -->>
 * on 07 April 2021
 * at 7:50 PM.
 */
@AndroidEntryPoint
class FragmentMain : Fragment(R.layout.fragment_main), NotesAdapter.OnNoteClicked {

    private val viewModel: FragmentMainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)
        val notesAdapter = NotesAdapter(this)

        binding.apply {
            fragMainRv.adapter = notesAdapter
            val flexboxLayoutManager = FlexboxLayoutManager(requireContext())
            flexboxLayoutManager.flexDirection = FlexDirection.ROW
            flexboxLayoutManager.justifyContent = JustifyContent.FLEX_END
            fragMainRv.layoutManager = flexboxLayoutManager

//            fragMainRv.setHasFixedSize(true)

            fragMainFab.setOnClickListener {
                viewModel.fabClicked()
            }

            fragMainFabTestingViews.setOnClickListener { viewModel.testFabClicked() }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val note = notesAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.itemSwiped(note)
                }

            }).attachToRecyclerView(fragMainRv)

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
                        val action =
                            FragmentMainDirections.actionFragmentMainToFragmentAddNote(
                                null,
                                null,
                                "New Note"
                            )
                        findNavController().navigate(action)
                    }
                    EventHandler.OpenTestingFragment -> {
                        val action = FragmentMainDirections.actionGlobalConfirmationDialog()
                        findNavController().navigate(action)
                    }
                    is EventHandler.OpenEditNoteFragment -> {
                        val action =
                            FragmentMainDirections.actionFragmentMainToFragmentAddNote(
                                null,
                                event.note,
                                "Edit Note"
                            )
                        findNavController().navigate(action)

                    }
                }

            }
        }
    }

    override fun noteItemClicked(n: Note) {
        viewModel.noteItemClicked(n)
    }
}