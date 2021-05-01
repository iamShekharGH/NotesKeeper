package com.iamshekhargh.myapplication.broadcasts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.iamshekhargh.myapplication.R
import com.iamshekhargh.myapplication.ui.MainActivity
import com.iamshekhargh.myapplication.utils.NotificationKeys

/**
 * Created by <<-- iamShekharGH -->>
 * on 28 April 2021
 * at 8:27 PM.
 */
class MyAlarmBroadcastReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "kuch To Log Kahenge"

    override fun onReceive(context: Context?, intent: Intent?) {

//        makeTestToast(context, intent?.getStringExtra("key").toString())

        val builder = getNotificationBuilder(
            context!!,
            intent?.getStringExtra(NotificationKeys.HEADING.name) ?: "Note",
            intent?.getStringExtra(NotificationKeys.TIME.name) ?: "",
            intent?.getStringExtra(NotificationKeys.DESCRIPTION.name) ?: "You have a reminder!",
            intent?.getStringExtra(NotificationKeys.NOTE_OBJECT.name) ?: "{}",
            )

        setupNotificationChannel(context)

        // ideally id set the note primary key as id here. but using 777 instead coz.
        with(NotificationManagerCompat.from(context)) {
            notify(777, builder!!.build())
        }
    }

    private fun getActivityIntent(context: Context?): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    private fun getPendingIntent(context: Context, noteStr: String): PendingIntent? {
        val bundle = Bundle()
        bundle.putString(NotificationKeys.NOTE_OBJECT.name, noteStr)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.fragmentAddNote)
            .setArguments(bundle)
            .createPendingIntent()

        return pendingIntent
//            .setArguments() // Idher add note
//        return PendingIntent.getActivity(context, 0, getActivityIntent(context), 0)
    }

    private fun setupNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel Name hai, shayad",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "are bhai kya bataoon mai itna dimag kharaab ho rakha hai.. chood."
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationBuilder(
        context: Context,
        title: String,
        text: String,
        description: String,
        noteStr: String,
    ): NotificationCompat.Builder? {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent(context, noteStr))
            .setAutoCancel(true)
    }

    private fun makeTestToast(context: Context?, y: String?) {
        if (context != null)
            Toast.makeText(
                context,
                "Broadcast received kaam kar ra. stringExtre->$y",
                Toast.LENGTH_LONG
            ).show()
    }
}