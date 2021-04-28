package com.iamshekhargh.myapplication.ui.addNoteFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
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
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <<-- iamShekharGH -->>
 * on 08 April 2021
 * at 1:03 AM.
 */
@AndroidEntryPoint
class FragmentAddNote : Fragment(R.layout.fragment_add_note), LabelAdapterListEditingOptions {

    private val viewModel: FragmentAddNoteViewModel by viewModels()
    private val labelAdapter = LabelAdapter(this)
    lateinit var binding: FragmentAddNoteBinding
    private val TAG = "FragmentAddNote"

    lateinit var datePickerDialog: DatePickerDialog
    lateinit var timePickerDialog: TimePickerDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillFragmentWithDetails()

        binding = FragmentAddNoteBinding.bind(view)
        binding.apply {

            // When edit note clicked.
            if (viewModel.editNote != null) {
                addFragHeading.setText(viewModel.editNote?.heading)
                addFragDescription.setText(viewModel.editNote?.description)
                viewModel.editNote?.labels?.forEach { label ->
                    viewModel.labelList.add(label)
                }
                addFragBookmark.isChecked = viewModel.editNote?.bookmark ?: false
                updateLabelsInRV()
            }

            // by default so no alarms trigger if nothing is selected.
            viewModel.cal.set(Calendar.YEAR, 1990)
            addFragLable.adapter = labelAdapter

            addFragAddReminder.setOnClickListener {
                showDateTimePicker()
            }

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
                    addFragDescription.text.toString(),
                    addFragBookmark.isChecked
                )
            }
        }

        setupEvents()
    }

    private fun showDateTimePicker() {
        // This c is for setting a limit to reminders.
        val c = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->

                timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->

                        viewModel.cal.set(Calendar.YEAR, year)
                        viewModel.cal.set(Calendar.MONTH, monthOfYear)
                        viewModel.cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        viewModel.cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        viewModel.cal.set(Calendar.MINUTE, minute)

                        binding.addFragAddReminderText.text =
                            SimpleDateFormat("E, dd MMM, hh:mm a", Locale.ENGLISH).format(
                                viewModel.cal.timeInMillis
                            )
                    },
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.show()
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DATE)
        )
        datePickerDialog.datePicker.minDate = (c.timeInMillis - 1000)
        datePickerDialog.show()
    }


    private fun fillFragmentWithDetails() {
        if (arguments != null) {
            val args = FragmentAddNoteArgs.fromBundle(requireArguments())

            // for edit label
            val l = args.label
            if (l != null && l.isNotEmpty()) {
                viewModel.newLabelEntered(l)
                updateLabelsInRV()
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
                        updateLabelsInRV()
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
        updateLabelsInRV()
    }

    override fun editLabel(position: Int, newText: String) {
        viewModel.labelList.remove(labelAdapter.currentList[position])
        viewModel.addLabelClicked(newText)
        updateLabelsInRV()
    }

    private fun updateLabelsInRV() {
        labelAdapter.submitList(viewModel.getTheLabelList())
    }


}
