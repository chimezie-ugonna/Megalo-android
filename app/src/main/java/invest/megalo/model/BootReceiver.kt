package invest.megalo.model

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val scheduler: JobScheduler =
                context?.getSystemService(AppCompatActivity.JOB_SCHEDULER_SERVICE) as JobScheduler
            if (scheduler.getPendingJob(1) == null) {
                scheduler.schedule(
                    JobInfo.Builder(
                        1, ComponentName(context, DeviceTokenUpdatingService::class.java)
                    ).setPeriodic(7 * 24 * 60 * 60 * 1000L).setPersisted(true).build()
                )
            }
        }
    }
}