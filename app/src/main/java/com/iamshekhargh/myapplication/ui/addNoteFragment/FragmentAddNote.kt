package com.iamshekhargh.myapplication.ui.addNoteFragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.databinding.FragmentAddNoteBinding
import com.iamshekhargh.myapplication.ui.LabelAdapter
import com.iamshekhargh.myapplication.ui.LabelAdapterListEditingOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

/**
 * Created by <<-- iamShekharGH -->>
 * on 08 April 2021
 * at 1:03 AM.
 */
@AndroidEntryPoint
class FragmentAddNote : Fragment(R.layout.fragment_add_note), LabelAdapterListEditingOptions {

    private val viewModel: FragmentAddNoteViewModel by viewModels()
    private val labelAdapter = LabelAdapter(this)
    private val TAG = "FragmentAddNote"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "FragmentAddNote")

    //    val labelKey = intPreferencesKey("label")
    private val labelKey = stringPreferencesKey("label")

    private lateinit var dataStore: DataStore<Preferences>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreFragment()

        val binding = FragmentAddNoteBinding.bind(view)
        binding.apply {

            // When edit note clicked.
            if (viewModel.editNote != null) {
                addFragHeading.setText(viewModel.editNote?.heading)
                addFragDescription.setText(viewModel.editNote?.description)
                viewModel.editNote?.labels?.forEach { label ->
                    viewModel.labelList.add(label)
                }
                updateLabelsRV()
            }

            addFragLable.adapter = labelAdapter
            addFragLabelIvAdd.setOnClickListener {
//                Here i have to store list in shared pref or DataStore on exit to DialogFrag and then ulta.
//                viewModel.openLabelAdder(addFragLabelEtName.text.toString())
                // FIXME: 13/4/21 add datastore
                viewModel.addLabelClicked(addFragLabelEtName.text.toString())
                labelAdapter.submitList(viewModel.getTheLabelList())
                addFragLabelEtName.selectAll()
                addFragLabelEtName.text.clear()
            }

            addFragSubmit.setOnClickListener {
                viewModel.onSubmitClicked(
                    addFragHeading.text.toString(),
                    addFragDescription.text.toString()
                )
            }
        }

        setupEvents()
    }

    suspend fun writeToDatastore(label: String) {
        requireContext().dataStore.edit { file ->
            file[labelKey] = label
        }
    }

    suspend fun readFromDatastore(): String {
        var label: String
        val labelFlow: Flow<String> = requireContext().dataStore.data
            .map { preferences ->
                preferences[labelKey] ?: ""
            }
        var l: String = ""
        labelFlow.collect { value ->
            l = value
        }
        return l
    }


    private fun restoreFragment() {
        if (arguments != null) {
            val args = FragmentAddNoteArgs.fromBundle(requireArguments())

            // for edit label
            val l = args.label
            if (l != null && l.isNotEmpty()) {
                viewModel.newLabelEntered(l)
                updateLabelsRV()
            }

            // for edit note.
            val note = args.editNote
            if (note != null) {
                viewModel.loadNoteItem(note)
            }

            // to set toolbar title.
            val title = args.title
            if (title.isNotEmpty()) {
                requireActivity().title = title
            }
        }
    }

    private fun setupEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is EventsAddNote.HeadingIsEmpty -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    EventsAddNote.PopTheFragment -> {
                        findNavController().popBackStack()
                    }
                    is EventsAddNote.AddLabelToTheNote -> {
                        updateLabelsRV()
                    }
                    EventsAddNote.OpenAddLabelDialog -> {
                        val action = FragmentAddNoteDirections.actionGlobalConfirmationDialog()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun deleteLabel(position: Int) {
        val label = labelAdapter.currentList[position]
        viewModel.labelList.remove(label)
        updateLabelsRV()
    }

    override fun editLabel(position: Int, newText: String) {
        viewModel.labelList.remove(labelAdapter.currentList[position])
        viewModel.addLabelClicked(newText)
        updateLabelsRV()
    }

    private fun updateLabelsRV() {
        labelAdapter.submitList(viewModel.getTheLabelList())
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
