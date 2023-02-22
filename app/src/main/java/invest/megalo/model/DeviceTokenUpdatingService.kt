package invest.megalo.model

import android.app.job.JobParameters
import android.app.job.JobService
import com.android.volley.Request
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

class DeviceTokenUpdatingService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful && Session(this).loggedIn()) {
                ServerConnection(
                    this,
                    "updateDeviceToken",
                    Request.Method.PUT,
                    "login/update_device_token",
                    JSONObject().put("device_token", task.result)
                )
            }
        }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}