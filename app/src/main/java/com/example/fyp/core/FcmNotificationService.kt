package com.example.fyp.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fyp.MainActivity
import com.example.fyp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Handles incoming FCM push notifications for the app.
 *
 * Responsibilities:
 * 1. `onNewToken` — store the refreshed FCM registration token in Firestore so
 *    the Cloud Functions backend can reach this device.
 * 2. `onMessageReceived` — show a system notification for data messages
 *    (e.g., new chat messages) received while the app is in the background or foreground.
 *
 * Token storage path: users/{userId}/fcm_tokens/{tokenId}
 * (covered by the existing `match /users/{userId}/{document=**}` read/write rule)
 */
class FcmNotificationService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID_CHAT = "chat_messages"
        const val CHANNEL_NAME_CHAT = "Chat Messages"
        const val CHANNEL_DESC_CHAT = "Notifications for new chat messages from friends"

        /**
         * Upload the current FCM token to Firestore for the signed-in user.
         * Call this once at login and whenever `onNewToken` fires.
         */
        fun uploadTokenIfLoggedIn(context: Context) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    if (token.isNullOrBlank()) return@addOnSuccessListener
                    FirebaseFirestore.getInstance()
                        .collection("users").document(uid)
                        .collection("fcm_tokens").document(token)
                        .set(mapOf(
                            "token" to token,
                            "platform" to "android",
                            "updatedAt" to com.google.firebase.Timestamp.now()
                        ))
                        .addOnFailureListener { e ->
                            AppLogger.e("FcmService", "Failed to upload FCM token", e)
                        }
                }
                .addOnFailureListener { e ->
                    AppLogger.e("FcmService", "Failed to get FCM token", e)
                }
        }

        /** Create the notification channel (call from Application.onCreate). */
        fun createNotificationChannels(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID_CHAT,
                    CHANNEL_NAME_CHAT,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC_CHAT
                    enableVibration(true)
                }
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
                manager.createNotificationChannel(channel)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        AppLogger.d("FcmService", "FCM token refreshed")
        uploadTokenIfLoggedIn(applicationContext)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val type = data["type"] ?: return

        when (type) {
            "new_message" -> showChatNotification(
                senderUsername = data["senderUsername"] ?: "Friend",
                messagePreview = data["messagePreview"] ?: "Sent you a message",
                friendId = data["senderId"] ?: ""
            )
            "friend_request" -> showFriendRequestNotification(
                senderUsername = data["senderUsername"] ?: "Someone"
            )
            "request_accepted" -> showRequestAcceptedNotification(
                friendUsername = data["friendUsername"] ?: "Someone"
            )
        }
    }

    private fun showChatNotification(senderUsername: String, messagePreview: String, friendId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_chat_friend_id", friendId)
            putExtra("open_chat_friend_username", senderUsername)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, friendId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_CHAT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(senderUsername)
            .setContentText(messagePreview)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Use friendId hashcode as notification ID so multiple messages from the
        // same friend update the same notification instead of stacking.
        manager.notify(friendId.hashCode(), notification)
    }

    private fun showFriendRequestNotification(senderUsername: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_friends", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, "friend_request".hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_CHAT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("New Friend Request")
            .setContentText("$senderUsername sent you a friend request")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify("friend_req_$senderUsername".hashCode(), notification)
    }

    private fun showRequestAcceptedNotification(friendUsername: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_friends", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, "req_accepted".hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_CHAT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Friend Request Accepted")
            .setContentText("$friendUsername accepted your friend request!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify("req_accepted_$friendUsername".hashCode(), notification)
    }
}
