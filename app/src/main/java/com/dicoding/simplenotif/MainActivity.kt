package com.dicoding.simplenotif

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import com.dicoding.simplenotif.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

//        window.statusBarColor = Color.TRANSPARENT
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(binding?.root)

        val title = getString(R.string.notification_title)
        val message = getString(R.string.notification_message)

        binding?.BTSendNotification?.setOnClickListener{
            sendNotification(title, message)
        }

        // untuk versi android diatas Tiramisu
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

    }

    private fun sendNotification(title: String, message: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://dicoding.com"))
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        
        /*
            Pending Intent digunakan untuk memberikan action jika notifikasi disentuh.
            Parameter yang diperlukan untuk membuat Intent adalah Context, Request Code, Intent, dan Flag.
            Selain ke halaman Web, anda juga bisa mengganti intent tersebut ke Activity
            dengan menggunakan Intent untuk memanggil Activity seperti biasanya.
         */

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title) // Judul dari notifikasi (wajib ada).
            .setSmallIcon(R.drawable.ic_notification_active) // Ikon ini yang akan muncul pada status bar (wajib ada).
            .setContentText(message) // Text yang akan muncul di bawah judul notifikasi (wajib ada).
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Untuk menentukan tingkat kepentingan dari notifiksai yang ditampilkan
            .setSubText(getString(R.string.notification_subtext)) // Text ini yang akan muncul di bawah content text atau diatas sebelahan dengan nameApp
            .setContentIntent(pendingIntent) // untuk menambahkan Intent jika pada notifikassi status bar di tekan
            .setAutoCancel(true) // Digunakan untuk menghapus notifikasi setelah ditekan.

        // untuk versi android diatas Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        /* ------------------------------------------------------------ */

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
        /*
        Kode di atas dengan code pada line 70 digunakan untuk mengirim notifikasi sesuai dengan id yang
        kita berikan. Fungsi id di sini nanti juga bisa untuk membatalkan notifikasi yang sudah muncul.
         */
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}