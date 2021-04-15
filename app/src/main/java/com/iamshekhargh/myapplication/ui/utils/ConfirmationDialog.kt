package com.iamshekhargh.myapplication.ui.utils

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.iamshekhargh.myapplication.databinding.DialogCustomBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by <<-- iamShekharGH -->>
 * on 09 April 2021
 * at 8:30 PM.
 */
@AndroidEntryPoint
class ConfirmationDialog : DialogFragment() {

    private val viewModel: ConfirmationDialogViewModel by viewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogCustomBinding.inflate(LayoutInflater.from(requireContext()))
        binding.apply {
            dialogOk.setOnClickListener() {
                if (dialogEt.text.toString().isEmpty()) {
                    dialogTvLabelEmpty.visibility = View.VISIBLE
                } else {
                    val action =
                        ConfirmationDialogDirections.actionConfirmationDialogToFragmentAddNote(
                            dialogEt.text.toString(), null, "Edit Label"
                        )
                    findNavController().navigate(action)
                    findNavController().popBackStack()
                }
            }
            dialogCancel.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

//            .setTitle(viewModel.title)
//            .setMessage(viewModel.message)
//            .setIcon(R.mipmap.ic_launcher)
//            .setPositiveButton("OK") { _, _ ->
//                viewModel.onOKClicked("")
//            }
//            .setNegativeButton("CANCEL", null)
//        super.onCreateDialog(savedInstanceState)
//            .setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
//                dialogInterface.cancel()
//            }
    }
}