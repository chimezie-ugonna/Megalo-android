package invest.megalo.model

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import invest.megalo.R
import invest.megalo.controller.activity.MainActivity
import org.json.JSONObject
import java.util.*


class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        if (Session(this).loggedIn()) {
            ServerConnection(
                this,
                "updateDeviceToken",
                Request.Method.PUT,
                "login/update_device_token",
                JSONObject().put("device_token", p0)
            )
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val pattern = longArrayOf(500, 500, 500, 500, 500)

        val sound = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.notifications
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            Random().nextInt(),
            NotificationCompat.Builder(this, "megalo_general_channel_id")
                .setSmallIcon(R.drawable.notification_icon).setContentTitle(message.data["title"])
                .setContentText(message.data["body"]).setAutoCancel(true).setVibrate(pattern)
                .setLights(Color.BLUE, 1, 1).setSound(sound).setContentIntent(pendingIntent).build()
        )
        super.onMessageReceived(message)
    }
}