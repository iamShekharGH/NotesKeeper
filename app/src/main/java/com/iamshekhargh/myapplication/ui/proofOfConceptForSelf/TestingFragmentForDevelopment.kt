package com.iamshekhargh.myapplication.ui.proofOfConceptForSelf

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.databinding.FragmentTempEtcBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by <<-- iamShekharGH -->>
 * on 20 April 2021
 * at 6:24 PM.
 */
@AndroidEntryPoint
class TestingFragmentForDevelopment : Fragment(R.layout.fragment_temp_etc) {
    private val TAG = "FirebaseFragment"

    lateinit var binding: FragmentTempEtcBinding
    var db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTempEtcBinding.bind(view)

        binding.apply {
            val user = hashMapOf(
                "heading" to heading.text.toString(),
                "description" to description.text.toString(),
                "labels" to arrayListOf<String>(),
                "bookmark" to
                        "currentTime" to System.currentTimeMillis(),
                "reminder" to reminderTime.text.toString()
            )

            submit.setOnClickListener {
                if (heading.text.isNotEmpty()) {
                    val ll = arrayListOf<String>(
                        label1.text.toString(),
                        label2.text.toString(),
                        label1.text.toString(),
                        label2.text.toString(),
                        label1.text.toString(),
                        label2.text.toString(),
                        label1.text.toString(),
                        label2.text.toString(),

                        )
                    val note = Note(
                        heading = heading.text.toString(),
                        description = description.text.toString(),
                        labels = ll,
                        bookmark = bookmark.isChecked,
                    )
                    doTheDB(note)

                }
            }

        }

    }

    private fun popTheFrag() {
        findNavController().popBackStack()
    }

    private fun doTheDB(note: Note?) {
        if (note == null) {
            val aa = arrayListOf<String>(
                "name",
                "pata",
                "nai pata",
                "asdfasdf",
                "safavgfsda",
                "safdaswfsdvc"
            )
            db.collection("testNote")
                .add(Note("this", "is", aa, true))
                .addOnSuccessListener { documentReference ->
                    Snackbar.make(
                        requireView(),
                        "ID :" + documentReference.id,
                        Snackbar.LENGTH_LONG
                    ).show()
                    popTheFrag()
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "doTheDB: " + e.message)
                    e.printStackTrace()
                }
        } else {
            db.collection("testNote")
                .document(note.current.toString())
                .set(note)
                .addOnSuccessListener { documentReference ->
                    Snackbar.make(requireView(), "ID :$documentReference", Snackbar.LENGTH_LONG)
                        .show()

                    popTheFrag()
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "doTheDB: " + e.message)
                    e.printStackTrace()
                }
        }
    }
}