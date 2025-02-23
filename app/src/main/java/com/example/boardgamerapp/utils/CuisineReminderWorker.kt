package com.example.boardgamerapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.boardgamerapp.R

class CuisineReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = inputData.getString("userId") ?: return Result.failure()
        val userName = inputData.getString("userName") ?: "SpielerIn"

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "cuisine_channel",
                "Essens-Erinnerungen",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, "cuisine_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Lieblingsessensrichtung wählen")
            .setContentText("$userName, wähle jetzt deine Lieblingsessensrichtung!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1001, builder.build())

        return Result.success()
    }
}
