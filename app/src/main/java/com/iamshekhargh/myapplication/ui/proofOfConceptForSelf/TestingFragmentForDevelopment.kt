package com.iamshekhargh.myapplication.ui.proofOfConceptForSelf

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.broadcasts.MyAlarmBroadcastReceiver
import com.iamshekhargh.myapplication.data.Note
import com.iamshekhargh.myapplication.databinding.FragmentTempEtcBinding
import com.iamshekhargh.myapplication.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Created by <<-- iamShekharGH -->>
 * on 20 April 2021
 * at 6:24 PM.
 */

private const val MY_REQUEST_CODE = 11111
private const val CHANNEL_ID = "this.is.the.survivial.of.the.fittest"
private const val TAG = "FirebaseFragment"

@AndroidEntryPoint
class TestingFragmentForDevelopment : Fragment(R.layout.fragment_temp_etc) {

    lateinit var binding: FragmentTempEtcBinding
    lateinit var datePickerDialog: DatePickerDialog
    lateinit var timePickerDialog: TimePickerDialog
    lateinit var alarmManager: AlarmManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTempEtcBinding.bind(view)

        binding.apply {
            testDateButton.setOnClickListener() {
                testDate.setText(setAlarmDate())
            }

            testTimeButton.setOnClickListener {
                testTime.setText(setAlarmTime())
            }

            testShowAlarm.setOnClickListener {
                setupAlarms()
            }
            testShowNotification.setOnClickListener {
                createNotificationNow()
            }
        }
    }

    private fun createNotificationNow() {

        val mainActivIntent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(requireContext(), 0, mainActivIntent, 0)

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("ContentTitle")
            .setContentText("Sonam bewafa hai!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel Name hai shayad",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "are bhai kya bataoon mai itna dimag kharaab ho rakha hai.. chood."
            }

            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(requireContext())) {
            notify(777, builder.build())
        }


    }

    private fun setupAlarms() {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val alarmIntent = Intent(context, MyAlarmBroadcastReceiver::class.java).let { intent1 ->
            intent1.putExtra("key", "The answer to the universe")
            PendingIntent.getBroadcast(context, MY_REQUEST_CODE, intent1, 0)
        }

        Toast.makeText(
            requireContext(),
            "current " + Calendar.getInstance().timeInMillis,
            Toast.LENGTH_SHORT
        ).show()

        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + 5 * 1000,
            alarmIntent
        )
    }

    private fun setAlarmTime(): String {
        var timeIs = ""
        timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            timeIs = " $hourOfDay : $minute"

        }, 7, 7, false)
        timePickerDialog.show()
        return timeIs
    }

    private fun setAlarmDate(): String {
        var dateIs = ""
        datePickerDialog =
            DatePickerDialog(requireContext(), { datePicker, year, month, dayOfMonth ->
                dateIs = "$dayOfMonth/$month/$year"
            }, 1990, 8, 31)
        datePickerDialog.show()
        return dateIs
    }

    private fun popTheFrag() {
        findNavController().popBackStack()
    }

    private fun doTheDB(note: Note?) {
        var db = Firebase.firestore
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
                .add(Note("this", "is", "noUser", aa, true))
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