package invest.megalo.controller.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.TextWatcher
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.chaos.view.PinView
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import invest.megalo.R
import invest.megalo.model.*
import org.json.JSONObject


class OtpVerification : AppCompatActivity() {
    private var formattedPhoneNumber = ""
    private var fullPhoneNumber = ""
    private var email = ""
    private var type = "sms"
    private var update = false
    private lateinit var pinView: PinView
    private lateinit var header: TextView
    private lateinit var errorMessage: TextView
    private lateinit var resend: TextView
    private lateinit var back: FrameLayout
    private lateinit var loader: CustomLoader
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val b = intent.extras
            if (b != null) {
                val otp = b.getString("otp")
                pinView.setText(otp)
            }
        }
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
            update = bundle.getBoolean("update")
        }

        header = findViewById(R.id.header)
        header.text = Html.fromHtml(
            getString(
                R.string.otp_verification_header_text, formattedPhoneNumber
            ), FROM_HTML_MODE_LEGACY
        )

        loader = CustomLoader(this)
        errorMessage = findViewById(R.id.error_message)
        pinView = findViewById(R.id.pin_view)
        pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                errorMessage.text = ""
                errorMessage.visibility = View.GONE
                pinView.setItemBackgroundColor(
                    ColorResCompat(this@OtpVerification, R.attr.lightGray_night).get()
                )
                if (s.toString().length >= 6) {
                    proceed()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        back = findViewById(R.id.back)
        back.setOnClickListener { finish() }

        resend = findViewById(R.id.resend)
        resend.setOnClickListener {
            if (InternetCheck(this, findViewById(R.id.parent)).status()) {
                loader.show(getString(R.string.resending_verification_code))
                val name = if (type == "sms") "phone_number" else "email"
                val value = if (type == "sms") fullPhoneNumber else email
                ServerConnection(
                    this,
                    "sendOtp",
                    Request.Method.POST,
                    "user/send_otp",
                    JSONObject().put("type", type).put(name, value).put("update", update)
                )
            }
        }
        resend.isEnabled = false
        resend.setTextColor(ContextCompat.getColor(this, R.color.darkGray))
        counter()

        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener {
            registerReceiver(broadcastReceiver, IntentFilter("passOtp"))
        }
        task.addOnFailureListener {
            it.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private fun counter(count: Int = 30) {
        var index = count
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
            index--
            Handler(Looper.getMainLooper()).postDelayed({ counter(index) }, 1000)
        }
    }

    fun proceed() {
        if (InternetCheck(this, findViewById(R.id.parent)).status()) {
            loader.show(getString(R.string.checking_verification_code))
            val name = if (type == "sms") "phone_number" else "email"
            val value = if (type == "sms") fullPhoneNumber else email
            ServerConnection(
                this,
                "verifyOtp",
                Request.Method.POST,
                "user/verify_otp",
                JSONObject().put("type", "sms").put(name, value).put("otp", pinView.text)
                    .put("update", update)
            )
        }
    }

    fun otpVerified(l: Int, userExists: Boolean, statusCode: Int? = 0) {
        if (l == 1) {
            if (!update) {
                if (userExists) {
                    loader.setMessage(getString(R.string.logging_you_in))
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
                    val intent = Intent(this, Registration::class.java)
                    intent.putExtra("phone_number", fullPhoneNumber)
                    startActivity(intent)
                }
            }
        } else {
            loader.dismiss()
            when (statusCode) {
                0 -> {
                    CustomSnackBar(
                        this@OtpVerification,
                        findViewById(R.id.parent),
                        getString(R.string.unusual_error_message),
                        "error"
                    )
                }
                in 400..499 -> {
                    when (statusCode) {
                        403 -> {
                            pinView.setItemBackground(
                                ContextCompat.getDrawable(
                                    this@OtpVerification,
                                    R.drawable.light_gray_night_solid_red_stroke_curved_corners
                                )
                            )
                            errorMessage.text = getString(R.string.incorrect_code_error_message)
                            errorMessage.visibility = View.VISIBLE
                        }
                        409 -> {
                            CustomSnackBar(
                                this@OtpVerification,
                                findViewById(R.id.parent),
                                getString(R.string.the_phone_number_you_provided_has_been_taken),
                                "error"
                            )
                        }
                        420 -> {
                            if (update) {
                                finish()
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                        }
                        else -> {
                            CustomSnackBar(
                                this@OtpVerification,
                                findViewById(R.id.parent),
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }
                }
                else -> {
                    CustomSnackBar(
                        this@OtpVerification,
                        findViewById(R.id.parent),
                        getString(R.string.server_error_message),
                        "error"
                    )
                }
            }
        }
    }

    fun loggedIn(l: Int, statusCode: Int? = 0) {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(true)
            startActivity(Intent(this, Home::class.java))
        } else {
            when (statusCode) {
                0 -> {
                    CustomSnackBar(
                        this@OtpVerification,
                        findViewById(R.id.parent),
                        getString(R.string.unusual_error_message),
                        "error"
                    )
                }
                in 400..499 -> {
                    CustomSnackBar(
                        this@OtpVerification,
                        findViewById(R.id.parent),
                        getString(R.string.client_error_message),
                        "error"
                    )
                }
                else -> {
                    CustomSnackBar(
                        this@OtpVerification,
                        findViewById(R.id.parent),
                        getString(R.string.server_error_message),
                        "error"
                    )
                }
            }
        }
    }

    fun otpSent(l: Int, statusCode: Int? = 0) {
        loader.dismiss()
        if (l == 1) {
            CustomSnackBar(
                this@OtpVerification,
                findViewById(R.id.parent),
                getString(R.string.code_sent_message),
                "success"
            )
            resend.isEnabled = false
            resend.setTextColor(ContextCompat.getColor(this, R.color.darkGray))
            counter()
        } else {
            when (statusCode) {
                0 -> {
                    CustomSnackBar(
                        this@OtpVerification,
                        findViewById(R.id.parent),
                        getString(R.string.unusual_error_message),
                        "error"
                    )
                }
                in 400..499 -> {
                    if (statusCode == 420 && update) {
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        CustomSnackBar(
                            this@OtpVerification,
                            findViewById(R.id.parent),
                            getString(R.string.client_error_message),
                            "error"
                        )
                    }
                }
                else -> {
                    CustomSnackBar(
                        this@OtpVerification,
                        findViewById(R.id.parent),
                        getString(R.string.server_error_message),
                        "error"
                    )
                }
            }
        }
    }
}