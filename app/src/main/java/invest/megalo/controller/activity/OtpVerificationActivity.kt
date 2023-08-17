package invest.megalo.controller.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.android.volley.Request
import com.chaos.view.PinView
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import invest.megalo.R
import invest.megalo.model.CustomLoader
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.KeyStore
import invest.megalo.model.ServerConnection
import invest.megalo.model.Session
import invest.megalo.model.SetAppTheme
import org.json.JSONObject


class OtpVerificationActivity : AppCompatActivity() {
    private var formattedPhoneNumber = ""
    private var fullPhoneNumber = ""
    private var email = ""
    private var origin = ""
    private var type = "sms"
    private var update = false
    private var count = 30
    private lateinit var handler: Handler
    private lateinit var pinView: PinView
    private lateinit var back: ImageView
    private lateinit var description: TextView
    private lateinit var heading: TextView
    private lateinit var errorMessage: TextView
    private lateinit var resend: TextView
    private lateinit var title: TextView
    private lateinit var loader: CustomLoader
    private lateinit var parent: LinearLayout
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val b = intent.extras
            if (b != null) {
                val otp = b.getString("otp")
                pinView.setText(otp)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        SetAppTheme(this)
        super.onConfigurationChanged(newConfig)
        val config = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (config == Configuration.UI_MODE_NIGHT_YES) {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                false
        } else if (config == Configuration.UI_MODE_NIGHT_NO) {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                true
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.white_black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white_black)

        parent.setBackgroundColor(ContextCompat.getColor(this, R.color.white_black))

        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        back.contentDescription = getString(R.string.go_back)
        back.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        back.setImageResource(R.drawable.arrow_back)

        heading.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        heading.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        description.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        description.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        if (update || type == "email") {
            title.text = if (type == "email") getString(R.string.verify_email) else getString(
                R.string.verify_phone_number
            )
            heading.text = Html.fromHtml(
                getString(
                    R.string.otp_verification_header_text,
                    if (type == "sms") formattedPhoneNumber else email
                ), FROM_HTML_MODE_LEGACY
            )
        } else {
            heading.text = if (type == "sms") getString(
                R.string.verify_your_phone_number
            ) else getString(R.string.verify_your_email_address)
            description.text = Html.fromHtml(
                getString(
                    R.string.otp_verification_header_text,
                    if (type == "sms") formattedPhoneNumber else email
                ), FROM_HTML_MODE_LEGACY
            )
        }

        if (count == 0) {
            resend.text = resources.getString(R.string.resend_code)
            resend.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
        } else {
            if (count == 1) {
                resend.text = Html.fromHtml(
                    getString(
                        R.string.resend_code_single_counter_text, count.toString()
                    ), FROM_HTML_MODE_LEGACY
                )
            } else {
                resend.text = Html.fromHtml(
                    getString(
                        R.string.resend_code_multiple_counter_text, count.toString()
                    ), FROM_HTML_MODE_LEGACY
                )
            }
            resend.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        }
        resend.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        pinView.setItemBackground(
            ContextCompat.getDrawable(
                this, R.color.white_black
            )
        )
        pinView.text = pinView.text
        pinView.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        pinView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )
        if (errorMessage.isVisible) {
            pinView.setLineColor(
                ContextCompat.getColor(
                    this, R.color.darkRed_lightRed
                )
            )
            errorMessage.text = getString(R.string.incorrect_code_error_message)
        } else {
            pinView.setLineColor(
                ContextCompat.getColorStateList(
                    this@OtpVerificationActivity, R.color.otp_edit_text_selector
                )
            )
        }
        errorMessage.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        errorMessage.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        loader.onConfigurationChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("formatted_phone_number") != null) {
                formattedPhoneNumber = bundle.getString("formatted_phone_number").toString()
            }
            if (bundle.getString("full_phone_number") != null) {
                fullPhoneNumber = bundle.getString("full_phone_number").toString()
            }
            if (bundle.getString("type") != null) {
                type = bundle.getString("type").toString()
            }
            if (bundle.getString("email") != null) {
                email = bundle.getString("email").toString()
            }
            if (bundle.getString("origin") != null) {
                origin = bundle.getString("origin").toString()
            }
            update = bundle.getBoolean("update")
        }

        parent = findViewById(R.id.parent)
        title = findViewById(R.id.title)
        heading = findViewById(R.id.heading)
        description = findViewById(R.id.description)

        if (update || type == "email") {
            findViewById<ImageView>(R.id.logo).visibility = View.GONE
            title.text = if (type == "email") getString(R.string.verify_email) else getString(
                R.string.verify_phone_number
            )
            description.visibility = View.GONE

            heading.text = Html.fromHtml(
                getString(
                    R.string.otp_verification_header_text,
                    if (type == "sms") formattedPhoneNumber else email
                ), FROM_HTML_MODE_LEGACY
            )
        } else {
            heading.text = if (type == "sms") getString(
                R.string.verify_your_phone_number
            ) else getString(R.string.verify_your_email_address)

            description.text = Html.fromHtml(
                getString(
                    R.string.otp_verification_header_text,
                    if (type == "sms") formattedPhoneNumber else email
                ), FROM_HTML_MODE_LEGACY
            )
        }

        loader = CustomLoader(this)
        errorMessage = findViewById(R.id.error_message)
        pinView = findViewById(R.id.pin_view)
        pinView.setAnimationEnable(true)
        pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (errorMessage.isVisible) {
                    errorMessage.text = ""
                    errorMessage.visibility = View.GONE
                    pinView.setLineColor(
                        ContextCompat.getColorStateList(
                            this@OtpVerificationActivity, R.color.otp_edit_text_selector
                        )
                    )
                }
                if (s.toString().length >= 6) {
                    if (pinView.text.toString() == "000000") {
                        pinView.setLineColor(
                            ContextCompat.getColor(
                                this@OtpVerificationActivity, R.color.darkRed_lightRed
                            )
                        )
                        errorMessage.text = getString(R.string.incorrect_code_error_message)
                        errorMessage.visibility = View.VISIBLE
                    } else {
                        proceed()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        pinView.requestFocus()

        back = findViewById(R.id.back)
        back.visibility = View.VISIBLE
        back.setOnClickListener { finish() }

        handler = Handler(Looper.getMainLooper())

        resend = findViewById(R.id.resend)
        resend.setOnClickListener {
            if (InternetCheck(this, parent).status()) {
                loader.show(R.string.resending_verification_code)
                val key = if (type == "sms") "phone_number" else "email"
                val value = if (type == "sms") fullPhoneNumber else email
                val jsonObject = if (type == "sms") JSONObject().put("type", type).put(key, value)
                    .put("update", update) else JSONObject().put("type", type).put(key, value)
                ServerConnection(
                    this, "sendOtp", Request.Method.POST, "user/send_otp", jsonObject
                )
            }
        }

        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener {
            ContextCompat.registerReceiver(
                this, broadcastReceiver, IntentFilter("passOtp"), ContextCompat.RECEIVER_EXPORTED
            )
        }
        task.addOnFailureListener {
            it.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacksAndMessages(null)
        counter()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        handler.removeCallbacksAndMessages(null)
    }

    override fun onRestart() {
        super.onRestart()
        handler.removeCallbacksAndMessages(null)
        counter()
    }

    private fun counter() {
        if (count == 0) {
            resend.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
            resend.text = resources.getString(R.string.resend_code)
            resend.isEnabled = true
        } else {
            if (count == 1) {
                resend.text = Html.fromHtml(
                    getString(
                        R.string.resend_code_single_counter_text, count.toString()
                    ), FROM_HTML_MODE_LEGACY
                )
            } else {
                resend.text = Html.fromHtml(
                    getString(
                        R.string.resend_code_multiple_counter_text, count.toString()
                    ), FROM_HTML_MODE_LEGACY
                )
            }
            count--
            handler.postDelayed({ counter() }, 1000)
        }
    }

    fun proceed() {
        if (InternetCheck(this, parent).status()) {
            loader.show(R.string.checking_verification_code)
            val key = if (type == "sms") "phone_number" else "email"
            val value = if (type == "sms") fullPhoneNumber else email
            val jsonObject = if (type == "sms") JSONObject().put("type", type).put(key, value)
                .put("otp", pinView.text).put("update", update) else JSONObject().put("type", type)
                .put(key, value).put("otp", pinView.text)

            ServerConnection(
                this, "verifyOtp", Request.Method.POST, "user/verify_otp", jsonObject
            )
        }
    }

    fun otpVerified(
        l: Int, statusCode: Int? = 0, message: String = "", jsonObject: JSONObject = JSONObject()
    ) {
        if (l == 1) {
            if (update || type == "email") {
                loader.dismiss()
                if (origin == "home") {
                    val intent = Intent(this, HomeActivity::class.java)
                    setResult(1, intent)
                } else {
                    val intent = Intent(this, UpdateDataActivity::class.java)
                    setResult(3, intent)
                }
                finish()
            } else {
                KeyStore(this).encryptData(
                    jsonObject.getJSONObject("data").getString("token")
                )
                if (jsonObject.getJSONObject("data").getBoolean("user_exists")) {
                    loader.setMessage(R.string.logging_you_in)
                    ServerConnection(
                        this,
                        "logIn",
                        Request.Method.POST,
                        "login/create",
                        JSONObject().put("phone_number", fullPhoneNumber)
                    )
                } else {
                    loader.dismiss()
                    finish()
                    val intent = Intent(this, RegistrationActivity::class.java)
                    intent.putExtra("phone_number", fullPhoneNumber)
                    startActivity(intent)
                }
            }
        } else {
            loader.dismiss()
            if (message != "") {
                CustomSnackBar(
                    this@OtpVerificationActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@OtpVerificationActivity,
                            parent,
                            getString(R.string.unusual_error_message),
                            "error"
                        )
                    }

                    in 400..499 -> {
                        when (statusCode) {
                            403 -> {
                                pinView.setLineColor(
                                    ContextCompat.getColor(
                                        this, R.color.darkRed_lightRed
                                    )
                                )
                                errorMessage.text = getString(R.string.incorrect_code_error_message)
                                errorMessage.visibility = View.VISIBLE
                            }

                            420 -> {
                                if (update || type == "email") {
                                    finish()
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                            }

                            else -> {
                                CustomSnackBar(
                                    this@OtpVerificationActivity,
                                    parent,
                                    getString(R.string.client_error_message),
                                    "error"
                                )
                            }
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@OtpVerificationActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }

    fun loggedIn(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(true)
            Session(this).devicePhoneNumber(fullPhoneNumber)
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@OtpVerificationActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@OtpVerificationActivity,
                            parent,
                            getString(R.string.unusual_error_message),
                            "error"
                        )
                    }

                    in 400..499 -> {
                        CustomSnackBar(
                            this@OtpVerificationActivity,
                            parent,
                            getString(R.string.client_error_message),
                            "error"
                        )
                    }

                    else -> {
                        CustomSnackBar(
                            this@OtpVerificationActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }

    fun otpSent(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            CustomSnackBar(
                this@OtpVerificationActivity,
                parent,
                getString(R.string.code_sent_message),
                "success"
            )
            resend.isEnabled = false
            resend.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
            count = 30
            counter()
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@OtpVerificationActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@OtpVerificationActivity,
                            parent,
                            getString(R.string.unusual_error_message),
                            "error"
                        )
                    }

                    in 400..499 -> {
                        if (statusCode == 420 && update || statusCode == 420 && type == "email") {
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            CustomSnackBar(
                                this@OtpVerificationActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@OtpVerificationActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }
}