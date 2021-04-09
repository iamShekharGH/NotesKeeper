package com.iamshekhargh.myapplication.ui.addNoteFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.databinding.FragmentAddNoteBinding
import com.iamshekhargh.myapplication.ui.mainFragment.LabelAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * Created by <<-- iamShekharGH -->>
 * on 08 April 2021
 * at 1:03 AM.
 */
@AndroidEntryPoint
class FragmentAddNote : Fragment(R.layout.fragment_add_note) {

    private val viewModel: FragmentAddNoteViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddNoteBinding.bind(view)
        binding.apply {

            val labelAdapter = LabelAdapter()
            addFragLable.adapter = labelAdapter
            addFragLable.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            val list = ArrayList<String>()
            list.add("Label 1")
            list.add("Add  2")
            list.add("Add")
            list.add("4")
            list.add("Label 5")
            list.add("6")
            list.add("Label 7")
            list.add("Add")
            list.add("Add Label")
            labelAdapter.submitList(list)



            addFragSubmit.setOnClickListener {
                var note = Note(
                    heading = addFragHeading.text.toString(),
                    description = addFragDescription.text.toString(),
                    labels = list
                )

                viewModel.onSubmitClicked(note)
            }
        }
        setupEvents()
    }

    fun setupEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is EventsAddNote.HeadingIsEmpty -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    EventsAddNote.PopTheFragment -> {
                        findNavController().popBackStack()
                    }
                }

            }
        }
    }
}