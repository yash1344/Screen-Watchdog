package com.example.jobnoti

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseListenerService : Service() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var valueEventListner: ValueEventListener
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "running_channel"

    override fun onCreate() {
        super.onCreate()
//        createNotificationChannel()
        startForeground(500, createNotification())

        // Initialize MediaPlayer with your .mp3 file
        mediaPlayer = MediaPlayer.create(this, R.raw.bhag_bhag)

        // Initialize Firebase Database reference
        databaseReference = Firebase.database.getReference("FLINK")

        // Start listening to Firebase changes
        startListeningToFirebase()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_SERVICE") {
            stopSelf() // Stop the service when the action is triggered
        }
        return START_STICKY
    }

    private fun startListeningToFirebase() {
        valueEventListner = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseListener", "Data changed: ${snapshot.value}")
                if (snapshot.value == true) {
//                    createNotification()
                    mediaPlayer?.start()
                } else if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseListener", "Error: ${error.message}")
            }
        }
        databaseReference.addValueEventListener(valueEventListner)
    }

    private fun createNotification(): Notification {
        // Intent to stop the service
        val stopIntent = Intent(this, FirebaseListenerService::class.java).apply {
            action = "STOP_SERVICE"
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

//      Create an icon using IconCompat for the button
        val stopIcon = IconCompat.createWithResource(this, android.R.drawable.ic_delete)

        // Use Notification.Action.Builder with IconCompat instead of a drawable resource ID
        val stopAction = NotificationCompat.Action.Builder(
            stopIcon, // Use IconCompat for the icon
            "Stop Service", // Button Text
            stopPendingIntent
        ).build()

        val noti = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle("Listening to Firebase")
            .setContentText("The service is running in the background.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(stopAction) // Add the stop action
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        noti.flags = Notification.FLAG_ONGOING_EVENT
        return noti
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release resources to avoid memory leaks
        mediaPlayer?.release()
        mediaPlayer = null
        databaseReference.setValue(false)
        databaseReference.removeEventListener(valueEventListner)
        Log.d("FirebaseListener", "Service destroyed.")
    }
}