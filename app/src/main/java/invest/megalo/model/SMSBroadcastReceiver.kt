package invest.megalo.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import invest.megalo.R
import java.util.regex.Pattern


class SMSBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    val pattern = Pattern.compile("\\d{6}")
                    val matcher = pattern.matcher(message)
                    if (matcher.find()) {
                        val i = Intent("passOtp")
                        i.putExtra("otp", matcher.group(0))
                        context?.sendBroadcast(i)
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    println(context?.getString(R.string.sms_retriever_timed_out))
                }
            }
        }
    }
}