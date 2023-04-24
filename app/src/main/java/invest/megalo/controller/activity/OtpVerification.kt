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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.volley.Request
import com.chaos.view.PinView
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import invest.megalo.R
import invest.megalo.model.ColorResCompat
import invest.megalo.model.CustomLoader
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.Session
import invest.megalo.model.SetAppTheme
import org.json.JSONObject


class OtpVerification : AppCompatActivity() {
    private var formattedPhoneNumber = ""
    private var fullPhoneNumber = ""
    private var email = ""
    private var type = "sms"
    private var update = false
    private var count = 30
    private lateinit var handler: Handler
    private lateinit var pinView: PinView
    private lateinit var back: ImageView
    private lateinit var description: TextView
    private lateinit var errorMessage: TextView
    private lateinit var resend: TextView
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

        description = findViewById(R.id.description)
        description.text = Html.fromHtml(
            getString(
                R.string.otp_verification_header_text, formattedPhoneNumber
            ), FROM_HTML_MODE_LEGACY
        )

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
                            this@OtpVerification, R.color.otp_edit_text_selector
                        )
                    )
                }
                if (s.toString().length >= 6) {
                    proceed()
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
        resend.setTextColor(
            ColorResCompat(
                this@OtpVerification, R.attr.darkGrey_lightGrey
            ).get()
        )

        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener {
            registerReceiver(broadcastReceiver, IntentFilter("passOtp"))
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
            resend.setTextColor(ContextCompat.getColor(this, R.color.app_green))
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

    fun otpVerified(l: Int, userExists: Boolean, statusCode: Int? = 0, message: String = "") {
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
            if (message != "") {
                CustomSnackBar(
                    this@OtpVerification, findViewById(R.id.parent), message, "error"
                )
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
                        when (statusCode) {
                            403 -> {
                                pinView.setLineColor(
                                    ColorResCompat(
                                        this, R.attr.darkRed_lightRed
                                    ).get()
                                )
                                errorMessage.text = getString(R.string.incorrect_code_error_message)
                                errorMessage.visibility = View.VISIBLE
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
    }

    fun loggedIn(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(true)
            Session(this).devicePhoneNumber(fullPhoneNumber)
            startActivity(Intent(this, Home::class.java))
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@OtpVerification, findViewById(R.id.parent), message, "error"
                )
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
    }

    fun otpSent(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            CustomSnackBar(
                this@OtpVerification,
                findViewById(R.id.parent),
                getString(R.string.code_sent_message),
                "success"
            )
            resend.isEnabled = false
            resend.setTextColor(
                ColorResCompat(
                    this@OtpVerification, R.attr.darkGrey_lightGrey
                ).get()
            )
            count = 30
            counter()
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@OtpVerification, findViewById(R.id.parent), message, "error"
                )
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
}