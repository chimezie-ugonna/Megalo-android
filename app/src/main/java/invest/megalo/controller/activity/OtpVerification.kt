package invest.megalo.controller.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.model.*
import org.json.JSONObject


class OtpVerification : AppCompatActivity() {
    private var formattedPhoneNumber = ""
    private var fullPhoneNumber = ""
    private lateinit var header: TextView
    private lateinit var e1: EditText
    private lateinit var e2: EditText
    private lateinit var e3: EditText
    private lateinit var e4: EditText
    private lateinit var e5: EditText
    private lateinit var e6: EditText
    private lateinit var errorMessage: TextView
    private lateinit var resend: TextView
    private lateinit var back: FrameLayout
    private lateinit var loader: CustomLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        val bundle = intent.extras
        formattedPhoneNumber = bundle?.get("formatted_phone_number").toString()
        fullPhoneNumber = bundle?.get("full_phone_number").toString()

        header = findViewById(R.id.header)
        header.text = getString(R.string.otp_verification_header_text, formattedPhoneNumber)

        loader = CustomLoader(this)
        errorMessage = findViewById(R.id.error_message)
        e1 = findViewById(R.id.e1)
        e2 = findViewById(R.id.e2)
        e3 = findViewById(R.id.e3)
        e4 = findViewById(R.id.e4)
        e5 = findViewById(R.id.e5)
        e6 = findViewById(R.id.e6)

        e1.disableCopyPaste()
        e2.disableCopyPaste()
        e3.disableCopyPaste()
        e4.disableCopyPaste()
        e5.disableCopyPaste()
        e6.disableCopyPaste()

        e1.addTextChangedListener(GenericTextWatcher(this, e1, e2, e3, e4, e5, e6, e1, e2))
        e2.addTextChangedListener(GenericTextWatcher(this, e1, e2, e3, e4, e5, e6, e1, e3))
        e3.addTextChangedListener(GenericTextWatcher(this, e1, e2, e3, e4, e5, e6, e2, e4))
        e4.addTextChangedListener(GenericTextWatcher(this, e1, e2, e3, e4, e5, e6, e3, e5))
        e5.addTextChangedListener(GenericTextWatcher(this, e1, e2, e3, e4, e5, e6, e4, e6))
        e6.addTextChangedListener(GenericTextWatcher(this, e1, e2, e3, e4, e5, e6, e5, e6))

        e1.setOnKeyListener(GenericKeyListener(e1, e1, e2))
        e2.setOnKeyListener(GenericKeyListener(e1, e2, e3))
        e3.setOnKeyListener(GenericKeyListener(e2, e3, e4))
        e4.setOnKeyListener(GenericKeyListener(e3, e4, e5))
        e5.setOnKeyListener(GenericKeyListener(e4, e5, e6))
        e6.setOnKeyListener(GenericKeyListener(e5, e6, e6))

        e1.setOnFocusChangeListener { _, _ ->
            resetFields()
        }
        e2.setOnFocusChangeListener { _, _ ->
            resetFields()
        }
        e3.setOnFocusChangeListener { _, _ ->
            resetFields()
        }
        e4.setOnFocusChangeListener { _, _ ->
            resetFields()
        }
        e5.setOnFocusChangeListener { _, _ ->
            resetFields()
        }
        e6.setOnFocusChangeListener { _, _ ->
            resetFields()
        }

        e1.requestFocus()
        resetFields()

        back = findViewById(R.id.back)
        back.setOnClickListener { finish() }

        resend = findViewById(R.id.resend)
        resend.setOnClickListener {
            if (InternetCheck(this, findViewById(R.id.parent)).status()) {
                loader.show(getString(R.string.resending_verification_code))
                ServerConnection(
                    this, "sendOtp", Request.Method.POST, "user/send_otp",
                    JSONObject().put("phone_number", fullPhoneNumber)
                )
            }
        }
        resend.isEnabled = false
        resend.setTextColor(ContextCompat.getColor(this, R.color.darkGray))
        counter(30)
    }

    private fun counter(count: Int) {
        var index = count
        if (count == 0) {
            resend.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
            resend.text = resources.getString(R.string.resend_code)
            resend.isEnabled = true
        } else {
            if (count == 1) {
                resend.text = Html.fromHtml(
                    getString(
                        R.string.resend_code_single_counter_text,
                        count.toString()
                    ), FROM_HTML_MODE_LEGACY
                )
            } else {
                resend.text = Html.fromHtml(
                    getString(
                        R.string.resend_code_multiple_counter_text,
                        count.toString()
                    ), FROM_HTML_MODE_LEGACY
                )
            }
            index--
            Handler(Looper.getMainLooper()).postDelayed({ counter(index) }, 1000)
        }
    }

    private fun resetFields() {
        if (e1.hasFocus()) {
            e1.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
        } else {
            e1.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
        }
        if (e2.hasFocus()) {
            e2.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
        } else {
            e2.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
        }
        if (e3.hasFocus()) {
            e3.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
        } else {
            e3.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
        }
        if (e4.hasFocus()) {
            e4.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
        } else {
            e4.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
        }
        if (e5.hasFocus()) {
            e5.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
        } else {
            e5.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
        }
        if (e6.hasFocus()) {
            e6.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
        } else {
            e6.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
        }
        errorMessage.visibility = View.GONE
    }

    fun proceed() {
        if (InternetCheck(this, findViewById(R.id.parent)).status()) {
            loader.show(getString(R.string.checking_verification_code))
            ServerConnection(
                this, "verifyOtp", Request.Method.POST, "user/verify_otp",
                JSONObject().put("phone_number", fullPhoneNumber)
                    .put("otp", "${e1.text}${e2.text}${e3.text}${e4.text}${e5.text}${e6.text}")
            )
        }
    }

    fun otpVerified(l: Int, userExists: Boolean) {
        if (l == 1) {
            if (userExists) {
                loader.setMessage(getString(R.string.logging_you_in))
                ServerConnection(
                    this, "logIn", Request.Method.POST, "login/create",
                    JSONObject().put("phone_number", fullPhoneNumber)
                )
            } else {
                loader.dismiss()
                finish()
                val intent = Intent(this, Registration::class.java)
                intent.putExtra("phone_number", fullPhoneNumber)
                startActivity(intent)
            }
        } else {
            loader.dismiss()
            e1.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
            e2.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
            e3.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
            e4.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
            e5.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
            e6.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
            errorMessage.text = getString(R.string.incorrect_code_error_message)
            errorMessage.visibility = View.VISIBLE
        }
    }

    fun loggedIn(l: Int) {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(true)
            startActivity(Intent(this, Home::class.java))
        } else {
            CustomSnackBar(
                this@OtpVerification,
                findViewById(R.id.parent),
                resources.getString(R.string.server_error_message),
                "error"
            )
        }
    }

    fun otpSent(l: Int) {
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
            counter(30)
        } else {
            CustomSnackBar(
                this@OtpVerification,
                findViewById(R.id.parent),
                resources.getString(R.string.server_error_message),
                "error"
            )
        }
    }

    private fun TextView.disableCopyPaste() {
        isLongClickable = false
        setTextIsSelectable(false)
        customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {}
        }
    }
}