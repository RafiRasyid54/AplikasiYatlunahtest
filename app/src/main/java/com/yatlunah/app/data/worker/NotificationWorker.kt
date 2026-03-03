package com.yatlunah.app.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yatlunah.app.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // Robot mulai bekerja memunculkan notifikasi
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val channelId = "yatlunah_reminder"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Wajib buat Channel untuk Android 8.0 ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pengingat Bimbingan",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Rakit isi notifikasinya
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Waktunya Mengaji! 📖")
            .setContentText("Rafi, bimbingan Jilid 1-3 akan segera dimulai. Siapkan Iqra-mu ya!")
            .setSmallIcon(R.drawable.logo_yatlunah) // 👈 Pastikan logo_yatlunah ada di folder 'res/drawable'
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}